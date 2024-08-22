问题：

IOT kafka不稳定问题，可能的解决方案是将IOT的kafka迁移至安心云集群中，然后将IOT集群的kafka等进行版本、硬盘升级。

## 整体说明

准备环境到



## 目前情况

> 版本 Kafka 0.10.1
>
> 节点 iota-m1 iota-m2 iota-n1~n4
>
> 配置：
>
> ​	保留时间：48h
>
> ​	吞吐量： ~40G/48h 每节点



Iota使用Kafka的进程

| 进程                       | Topic                                                 | 说明                 |
| -------------------------- | ----------------------------------------------------- | -------------------- |
| 容器编排 iota-orchestrator | HeartBeat                                             | 与DAC之间的心跳保活  |
| DAC iota-dac               | HeartBeat Registry **RawData** InvokeCap InvokeCapAck | 各种消息体           |
| 代理 iota-proxy            | Registry                                              | DAC资源注册到代理    |
| 规则引擎 iota-rule-engine  | **RawData**                                           | 消费DAC生成的数据    |
| 协议测试 DacTest           | ScriptTest ScriptTestAck                              | 协议测试             |
| 边缘网关 fs-edge-backend   | **RawData**                                           | 网关数据生成         |
| 以太API iota-api           | ScriptTest ScriptTestAck InvokeCap InvokeCapAck       | 及时采集 、 协议测试 |



topic列表：

`Alert`
`HeartBeat`
`InvokeCap`
`InvokeCapAck`
`LinkStatMetrics`
`RawData`
*`RawData1`
*`RawData2`
*`Register`
*`Registry`
*`Registry2`
`Registry3`
`RunTest`
`RunTestAck`
`ScriptTest`
`ScriptTestAck`
`StreamMetrics`
`TestProgress`

> *号为曾用topic，已无使用

一般采用 5 分片 3副本的配置：

```sh
Topic:RawData	PartitionCount:5	ReplicationFactor:3	Configs:
	Topic: RawData	Partition: 0	Leader: 1002	Replicas: 1002,1004,1005	Isr: 1002,1004,1005
	Topic: RawData	Partition: 1	Leader: 1006	Replicas: 1006,1005,1001	Isr: 1005,1001,1006
	Topic: RawData	Partition: 2	Leader: 1004	Replicas: 1004,1001,1002	Isr: 1004,1002,1001
	Topic: RawData	Partition: 3	Leader: 1005	Replicas: 1005,1002,1006	Isr: 1002,1005,1006
	Topic: RawData	Partition: 4	Leader: 1001	Replicas: 1001,1006,1004	Isr: 1004,1001,1006
```



目前以太几个k8s节点的使用情况参考下面的shell查询结果，以下节点可以重装

| 节点     | 当前状态          | 使用计划 |
| -------- | ----------------- | -------- |
| iota-m1  | 单独节点          |          |
| iota-n2  | k8s节点，关闭调度 |          |
| iota-n3  | k8s节点，关闭调度 |          |
| iota-n11 | k8s节点，较空闲   |          |



```shell

anxin@anxin-m1:~$ kubectl get no
NAME       STATUS                     ROLES     AGE       VERSION
anxin-m1   Ready                      master    4y        v1.8.2
anxin-m2   Ready                      worker    4y        v1.8.2
iota-m2    Ready                      worker    2y        v1.8.2
iota-n1    Ready                      worker    2y        v1.8.2
iota-n11   Ready                      worker    279d      v1.8.2
iota-n2    Ready,SchedulingDisabled   worker    2y        v1.8.2
iota-n3    Ready,SchedulingDisabled   worker    4y        v1.8.2
iota-n4    Ready                      worker    4y        v1.8.2

anxin@anxin-m1:~$ ki | grep 'n2\|n3\|n11'
iota-api-0                                              1/1       Running            2          47d       10.244.7.193   iota-n11
iota-proxy-787f4cf968-rz7h2                             2/2       Running            0          22d       10.244.7.200   iota-n11
iota-rules-engine-3                                     1/1       Running            2          22d       10.244.7.198   iota-n11
iota-rules-engine-6                                     1/1       Running            0          22d       10.244.7.199   iota-n11
iota-rules-engine-7                                     1/1       Running            0          22d       10.244.7.197   iota-n11
jupyter-notebook-1b2d8739-627e-4b7d-9480-3eee6e9396fe   0/1       ImagePullBackOff   0          161d      10.244.5.31    iota-n2
jupyter-notebook-637fb54b-3aac-46ef-958d-e0cdfccdf59c   0/1       ImagePullBackOff   0          116d      10.244.5.147   iota-n2
jupyter-notebook-f4befb7f-80e5-48a7-a741-80f0b40eb9dd   0/1       ImagePullBackOff   0          131d      10.244.5.94    iota-n2

anxin@anxin-m1:~$ ka | grep 'n2\|n3\|n11'
highwaybridge-report-586bc67c-8ks5g        1/1       Running   1          144d      10.244.5.24    iota-n2
highwayconsole-7fd76f7b67-f9wx6            1/1       Running   1          144d      10.244.5.34    iota-n2
logstash-api-log-599db7698f-mn4gs          1/1       Running   1          184d      10.244.5.47    iota-n2
savoir-iotaproxy-57655ff6c7-s7lsg          1/1       Running   1          39d       10.244.7.191   iota-n11
savoir-jupyter-notebook-1                  1/1       Running   0          141d      10.244.5.67    iota-n2
savoir-jupyter-notebook-5122               1/1       Running   0          126d      10.244.5.106   iota-n2
savoir-jupyter-notebook-5180               1/1       Running   1          160d      10.244.5.33    iota-n2
savoir-jupyter-notebook-5196               1/1       Running   0          131d      10.244.5.92    iota-n2
```





## 升级步骤

### 1. 迁移使用安心云集群Kafka

Kafka迁移使用安心云集群步骤

1. 几个nodejs的程序需要升级（kafka版本升级），包括**编排器、api、规则引擎**
2. 新kafka地址映射到anxin_m1。
3. 需修改以上几个程序对应的config-map中kafka的broker配置.（配置kafka broker时节点应配尽配）
4. 检查kafka中是否自动创建topic（参考以上列表），并查看topic中数据。 https://kafka.anxinyun.cn/



### 2. 重新安装新版本Kafka



