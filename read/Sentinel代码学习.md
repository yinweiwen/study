Sentinel

Metric接口

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

> ArrayMetric 通过BucketLeapArray实现的基础指标统计类



LeapArray

-- sentinel中统计的基础数据结构。滑动窗口算法 

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



MetricBucket

统计各个MetricEvent (PASS/BLOCK/EXCEPTION/SUCCESS)的窗口数据。(一段时间范围的指标数据)



## 匀速限流场景

RateLimiterController 类中canPass（这里并没有使用queue处理），计算每条数据需要等待的时间，如果时间超过maxQueueingTimeMs则丢弃，反之sleep需要等待的时长。

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



