## Promethus
入门参考：

[中文文档](https://fuckcloudnative.io/prometheus/2-concepts/data_model.html)

[从零搭建Prometheus监控报警系统
](https://www.cnblogs.com/chenqionghe/p/10494868.html)

[Prometheus ref 2](https://www.jianshu.com/p/93c840025f01)

![pm1](.\img\pm1.jpg)
![pm_everywhere](.\img\pm_architecture.png)

支持任意exporter（HTTP接口）  
+ Prometheus Daemon负责定时去目标上抓取`metrics(指标)`数据，每个抓取目标需要暴露一个http服务的接口给它定时抓取。Prometheus支持通过配置文件、文本文件、Zookeeper、Consul、DNS SRV Lookup等方式指定抓取目标。Prometheus采用PULL的方式进行监控，即服务器可以直接通过目标PULL数据或者间接地通过中间网关来Push数据。
+ Prometheus在本地存储抓取的所有数据，并通过一定规则进行清理和整理数据，并把得到的结果存储到新的时间序列中。
+ Prometheus通过PromQL和其他API可视化地展示收集的数据。Prometheus支持很多方式的图表可视化，例如Grafana、自带的Promdash以及自身提供的模版引擎等等。Prometheus还提供HTTP API的查询方式，自定义所需要的输出。
+ PushGateway支持Client主动推送metrics到PushGateway，而Prometheus只是定时去Gateway上抓取数据。
+ Alertmanager是独立于Prometheus的一个组件，可以支持Prometheus的查询语句，提供十分灵活的报警方式。

Metric类型：
+ Counter 累加型。 请求数、错误数、任务数等
+ Gauge 瞬时值。 温度等
+ Histogram 柱状图。统计类，如请求持续时间、响应大小，进行分组统计
+ Summary 类似Histogram，只是它不用计算

PromQL DSL
+ http_requests_total 瞬时数据
+ http_requests_total[5m] 区间数据
+ const(http_requests_total) 纯量数据
+ logback_events_total{level=~"in.."} 查询条件 正则
+ sum(http_requests_total{method="GET"} offset 5m) 偏移
+ 聚合 count/sum/avg/topk/irate

```more
//通过rate()函数获取HTTP请求量的增长率
rate(http_requests_total[5m])
//查询当前系统中，访问量前10的HTTP地址
topk(10, http_requests_total)
// 计算 CPU 温度在两小时内的差异
dalta(cpu_temp_celsius{host="zeus"}[2h])

```

## 四种指标类型

引用 [Blog](https://blog.csdn.net/qq_26531719/article/details/112391592)

### Counter

counter 是一个累积计数的指标，仅支持增加或者重置为0（只增不减 ）

统计服务请求数量、任务数量、出错数量

```shell
//通过rate()函数获取HTTP请求量的增长率
rate(http_requests_total[5m])
//查询当前系统中，访问量前10的HTTP地址
topk(10, http_requests_total)
```



### Gauge

gauge是一个纯数值型的能够经常进行 __增加或者减少__ 的指标

温度、内存cpu使用量等

```shell
dalta(cpu_temp_celsius{host="zeus"}[2h]) //计算CPU温度在两小时内的差异
predict_linear(node_filesystem_free{job="node"}[1h], 4*3600) //预测系统磁盘空间在4小时之后的剩余情况
```



### Histogram

histogram 在一段时间内进行采样，并能够对指定区间以及总数进行统计.。histogram会有一个基本的指标名称<basename>,由以下几部分组成

- <basename>_bucket{le="<upper inclusive bound>"}       用来统计满足指标的情况

```shell
# 在总共2次请求当中。http请求响应时间 <=0.005 秒 的请求次数为0
io_namespace_http_requests_latency_seconds_histogram_bucket{path="/",method="GET",code="200",le="0.005",} 0.0
# 在总共2次请求当中。http请求响应时间 <=0.01 秒 的请求次数为0
io_namespace_http_requests_latency_seconds_histogram_bucket{path="/",method="GET",code="200",le="0.01",} 0.0
```

- <basename>_sum             值的总和

```shell
# 实际含义： 发生的2次http请求总的响应时间为13.107670803000001 秒
io_namespace_http_requests_latency_seconds_histogram_sum{path="/",method="GET",code="200",} 13.107670803000001
```

- <basename>_count            请求总数

```shell
# 实际含义： 当前一共发生了2次http请求
io_namespace_http_requests_latency_seconds_histogram_count{path="/",method="GET",code="200",} 2.0
```

累计直方图

### Summary

summary与histogram类似，用于表示一段时间内的采样数据，但它直接存储了分位数，而不是通过区间来计算。

Summary与Histogram相比，存在如下区别：

- 都包含 < basename>_sum和< basename>_count;
- Histogram需要通过< basename>_bucket计算quantile，而Summary直接存储了quantile的值

summary 会有一个基本的指标名称<basename>,由以下几部分组成

- <basename>{quantile="<φ>"}

```shell
# 含义：这12次http请求响应时间的中位数是3.052404983s
io_namespace_http_requests_latency_seconds_summary{path="/",method="GET",code="200",quantile="0.5",} 3.052404983
# 含义：这12次http请求响应时间的9分位数是8.003261666s
io_namespace_http_requests_latency_seconds_summary{path="/",method="GET",code="200",quantile="0.9",} 8.003261666
```

- <basename>_sum

```shell
#含义：这12次http请求的总响应时间为 51.029495508s
io_namespace_http_requests_latency_seconds_summary_sum{path="/",method="GET",code="200",} 51.029495508
```

- <basename>_count

```shell
# 含义：当前http请求发生总次数为12次
io_namespace_http_requests_latency_seconds_summary_count{path="/",method="GET",code="200",} 12.0
```

## Histogram 和 summary的区别

1. Summary 的分位数是直接在**客户端**计算完成的，处理过程有频繁的全局锁操作，对高并发程序性能存在一定影响。histogram仅仅是在客户端给每个桶做一个原子变量的计数就可以了。Summary 会占用更多的客户端的cpu和内存。
2. 在服务端，不能对Summary产生的quantile值进行aggregation运算（例如sum, avg等），histogram可以进行各种操作。因此对服务端的消耗，histogram是大于Summary的。
3. histogram**存储的是区间的样本数统计值，**不能得到精确的分为数，而Summary可以。

## 安装

`prometheus.yml`
```yml
global:

  scrape_interval: 15s

  evaluation_interval: 15s

rule_files:

  # - "first.rules"

  # - "second.rules"

scrape_configs:

  - job_name: 'demo'

    metrics_path: '/prometheus'

    static_configs:

      - targets: ['192.168.0.10:8080']
```

docker run
```bash
docker run --name prom --hostname prom -p 9090:9090 -v prometheus.yml:/etc/prometheus/prometheus.yml prom/prometheus

# simply:
docker run -p 9098:9090 -v /home/anxin/prometheus/prometheus.yml:/etc/prometheus/prometheus.yml prom/prometheus
```

k8s run
`Dockerfile`
```Dockerfile
FROM prom/prometheus:latest
USER root:root
ENV TZ=Asia/HongKong
RUN ln -sf /usr/share/zoneinfo/Hongkong /etc/localtime
COPY ./resources/ /etc/prometheus
EXPOSE 19090
ENTRYPOINT [ "/bin/prometheus" ]
CMD        [ "--config.file=/etc/prometheus/prometheus.yml", \
             "--web.listen-address=:19090", \
             "--query.lookback-delta=7d"]
```

[Prometheus/client_java](https://github.com/prometheus/client_java#exporting-to-a-pushgateway)

```java
class YourClass {
  static final Counter requests = Counter.build()
     .name("my_library_requests_total").help("Total requests.")
     .labelNames("method").register();

  void processGetRequest() {
    requests.labels("get").inc();
    // Your code here.
  }
}

// 内置的一些collector
DefaultExports.initialize(); // FC MEM THREAD etc.

// Exporting

```
## PromQL

表达式类型
+ 瞬时向量（Instant vector） - 一组时间序列，每个时间序列包含单个样本，它们共享相同的时间戳。也就是说，表达式的返回值中只会包含该时间序列中的最新的一个样本值。而相应的这样的表达式称之为瞬时向量表达式。
+ 区间向量（Range vector） - 一组时间序列，每个时间序列包含一段时间范围内的样本数据。
+ 标量（Scalar） - 一个浮点型的数据值。
+ 字符串（String） - 一个简单的字符串值。


