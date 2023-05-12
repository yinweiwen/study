## 问题起于编排器：

DAC出现重启情况，非人为触发，查看编排器日志

![image-20230202201919467](imgs/Kafka%E9%97%AE%E9%A2%98%E6%8E%92%E6%9F%A5%E8%AE%B0%E5%BD%95/image-20230202201919467.png)

果然是DAC到编排器的心跳丢失导致，心跳是通过Kafka消息传递，Topic为 HeartBeat。

> 再次之前也出现了DTU无法注册的问题。因为DAC注册【DTU与所在实例IP信息】的消息是通过Kafka发送的，可能也是Kafka发送的消息丢失导致

## 手动验证

```sh
root@iota-n2:/usr/hdp/current/kafka-broker/bin# ./kafka-console-producer.sh --broker-list iota-n2:6667 --topic HeartBeat
root@iota-m2:/usr/hdp/current/kafka-broker/bin# ./kafka-console-consumer.sh --bootstrap-server iota-m2:6667 --topic HeartBeat
```

验证kafka的问题，启动一个Producer，一个Consumer，对发看情况。如下图（已经恢复后的）

之前报错：

> produce response with correlation id xx  on topic-partition 4 xx "NETWORK_EXCEPTION"

![image-20230202201515767](imgs/Kafka%E9%97%AE%E9%A2%98%E6%8E%92%E6%9F%A5%E8%AE%B0%E5%BD%95/image-20230202201515767.png)

查看topic信息：

```sh
root@iota-n2:/usr/hdp/current/kafka-broker/bin# ./kafka-topics.sh --zookeeper iota-n2:2181 --describe --topic HeartBeat
Topic:HeartBeat	PartitionCount:5	ReplicationFactor:3	Configs:
	Topic: HeartBeat	Partition: 0	Leader: 1002	Replicas: 1002,1003,1004	Isr: 1003,1002,1004
	Topic: HeartBeat	Partition: 1	Leader: 1003	Replicas: 1003,1004,1005	Isr: 1003,1004,1005
	Topic: HeartBeat	Partition: 2	Leader: 1004	Replicas: 1004,1005,1001	Isr: 1004,1005,1001
	Topic: HeartBeat	Partition: 3	Leader: 1005	Replicas: 1005,1001,1002	Isr: 1002,1005,1001
	Topic: HeartBeat	Partition: 4	Leader: 1001	Replicas: 1001,1002,1003	Isr: 1001,1002,1003
	
# 分区4所在broker 1001
root@iota-n2:/usr/hdp/current/zookeeper-client/bin# ./zkCli.sh
[zk: localhost:2181(CONNECTED) 0] ls /broker/ids
Node does not exist: /broker/ids
[zk: localhost:2181(CONNECTED) 1] ls /brokers/ids
[1006, 1005, 1004, 1003, 1002, 1001]
[zk: localhost:2181(CONNECTED) 2] get /brokers/ids/1001
{"jmx_port":-1,"timestamp":"1675325231426","endpoints":["PLAINTEXT://iota-n2:6667"],"host":"iota-n2","version":3,"port":6667}
cZxid = 0xc000c3a2e
ctime = Thu Feb 02 16:07:06 CST 2023
mZxid = 0xc000c3a2e
mtime = Thu Feb 02 16:07:06 CST 2023
pZxid = 0xc000c3a2e
cversion = 0
dataVersion = 0
aclVersion = 0
ephemeralOwner = 0x284516e9f3403ff
dataLength = 125
numChildren = 0

# 发现问题在iota-n2节点
```



## 查找日志

```
root@iota-m2:/var/log/kafka# more server.log.2023-02-02-18
```

iota-n2节点上都是些Shrink的正常日志，并未发现明显异常。但是在其他节点中发现有异常，也是连接Broker 1001时出现失败的情况。

```log

[2023-02-02 18:05:41,431] WARN [ReplicaFetcherThread-0-1001], Error in fetch kafka.server.ReplicaFetcherThread$FetchRequest@c91df11 
(kafka.server.ReplicaFetcherThread)
java.io.IOException: Connection to 1001 was disconnected before the response was read
	at kafka.utils.NetworkClientBlockingOps$$anonfun$blockingSendAndReceive$extension$1$$anonfun$apply$1.apply(NetworkClientBloc
kingOps.scala:115)
	at kafka.utils.NetworkClientBlockingOps$$anonfun$blockingSendAndReceive$extension$1$$anonfun$apply$1.apply(NetworkClientBloc
kingOps.scala:112)
	at scala.Option.foreach(Option.scala:236)
	at kafka.utils.NetworkClientBlockingOps$$anonfun$blockingSendAndReceive$extension$1.apply(NetworkClientBlockingOps.scala:112
)
	at kafka.utils.NetworkClientBlockingOps$$anonfun$blockingSendAndReceive$extension$1.apply(NetworkClientBlockingOps.scala:108
)
	at kafka.utils.NetworkClientBlockingOps$.recursivePoll$1(NetworkClientBlockingOps.scala:137)
	at kafka.utils.NetworkClientBlockingOps$.kafka$utils$NetworkClientBlockingOps$$pollContinuously$extension(NetworkClientBlock
ingOps.scala:143)
	at kafka.utils.NetworkClientBlockingOps$.blockingSendAndReceive$extension(NetworkClientBlockingOps.scala:108)
	at kafka.server.ReplicaFetcherThread.sendRequest(ReplicaFetcherThread.scala:253)
	at kafka.server.ReplicaFetcherThread.fetch(ReplicaFetcherThread.scala:238)
	at kafka.server.ReplicaFetcherThread.fetch(ReplicaFetcherThread.scala:42)
	at kafka.server.AbstractFetcherThread.processFetchRequest(AbstractFetcherThread.scala:118)
	at kafka.server.AbstractFetcherThread.doWork(AbstractFetcherThread.scala:103)
	at kafka.utils.ShutdownableThread.run(ShutdownableThread.scala:63)
```



## 查看性能

```sh
# 整体可视化
htop
# 进程性能消耗情况 SHIFT+M
top -c 
# 磁盘使用
df -h
# 网络带宽占用
bmon

```

经以上，发现性能消耗在一个中上的水准，同时网络带宽压力较其他节点略有升高。通过kubectl命令查看pod分布，发现其上容器组较多，且主要对外Proxy在该节点。（另外主要占用应该时savoir-recv）



## 人为触发重新编排，释放压力

之前因为iota-m2节点磁盘满导致k8s服务失败，遂将其剔除到调度外。现已将其上的基础镜像恢复，并执行uncordon将其重启加入k8s调度。

```sh
kubectl uncordon iota-m2
```

注释了其他一些容器中的nodeselecter中对n2的限制，重新执行，释放iota-n2节点压力。



## 参考

【Kafka实战宝典：一文带解决Kafka常见故障处理】https://cloud.tencent.com/developer/article/1632139