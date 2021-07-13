## 问题描述：
mqtt接收消息超过500k时；接收进程连接断开，之后一直尝试重连
## 问题排查过程：
1. 使用不同的mqtt配置参数和尝试不同的mqtt代理：

		clean.session:false QoS:2 代理Mosquitto  x
		clean.session:false QoS:1 代理Mosquitto  x
		clean.session:false QoS:0 代理Mosquitto  x
		clean.session:false QoS:2 代理EMQ  x 
		clean.session:false QoS:1 代理EMQ  x
		clean.session:false QoS:0 代理EMQ  x

2. 修改发送端的QoS，同样无效

3. 怀疑是不是代理那里有数据包大小限制，修改EMQX的mqtt.max_packet_size=100MB， 同样无效
```yaml
 env:
	- name: EMQX_MQTT__MAX_PACKET_SIZE
	  value: 100MB
```
	
4. 难道用的mqtt-client java库有bug。尝试更换java MQTT-client库，__Eclipse Paho__ ->__FuseSource MqttClient__
	https://github.com/eclipse/paho.mqtt.java
	https://github.com/fusesource/mqtt-client
	https://www.jianshu.com/p/95b4e349cde4
	

    更换后mqtt接受不再报错，但是kafka报错了：
```
java.util.concurrent.ExecutionException: org.apache.kafka.common.errors.RecordTooLargeException: The request included a message larger than the max message size the server will accept.
```
kafka服务无法接收超过最大限制的数据包，于是[谷歌](https://my.oschina.net/shyloveliyi/blog/1620012)后修改

配置kafka server,重启kafka服务：
```yaml
		message.max.bytes=12695150
```
 __配置后错误没有了！！__

基于以上测试结果，于是在代码里用工厂模式实现了两种库的调用，开始自测，结果两种库的实现 __竟然__ 都能实现1M以上包的传输了；

怀疑是kafka的那个异常被Paho内部吃掉了，导致Socket异常重启，于是kafka设置改回去再测试，果然Paho又报错开始重连了

## 解决办法:
kafkaProducer.send方法用 try-catch包住

## 引申：
之前进程也时常出现重连的问题，应该也是kafka-send超时之类的问题导致的.
## 总结：
这是一个异常未捕捉引发的血案，所以一切IO操作一定要做好异常处理，不然被第三方包吃掉异常，故障就真的不好定位了。