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

```

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



