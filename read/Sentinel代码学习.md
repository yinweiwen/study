Sentinel

## 概述

每次资源调用创建Entry，同时创建一系列slot chain(功能插槽)。分：

1. NodeSelectorSlot 资源调用路径收集
2. ClusterBuilderSlot 资源统计信息 RT/QPS/Threads
3. StatisticsSlot 记录统计不同维度的runtime指标
4. FlowSlot 根据预设的限流规则以及前面slot统计的状态，进行流量控制
5. AuthoritySlot 黑白名单和调用来源控制
6. DegradeSlot 熔断降级
7. SystemSlot 系统状态(如load1)进行入口流量控制

## 核心类解析

### Basic

#### Metric

`Metric`接口。窗口指标统计类

```java
    long success();
    long maxSuccess();
    long exception();
    long block();
    long pass();
    long rt(); // total response time
    long minRt();
    List<MetricNode> details(); // Get aggregated metric nodes of all resources
    List<MetricNode> detailsOnCondition(Predicate<Long> timePredicate); // 时间断言范围内的聚集metric node

    MetricBucket[] windows(); // raw window array

    void addException(int n);
    void addBlock(int n);
    void addSuccess(int n);
    void addPass(int n);
    void addRT(long rt);
    double getWindowIntervalInSec();
    int getSampleCount();
    long getWindowPass(long timeMillis);
    void addOccupiedPass(int acquireCount); // Add occupied pass, which represents pass requests that borrow the latter windows' token.
    void addWaiting(long futureTime, int acquireCount);
    long waiting();
    long occupiedPass();

    // Tool methods.

    long previousWindowBlock();

    long previousWindowPass();
```



`MetricBucket`

统计各个MetricEvent (PASS/BLOCK/EXCEPTION/SUCCESS)的窗口数据。(一段时间范围的指标数据)

#### Context

保存当前调用的元数据

EntranceNode 当前调用树的根节点

curEntry 当前调用点

origin 原始调用者(一般是消费服务的app名称或调用者的ip)

所有的SphU.entry 或 SphO.entry都应该在某个context下。如果不指定将使用默认的context (通过ContextUtil.enter指定)

#### Node

资源的实时统计数据。 total/block/pass/Qps/Rt

Node > StatisticNode > DefaultNode > +ClusterBuilderSlot

#### Entry

资源调用信息。

curNode 当前节点

originNode 调用者节点

createTimestamp 创建时间
completeTimestamp 结束时间

```:
ResourceWrapper类：
包含 name EntryType (In,Out)  resourceType
```

```
继承
CtEntry -- linked entry
AsyncEntry -- the entry for asynchronous resources
```

### LeapArray

Sentinel 底层采用高性能的滑动窗口数据结构 `LeapArray` 来统计实时的秒级指标数据，可以很好地支撑写多于读的高并发场景。

```java
sampleCount = intervalInMs总时长 / windowLengthInMs窗口(桶)时长
```

> ```
> BucketLeapArray
> 
> FutureBucketLeapArray
> 
> OccupiableBucketLeapArray
> ```



### ProcessorSlotChain

Sentinel 的核心骨架，将不同的 Slot 按照顺序串在一起（责任链模式），从而将不同的功能（限流、降级、系统保护）组合在一起。slot chain 其实可以分为两部分：统计数据构建部分（statistic）和判断部分（rule checking）。

### ProcessorSlot

处理器插槽。存放处理完成时候的通知方法。 fireEntry、fireExit

### FlowSlot

结合前置slots的运行时状态采集数据(NodeSelectorSlot, ClusterNodeBuilderSlot, and StatisticSlot)，FlowSlot负责使用预先设置的规则判断请求是否被阻挡(blocked)

```
void entry(){
	FlowRuleChecker.checkFlow
    ...
    rule.getRater().canPass(selectedNode, acquireCount, prioritized);
    	TrafficShapingController.canPass
    		DefaultController
    		RateLimiterController
    		WarmUpController
    		WarmUpRateLimiterController
    
}

FlowRuleUtil.java generateRater:
 if (rule.getGrade() == RuleConstant.FLOW_GRADE_QPS) {
            switch (rule.getControlBehavior()) {
                case RuleConstant.CONTROL_BEHAVIOR_WARM_UP:
                    return new WarmUpController(...);
                case RuleConstant.CONTROL_BEHAVIOR_RATE_LIMITER:
                    return new RateLimiterController(....);
                case RuleConstant.CONTROL_BEHAVIOR_WARM_UP_RATE_LIMITER:
                    return new WarmUpRateLimiterController(....);
            }
        }
```



## 匀速限流场景

`RateLimiterController` 类中 canPass（这里并没有使用queue处理），计算每条数据需要等待的时间，如果时间超过maxQueueingTimeMs则丢弃，反之sleep需要等待的时长。

```java
@Override
    public boolean canPass(Node node, int acquireCount, boolean prioritized) {
        // Pass when acquire count is less or equal than 0.
        if (acquireCount <= 0) {
            return true;
        }
        // Reject when count is less or equal than 0.
        // Otherwise,the costTime will be max of long and waitTime will overflow in some cases.
        if (count <= 0) {
            return false;
        }

        long currentTime = TimeUtil.currentTimeMillis();
        // Calculate the interval between every two requests.
        long costTime = Math.round(1.0 * (acquireCount) / count * 1000);

        // Expected pass time of this request.
        long expectedTime = costTime + latestPassedTime.get();

        if (expectedTime <= currentTime) {
            // Contention may exist here, but it's okay.
            latestPassedTime.set(currentTime);
            return true;
        } else {
            // Calculate the time to wait.
            long waitTime = costTime + latestPassedTime.get() - TimeUtil.currentTimeMillis();
            if (waitTime > maxQueueingTimeMs) {
                return false;
            } else {
                long oldTime = latestPassedTime.addAndGet(costTime);
                try {
                    waitTime = oldTime - TimeUtil.currentTimeMillis();
                    if (waitTime > maxQueueingTimeMs) {
                        latestPassedTime.addAndGet(-costTime);
                        return false;
                    }
                    // in race condition waitTime may <= 0
                    if (waitTime > 0) {
                        Thread.sleep(waitTime);
                    }
                    return true;
                } catch (InterruptedException e) {
                }
            }
        }
        return false;
    }
```



