
  bin/zkServer.sh start-foreground
  #windows zkServer.cmd (不要start)


  bin/kafka-server-start.sh config/server.properties
  #windows>>  bin/windows/kafka-server-start ../../config/server.properties

  ./http_server -addr ":8011" -brokers "10.8.30.173:9092"

  #创建topic
  ./kafka-topics.sh --create --zookeeper node35:2181,node36:2181,node37:2181 --replication-factor 1 --partitions 3 --topic anxinyun_data
  ./kafka-topics.sh --create --zookeeper test-n1:2181,test-n2:2181,test-n3:2181 --replication-factor 1 --partitions 3 --topic anxinyun_deliver

  ./kafka-console-producer.sh --broker-list 10.8.30.35:6667,10.8.30.36:6667,10.8.30.37:6667 --topic anxinyun_data2
  ./kafka-console-producer.sh --broker-list test-n1:9092,test-n2:9092,test-n3:9092,test-n4:9092,test-n5:9092 --topic savoir_data

  ./kafka-console-consumer.sh --bootstrap-server iota-m1:6667,iota-n1:6667,iota-n2:6667 --topic savoir_alarm --from-beginning
  ./kafka-console-consumer.sh --bootstrap-server test-n1:9092,test-n2:9092,test-n3:9092,test-n4:9092,test-n5:9092 --topic savoir_data

  // on windows
  // I:\WorkSpace\@JAVA\kafka_2.11-0.11.0.0\bin\windows>kafka-console-consumer.bat --bootstrap-server 10.8.30.117:9092 --topic important --from-beginning

  // 查看消费情况
  ./kafka-consumer-groups.sh --new-consumer --bootstrap-server test-n1:9092 --list.
  ./kafka-consumer-groups.sh --new-consumer --bootstrap-server 10.8.30.36:6667 --group et.mainxx --describe

  ./kafka-consumer-groups.sh --describe --group savoir.master.server --zookeeper iota-m2:2181
  ./kafka-consumer-groups.sh  --bootstrap-server anxinyun-m1:6667 --group et.mainx --describe
	
  # 查看topic  (describe)
  ./kafka-topics.sh --list --zookeeper node35:2181,node36:2181,node37:2181

  ./kafka-topics.sh --zookeeper node35:2181,node36:2181,node37:2181  --describe  --topic anxinyun_data
  ```bash
	Topic:anxinyun_data	PartitionCount:1	ReplicationFactor:1	Configs:
	Topic: anxinyun_data	Partition: 0	Leader: 1003	Replicas: 1003	Isr: 1003
  ```

## 工具

### [Offset Explorer 2.1](https://www.kafkatool.com/download.html)



# 修改分区

  ./kafka-topics.sh --zookeeper node35:2181,node36:2181,node37:2181 --alter --topic anxinyun_data --partitions 3

  # 修改副本
  `increase-replication-factor.json`
  ``` json
  {"version":1,
	  "partitions":[
		 {"topic":"anxinyun_data","partition":0,"replicas":[1001,1002,1003]},
		 {"topic":"anxinyun_data","partition":1,"replicas":[1001,1002,1003]},
		 {"topic":"anxinyun_data","partition":2,"replicas":[1001,1002,1003]}
	]}
  ```
  kafka-reassign-partitions --zookeeper node35:2181,node36:2181,node37:2181 --reassignment-json-file increase-replication-factor.json --execute

## ambari 中kafka的broker.id

  在server.properties中log.dir中meta.properties
  ```
  #
#Fri Jul 14 14:50:50 CST 2017
version=0
broker.id=1004
  ```


  # 删除topic
  ./kafka-topics.sh --delete --zookeeper node35:2181,node36:2181,node37:2181 --topic anxinyun_data
  如果 `delete.topic.enable=true` 则直接删除，否则topic仅仅被标记为删除
  从zk中删除topic
  ./zkCli.sh
  ls /brokers/topics
  rmr /brokers/topics/anxinyun_data2
  删除完成后重启zookeeper和kafka服务；


  # 常见问题：
  1. Offset commit failed with a retriable exception

    consumer提交异常； https://blog.csdn.net/hanghangde/article/details/97945315

  session.timeout.ms => consumer-group检查组内成员掉线的时间； 消费逻辑处理时间太长导致，两次poll间隔不能超过此阈值；




## SPARK

  启动master	./sbin/start-master.sh
  启动slaves	./sbin/start-slave.sh <worker#> <master-spark-URL>
  启动shell		./bin/spark-shell --master spark://IP:PORT
​		count.saveAsTextFile("README.md")  不带file://的话，默认保存到hdfs中

报错 java.lang.NumberFormatException: For input string: "E:\xxx\xx"
zoo.cfg
用zkServer start命令报如题的错误，改为直接用zkServer启动则ok
dataDir=E:/★Coding/1workspace/zookeeper-3.4.10/data
		

## Eclipse

Windows——>Preferences——>Java-->Editor-->Content Asist，在Auto activation triggers for Java后面的文本框里只有一个“.”。现在你将其改为“.abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ”即可

## windows下部署hadoop

Error: JAVA_HOME is incorrectly set.
解决方法： JAVA_HOME不能包含空格
找不到hadoop和yarn文件
解决方法：hadooponwindows-master.zip解压替换hadoop中bin目录 （http://blog.csdn.net/antgan/article/details/52067441）
http://localhost:50070

## linq<==>scala

Select is map, 
Aggregate is reduce (or fold),
 SelectMany is flatMap,
 Where is filter or withFilter, 
 orderBy is sort or sortBy or sortWith,
 and there are zip, take and takeWhile
 First is find
 Any is exists
 All is forall

 Spark Stream中 Partitioner是个什么鬼？？？

hdfs namenode -format

start-dfs.cmd 中修改
start "Apache Hadoop Distribution" %HADOOP_BIN_PATH%\..\bin\hadoop.cmd namenode
start "Apache Hadoop Distribution" %HADOOP_BIN_PATH%\..\bin\hadoop.cmd datanode

### nexus

http://localhost:8081/
http://books.sonatype.com/nexus-book/reference3/install.html#installation-java
nexus.exe /install <optional-service-name>
nexus.exe /start <optional-service-name>
nexus.exe /stop <optional-service-name>
nexus.exe /uninstall <optional-service-name>

### maven项目支持scala(IDEA)

File>ProjectStructure>Library>+ScalaSDK  (可能要download)



###  postgres备份

 pg_dump -h host1 dbname | psql -h host2 dbname  

 

 #!/bin/sh
for host in node-1 node-2 node-3
do
	ssh $host "source /etc/profile;/export/servers/zookeeper/bin/zkServer.sh start"
    ssh $host"source/etc/profile;nohup /export/servers/kafka/binkafka-server-start.sh 
	/export/servers/kafka/config/server.properties >/dev/null 2>&1" 
	echo "$host zookeeper&kafka is running"
done

#!/bin/sh
for host in node-1 node-2 node-3
do
	ssh $host "source /etc/profile;bash /export/servers/kafka/bin/kafka-server-stop.sh" 
	ssh $host "source /etc/profile;/export/servers/zookeeper/bin/zkServer.sh stop"
	echo "$host kafka&zookeeper is stopping"
done

## [kafka常见参数说明](https://www.cnblogs.com/weixiuli/p/6413109.html)

```shell
一、相关参数配置

############################ System #############################

#唯一标识在集群中的ID，要求是正数。
broker.id=0
#服务端口，默认9092
port=9092
#监听地址，不设为所有地址
host.name=debugo01

处理网络请求的最大线程数

num.network.threads=2

处理磁盘I/O的线程数

num.io.threads=8

一些后台线程数

background.threads = 4

等待IO线程处理的请求队列最大数

queued.max.requests = 500

socket的发送缓冲区（SO_SNDBUF）

socket.send.buffer.bytes=1048576

socket的接收缓冲区 (SO_RCVBUF)

socket.receive.buffer.bytes=1048576
# socket请求的最大字节数。为了防止内存溢出，message.max.bytes必然要小于
socket.request.max.bytes = 104857600

############################# Topic #############################
# 每个topic的分区个数，更多的partition会产生更多的segment file
num.partitions=2
# 是否允许自动创建topic ，若是false，就需要通过命令创建topic
auto.create.topics.enable =true
# 一个topic ，默认分区的replication个数 ，不能大于集群中broker的个数。
default.replication.factor =1
# 消息体的最大大小，单位是字节
message.max.bytes = 1000000

############################# ZooKeeper #############################
# Zookeeper quorum设置。如果有多个使用逗号分割
zookeeper.connect=debugo01:2181,debugo02,debugo03
# 连接zk的超时时间
zookeeper.connection.timeout.ms=1000000
# ZooKeeper集群中leader和follower之间的同步实际
zookeeper.sync.time.ms = 2000

############################# Log #############################
#日志存放目录，多个目录使用逗号分割
log.dirs=/var/log/kafka

# 当达到下面的消息数量时，会将数据flush到日志文件中。默认10000
#log.flush.interval.messages=10000
# 当达到下面的时间(ms)时，执行一次强制的flush操作。interval.ms和interval.messages无论哪个达到，都会flush。默认3000ms
#log.flush.interval.ms=1000
# 检查是否需要将日志flush的时间间隔
log.flush.scheduler.interval.ms = 3000

# 日志清理策略（delete|compact）
log.cleanup.policy = delete
# 日志保存时间 (hours|minutes)，默认为7天（168小时）。超过这个时间会根据policy处理数据。bytes和minutes无论哪个先达到都会触发。
log.retention.hours=168
# 日志数据存储的最大字节数。超过这个时间会根据policy处理数据。
#log.retention.bytes=1073741824

# 控制日志segment文件的大小，超出该大小则追加到一个新的日志segment文件中（-1表示没有限制）
log.segment.bytes=536870912
# 当达到下面时间，会强制新建一个segment
log.roll.hours = 24*7
# 日志片段文件的检查周期，查看它们是否达到了删除策略的设置（log.retention.hours或log.retention.bytes）
log.retention.check.interval.ms=60000

# 是否开启压缩
log.cleaner.enable=false
# 对于压缩的日志保留的最长时间
log.cleaner.delete.retention.ms = 1 day

# 对于segment日志的索引文件大小限制
log.index.size.max.bytes = 10 * 1024 * 1024
#y索引计算的一个缓冲区，一般不需要设置。
log.index.interval.bytes = 4096

############################# replica #############################
# partition management controller 与replicas之间通讯的超时时间
controller.socket.timeout.ms = 30000
# controller-to-broker-channels消息队列的尺寸大小
controller.message.queue.size=10
# replicas响应leader的最长等待时间，若是超过这个时间，就将replicas排除在管理之外
replica.lag.time.max.ms = 10000
# 是否允许控制器关闭broker ,若是设置为true,会关闭所有在这个broker上的leader，并转移到其他broker
controlled.shutdown.enable = false
# 控制器关闭的尝试次数
controlled.shutdown.max.retries = 3
# 每次关闭尝试的时间间隔
controlled.shutdown.retry.backoff.ms = 5000

# 如果relicas落后太多,将会认为此partition relicas已经失效。而一般情况下,因为网络延迟等原因,总会导致replicas中消息同步滞后。如果消息严重滞后,leader将认为此relicas网络延迟较大或者消息吞吐能力有限。在broker数量较少,或者网络不足的环境中,建议提高此值.
replica.lag.max.messages = 4000
#leader与relicas的socket超时时间
replica.socket.timeout.ms= 30 * 1000
# leader复制的socket缓存大小
replica.socket.receive.buffer.bytes=64 * 1024
# replicas每次获取数据的最大字节数
replica.fetch.max.bytes = 1024 * 1024
# replicas同leader之间通信的最大等待时间，失败了会重试
replica.fetch.wait.max.ms = 500
# 每一个fetch操作的最小数据尺寸,如果leader中尚未同步的数据不足此值,将会等待直到数据达到这个大小
replica.fetch.min.bytes =1
# leader中进行复制的线程数，增大这个数值会增加relipca的IO
num.replica.fetchers = 1
# 每个replica将最高水位进行flush的时间间隔
replica.high.watermark.checkpoint.interval.ms = 5000

# 是否自动平衡broker之间的分配策略
auto.leader.rebalance.enable = false
# leader的不平衡比例，若是超过这个数值，会对分区进行重新的平衡
leader.imbalance.per.broker.percentage = 10
# 检查leader是否不平衡的时间间隔
leader.imbalance.check.interval.seconds = 300
# 客户端保留offset信息的最大空间大小
offset.metadata.max.bytes = 1024

#############################Consumer #############################

		# Consumer端核心的配置是group.id、zookeeper.connect
		# 决定该Consumer归属的唯一组ID，By setting the same group id multiple processes indicate that they are all part of the same consumer group.
		group.id
		# 消费者的ID，若是没有设置的话，会自增
		consumer.id
		# 一个用于跟踪调查的ID ，最好同group.id相同
		client.id = <group_id>
		 
		# 对于zookeeper集群的指定，必须和broker使用同样的zk配置
		zookeeper.connect=debugo01:2182,debugo02:2182,debugo03:2182
		# zookeeper的心跳超时时间，超过这个时间就认为是无效的消费者
		zookeeper.session.timeout.ms = 6000
		# zookeeper的等待连接时间
		zookeeper.connection.timeout.ms = 6000
		# zookeeper的follower同leader的同步时间
		zookeeper.sync.time.ms = 2000
		# 当zookeeper中没有初始的offset时，或者超出offset上限时的处理方式 。
		# smallest ：重置为最小值
		# largest:重置为最大值
		# anything else：抛出异常给consumer
		auto.offset.reset = largest
		/*
		kafka + zookeeper,当消息被消费时,会向zk提交当前groupId的consumer消费的offset信息,当consumer再次启动将会从此offset开始继续消费.
	
		在consumter端配置文件中(或者是ConsumerConfig类参数)有个"autooffset.reset"(在kafka 0.8版本中为auto.offset.reset),有2个合法的值"largest"/"smallest",默认为"largest",此配置参数表示当此groupId下的消费者,在ZK中没有offset值时(比如新的groupId,或者是zk数据被清空),consumer应该从哪个offset开始消费.
		1、largest表示接受接收最大的offset(即最新消息),
		2、smallest表示最小offset,即从topic的开始位置消费所有消息.
		*/
		 
		# socket的超时时间，实际的超时时间为max.fetch.wait + socket.timeout.ms.
		socket.timeout.ms= 30 * 1000
		# socket的接收缓存空间大小
		socket.receive.buffer.bytes=64 * 1024
		#从每个分区fetch的消息大小限制
		fetch.message.max.bytes = 1024 * 1024
		 
		# true时，Consumer会在消费消息后将offset同步到zookeeper，这样当Consumer失败后，新的consumer就能从zookeeper获取最新的offset
		auto.commit.enable = true   ，项目里用false 不知道是什么原因
		# 自动提交的时间间隔
		auto.commit.interval.ms = 60 * 1000
		 
		# 用于消费的最大数量的消息块缓冲大小，每个块可以等同于fetch.message.max.bytes中数值
		queued.max.message.chunks = 10
		 
		# 当有新的consumer加入到group时,将尝试reblance,将partitions的消费端迁移到新的consumer中, 该设置是尝试的次数
		rebalance.max.retries = 4
		# 每次reblance的时间间隔
		rebalance.backoff.ms = 2000
		# 每次重新选举leader的时间
		refresh.leader.backoff.ms
		 
		# server发送到消费端的最小数据，若是不满足这个数值则会等待直到满足指定大小。默认为1表示立即接收。
		fetch.min.bytes = 1
		# 若是不满足fetch.min.bytes时，等待消费端请求的最长等待时间
		fetch.wait.max.ms = 100
		# 如果指定时间内没有新消息可用于消费，就抛出异常，默认-1表示不受限
		consumer.timeout.ms = -1
		 
		#############################Producer#############################
		# 核心的配置包括：
		# metadata.broker.list
		# request.required.acks
		# producer.type
		# serializer.class
		 
		# 消费者获取消息元信息(topics, partitions and replicas)的地址,配置格式是：host1:port1,host2:port2，也可以在外面设置一个vip
		metadata.broker.list
		 
		#消息的确认模式
		# 0：不保证消息的到达确认，只管发送，低延迟但是会出现消息的丢失，在某个server失败的情况下，有点像TCP
		# 1：发送消息，并会等待leader 收到确认后，一定的可靠性
		# -1：发送消息，等待leader收到确认，并进行复制操作后，才返回，最高的可靠性
		request.required.acks = 0
		 
		# 消息发送的最长等待时间
		request.timeout.ms = 10000
		# socket的缓存大小
		send.buffer.bytes=100*1024
		# key的序列化方式，若是没有设置，同serializer.class
		key.serializer.class
		# 分区的策略，默认是取模
		partitioner.class=kafka.producer.DefaultPartitioner
		# 消息的压缩模式，默认是none，可以有gzip和snappy
		compression.codec = none
		# 可以针对默写特定的topic进行压缩
		compressed.topics=null
		# 消息发送失败后的重试次数
		message.send.max.retries = 3
		# 每次失败后的间隔时间
		retry.backoff.ms = 100
		# 生产者定时更新topic元信息的时间间隔 ，若是设置为0，那么会在每个消息发送后都去更新数据
		topic.metadata.refresh.interval.ms = 600 * 1000
		# 用户随意指定，但是不能重复，主要用于跟踪记录消息
		client.id=""
		 
		# 异步模式下缓冲数据的最大时间。例如设置为100则会集合100ms内的消息后发送，这样会提高吞吐量，但是会增加消息发送的延时
		queue.buffering.max.ms = 5000
		# 异步模式下缓冲的最大消息数，同上
		queue.buffering.max.messages = 10000
		# 异步模式下，消息进入队列的等待时间。若是设置为0，则消息不等待，如果进入不了队列，则直接被抛弃
		queue.enqueue.timeout.ms = -1
		# 异步模式下，每次发送的消息数，当queue.buffering.max.messages或queue.buffering.max.ms满足条件之一时producer会触发发送。
		batch.num.messages=200

```


​		
## KAFKA性能测试
 ♥♥♥ https://www.cnblogs.com/runnerjack/p/9105784.html ♥♥♥\

## Kafka Performance Tuning

 [HERE](https://data-flair.training/blogs/kafka-performance-tuning/)
 Producer:

+ Compression 减少网络io和延迟 但消耗cpu
+ Batch Size 分包大小的上限；提升该值可以提高吞吐量，但是会造成延迟问题latency
+ linger time 等待组包batch的时间，默认是0
+ Sync or Async


 ## 错误排查

 LEADER_NOT_AVAILABLE


 Error while fetching metadata with correlation id

 

 amabari中所有节点心跳丢失，无法启动iota-m1上的kafka服务；

查看ambari-agent日志：/var/log/ambari-agent# tail -f ambari-agent.log

```shell
Traceback (most recent call last):
  File "/usr/lib/python2.6/site-packages/ambari_agent/Controller.py", line 175, in registerWithServer
    ret = self.sendRequest(self.registerUrl, data)
  File "/usr/lib/python2.6/site-packages/ambari_agent/Controller.py", line 545, in sendRequest
    raise IOError('Request to {0} failed due to {1}'.format(url, str(exception)))
IOError: Request to https://iota-m1:8441/agent/v1/register/iota-n1 failed due to EOF occurred in violation of protocol (_ssl.c:590)
ERROR 2020-06-16 21:09:28,836 Controller.py:227 - Error:Request to https://iota-m1:8441/agent/v1/register/iota-n1 failed due to EOF occurred in violation of protocol (_ssl.c:590)
WARNING 2020-06-16 21:09:28,837 Controller.py:228 -  Sleeping for 5 seconds and then trying again 
```



发现问题是https证书过期
https://my.oschina.net/aubao/blog/1920933

在每个ambari节点上修改

```shell
vi /etc/ambari-agent/conf/ambari-agent.ini
```

```force_https_protocol=PROTOCOL_TLSv1_2```
然后，重启所有节点上`ambari-agent`：

```shell
ambari-agent restart
```



## Zookeeper:

[ref csdn](https://blog.csdn.net/java_66666/article/details/81015302)
统一命名服务、状态同步服务、集群管理、分布式应用配置项的管理等
zookeeper = 文件系统 + 监听通知机制

znode（目录）
+ Persistent 持久
+ persistent-sequential 持久顺序编号
+ ephemeral 临时目录节点； 客户端端口连接后，节点被删除
+ ephemeral_sequential 临时顺序编号

```shell
zkServer.sh status [conf/zoo.cfg]


zkCli.sh 
help"
stat path [watch]
	set path data [version]
	ls path [watch]
	delquota [-n|-b] path
	ls2 path [watch]
	setAcl path acl
	setquota -n|-b val path
	history 
	redo cmdno
	printwatches on|off
	delete path [version]
	sync path
	listquota path
	rmr path
	get path [watch]
	create [-s] [-e] path data acl
	addauth scheme auth
	quit 
	getAcl path
	close 
	connect host:port
	
create [-s] [-e] path data acl
	-s: 创建顺序节点
	-e：创建临时 ephemeral znode
	path: znode目录路径 /xxx/xx
	acl: 权限： world:anyone:crwa
getAcl/setAcl: 设置权限：
	默认 world:anyone cdrwa

set path data [version]
	version: 指定数据版本
	
get /iota/orchestration/Thing/1/resources
	{...data....}
	cZxid = 0x34001de68b 【创建节点ID】
	ctime = Wed Jun 10 19:51:50 CST 2020 【create time】
	mZxid = 0x3a0000013e 【修改节点的ID】
	mtime = Thu Aug 06 10:15:16 CST 2020 【modify time】
	pZxid = 0x34001de68b 【子节点的ID】
	cversion = 0 【子节点的版本】
	dataVersion = 12 【当前节点数据的版本】
	aclVersion = 0 【权限的版本】
	ephemeralOwner = 0x0 【时候临时节点 】
	dataLength = 13971 【数据长度】
	numChildren = 0 【子节点的数量】

查询状态
echo stat | nc 192.168.0.68 2181
查询是否启动
echo ruok | nc 192.168.0.68 2181
查询没有处理的节点，临时节点
echo dump | nc 192.168.0.68 2181
查询服务器配置
echo conf | nc 192.168.0.68 2181
查询客户端连接
echo cons | nc 192.168.0.68 2181
查询环境变量
echo envi | nc 127.0.0.1 2181
查询健康信息
echo mntr | nc 192.168.0.68 2181

wchs watch信息； wchc session watchs; wchp path watchs;
```

Zookeeper集群模式：
```shell
# 数据目录
dataDir=/tmp/zookeeper-1
# 客户端端口
clientPort=2181
# Service.N = ip:通信端口：Leader选举端口
server.1=127.0.0.1:2888:3888
server.2=127.0.0.1:2889:3889
server.3=127.0.0.1:2890:3890
# tickTime 心跳间隔
# initLimit Follow连接Leader的超时间隔数
# syncLimit Leader和Follow通信超时时间间隔
```


## Kafka 数据丢失问题排查：
[关键参数设置](https://www.cnblogs.com/wwcom123/p/11181680.html)
### buffer.memory
　　Kafka的客户端发送数据到服务器，不是来一条就发一条，而是经过缓冲的，也就是说，通过KafkaProducer发送出去的消息都是先进入到客户端本地的内存缓冲里，然后把很多消息收集成一个一个的Batch，再发送到Broker上去的，这样性能才可能高。

　　buffer.memory的本质就是用来约束KafkaProducer能够使用的内存缓冲的大小的，默认值32MB。

　　如果buffer.memory设置的太小，可能导致的问题是：消息快速的写入内存缓冲里，但Sender线程来不及把Request发送到Kafka服务器，会造成内存缓冲很快就被写满。而一旦被写满，就会阻塞用户线程，不让继续往Kafka写消息了。 

　　所以“buffer.memory”参数需要结合实际业务情况压测，需要测算在生产环境中用户线程会以每秒多少消息的频率来写入内存缓冲。经过压测，调试出来一个合理值。

 

batch.size
　　每个Batch要存放batch.size大小的数据后，才可以发送出去。比如说batch.size默认值是16KB，那么里面凑够16KB的数据才会发送。

  理论上来说，提升batch.size的大小，可以允许更多的数据缓冲在里面，那么一次Request发送出去的数据量就更多了，这样吞吐量可能会有所提升。

　　但是batch.size也不能过大，要是数据老是缓冲在Batch里迟迟不发送出去，那么发送消息的延迟就会很高。

　　一般可以尝试把这个参数调节大些，利用生产环境发消息负载测试一下。


linger.ms
　　一个Batch被创建之后，最多过多久，不管这个Batch有没有写满，都必须发送出去了。

　　比如说batch.size是16KB，但是现在某个低峰时间段，发送消息量很小。这会导致可能Batch被创建之后，有消息进来，但是迟迟无法凑够16KB，难道此时就一直等着吗？

　　当然不是，假设设置“linger.ms”是50ms，那么只要这个Batch从创建开始到现在已经过了50ms了，哪怕他还没满16KB，也会被发送出去。 

　　所以“linger.ms”决定了消息一旦写入一个Batch，最多等待这么多时间，他一定会跟着Batch一起发送出去。 

　　linger.ms配合batch.size一起来设置，可避免一个Batch迟迟凑不满，导致消息一直积压在内存里发送不出去的情况。

  

max.request.size
　　决定了每次发送给Kafka服务器请求消息的最大大小。

　　如果发送的消息都是大报文消息，每条消息都是数据较大，例如一条消息可能要20KB。此时batch.size需要调大些，比如设置512KB，buffer.memory也需要调大些，比如设置128MB。 

　　只有这样，才能在大消息的场景下，还能使用Batch打包多条消息的机制。

　　此时“max.request.size”也得同步增加。

 

retries和retries.backoff.ms
　　重试机制，也就是如果一个请求失败了可以重试几次，每次重试的间隔是多少毫秒，根据业务场景需要设置。

 

acks

含义
0	 Producer 往集群发送数据不需要等到集群的返回，不确保消息发送成功。安全性最低但是效率最高。
1	 Producer 往集群发送数据只要 Leader 应答就可以发送下一条，只确保 Leader 接收成功。
-1 或 all	 Producer 往集群发送数据需要所有的ISR Follower 都完成从 Leader 的同步才会发送下一条，确保 Leader 发送成功和所有的副本都成功接收。安全性最高，但是效率最低。