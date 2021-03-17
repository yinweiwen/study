JAVA&SCALA
=====

## JAVA引用webservice:
wsdl2java.bat -p wsdl.jiangsu -d wsdljiangsu  -encoding utf-8 -client http://58.213.116.130:9001/MonitorService-1.0/services/MonitorService?wsdl

wsdl2java.bat -p wsdl.jiangsu -d wsdljiangsutest  -encoding utf-8 -client http://61.132.52.36:9001/MonitorService-1.0/services/MonitorService?wsdl

## JVM 内存
[ref](https://www.cnblogs.com/likehua/p/3369823.html)
        常见配置汇总
        堆设置
        -Xms:初始堆大小
        -Xmx:最大堆大小
        -XX:NewSize=n:设置年轻代大小
        -XX:NewRatio=n:设置年轻代和年老代的比值。如:为3，表示年轻代与年老代比值为1：3，年轻代占整个年轻代年老代和的1/4
        -XX:SurvivorRatio=n:年轻代中Eden区与两个Survivor区的比值。注意Survivor区有两个。如：3，表示Eden：Survivor=3：2，一个Survivor区占整个年轻代的1/5
        -XX:MaxPermSize=n:设置持久代大小
        收集器设置
        -XX:+UseSerialGC:设置串行收集器
        -XX:+UseParallelGC:设置并行收集器
        -XX:+UseParalledlOldGC:设置并行年老代收集器
        -XX:+UseConcMarkSweepGC:设置并发收集器
        垃圾回收统计信息
        -XX:+PrintGC
        -XX:+PrintGCDetails
        -XX:+PrintGCTimeStamps
        -Xloggc:filename
        并行收集器设置
        -XX:ParallelGCThreads=n:设置并行收集器收集时使用的CPU数。并行收集线程数。
        -XX:MaxGCPauseMillis=n:设置并行收集最大暂停时间
        -XX:GCTimeRatio=n:设置垃圾回收时间占程序运行时间的百分比。公式为1/(1+n)
        并发收集器设置
        -XX:+CMSIncrementalMode:设置为增量模式。适用于单CPU情况。
        -XX:ParallelGCThreads=n:设置并发收集器年轻代收集方式为并行收集时，使用的CPU数。并行收集线程数。
		
		
		  目前ET中数据上报多采用调用wenservice服务的方式，包括青岛数据上报和江苏省级平台数据上报。
  第三方服务提供WSDL uri路径，在java/scala程序中可以通过工具解析出客户端代码，方便程序调用。
  
  
#### 使用 apache的wsdl2java工具

[下载地址](http://cxf.apache.org/download.html)

```
wsdl2java.bat -p wsdl.jiangsu \
-d wsdljiangsutest  \
-encoding utf-8 \
-client http://xxx:9001/MonitorService-1.0/services/MonitorService?wsdl
```
	-p : 指定包名
	-d ：指定生成文件目录
	-client ：生成客户端测试代码
	-server ：生成服务端测试代码
	-encoding utf-8：解决中文乱码问题
	最后的参数是wsdl文件所在的URL

生成的文件主要有：
+ 服务主类
+ ObjectFactory 参数工厂类
+ 其他数据接口和模型

ObjectFactory用于创建ws所使用的数据格式，例如将scala中string转换为ws中的字符串类型`JAXBElement`，可以增加如下隐式转换代码：
```scala
implicit def stringToJaxb(value:String) = new ObjectFactory().createString(value)
```
另外，生成的MonitorService构造函数内包含ws服务地址，所以测试时候可以构造测试环境的service，往测试平台上报数据，部分代码：
```scala
def testUpload(): Unit ={
		jiangsu.wsdlLocation=Some(new URL("""http://xxx:9001/MonitorService-1.0/services/MonitorService?wsdl"""))
		jiangsu.init()
		val stId=jiangsu.config.structId
		val sd1=StationData(Station("YL-Y1-B-1",1,
			Structure("thingid",stId,"test-struct"),null,
			Factor(1,"应变","3001","应变",null),false),DateTime.parse("2018-06-14T01:00:00Z"),
			"taskid",Some(mutable.Map(Map[String,Double]("strain"->1.25).toSeq: _*)))
		jiangsu.upload(sd1)
	}
```
```scala
...
var ss: MonitorService =new MonitorService(wsdlLocation.get)
...
```


## scala par 指定线程数
val ses = ...().par

ses.tasksupport = new ForkJoinTaskSupport(new ForkJoinPool(4))

ses.map{case (h, (start, end)) => ..... 
