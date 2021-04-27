[官网](https://ci.apache.org/projects/flink/flink-docs-release-1.8/)

##  创建Flink Maven项目

```
$ mvn archetype:generate                               \
      -DarchetypeGroupId=org.apache.flink              \
      -DarchetypeArtifactId=flink-quickstart-scala     \
      -DarchetypeVersion=1.9.0
```

dependency：
```xml
<dependency>
  <groupId>org.apache.flink</groupId>
  <artifactId>flink-scala_2.11</artifactId>
  <version>1.8.0</version>
  <scope>provided</scope>
</dependency>
<dependency>
  <groupId>org.apache.flink</groupId>
  <artifactId>flink-streaming-scala_2.11</artifactId>
  <version>1.8.0</version>
  <scope>provided</scope>
</dependency>

<dependency>
    <groupId>org.apache.flink</groupId>
    <artifactId>flink-connector-kafka-0.10_2.11</artifactId>
    <version>1.8.0</version>
</dependency>
```
## API基础概念

### File Sink

在项目中添加引用
```xml
<dependency>
	<groupId>org.apache.flink</groupId>
	<artifactId>flink-connector-filesystem_${scala.binary.version}</artifactId>
	<version>${flink.version}</version>
</dependency>

### Flink 1.9(Or Older)
[Sink to HDFS](https://liurio.github.io/2020/03/14/Flink-sink%E5%88%B0hdfs/)

```
#### Bucketing File Sink

Bucket: 分桶策略 （无界数据流按指定策略划分）
+ BasePathBucketer 不分桶
+ DateTimeBucketer 基于系统时间
+ 自定义 实现Bucketer接口

Writer： 写入方式
+ StringWriter 默认；tostring然后换行写入
+ SequenceFileWriter Hadoop序列文件写入方式
+ 自定义 实现Writer接口

```java
BucketingSink sink = new BucketingSink<T>("") //文件目录地址
    .setBucketer(new MemberBucket()) //自定义桶名称
    .setWriter(new MemberWriter()) //自定义写入
    .setBatchSize(120*1024*1024) //设置每个文件的最大大小，默认384M
    .setBatchRolloverInterval(Long.MaxValue) //滚动写入新文件的时间，默认无限大
    .setInactiveBucketCheckInterval(60*1000) //1分钟检查一次不写入的文件
    .setInactiveBucketThreshold(5*60*1000) //5min不写入，就滚动写入新的文件
    .setPartSuffix(".log") //文件后缀
```

#### Streaming File Sink (@Flink>=1.6)

分桶策略：BucketAssigner
+ BasePathBucketAssigner  不分桶，所有文件写入根目录
+ DateTimeBucketAssigner 基于系统时间(yyyy-MM-dd–HH)分桶
+ 自定义分桶 实现BucketAssigner接口


滚动策略：RollingPolicy
+ DefaultRollingPolicy (可按照最大桶大小(128M default)，滚动周期(60s)，未写入/不活跃状态超时(60s))
+ OnCheckpointRollingPolicy  当checkpoint的时候滚动

写入方式：
+ SimpleStringEncoder  __forRawFormat__ 基于行存储
+ BulkWriterFactory __forBulkFormat__  按列存储，批量编码方式，可以将输出结果用 Parquet 等格式进行压缩存储



## Flink1.12 版本

[Flink 1.12 Doc](https://ci.apache.org/projects/flink/flink-docs-release-1.12/dev/connectors/streamfile_sink.html)



## Metrics

Flink的指标获取

Flink supports `Counters`, `Gauges`, `Histograms` and `Meters`.

```scala
class MyMapper extends RichMapFunction[String,String] {
  @transient private var counter: Counter = _

  override def open(parameters: Configuration): Unit = {
    counter = getRuntimeContext()
      .getMetricGroup()
      .counter("myCounter")
  }

  override def map(value: String): String = {
    counter.inc()
    value
  }
}
```

Gauge:

```scala
new class MyMapper extends RichMapFunction[String,String] {
  @transient private var valueToExpose = 0

  override def open(parameters: Configuration): Unit = {
    getRuntimeContext()
      .getMetricGroup()
      .gauge[Int, ScalaGauge[Int]]("MyGauge", ScalaGauge[Int]( () => valueToExpose ) )
  }

  override def map(value: String): String = {
    valueToExpose += 1
    value
  }
}
```

[Metrics]:https://ci.apache.org/projects/flink/flink-docs-release-1.9/monitoring/metrics.html



已经集成到Dashboard：

![image-20210421140209482](imgs/flink/image-20210421140209482.png)