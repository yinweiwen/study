## Promethus
入门参考：
[ref](https://www.cnblogs.com/chenqionghe/p/10494868.html)

[ref 2](https://www.jianshu.com/p/93c840025f01)

![pm1](.\img\pm1.jpg)

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

### PULL 模式
```yml
global:

  scrape_interval: 15s

  evaluation_interval: 15s

rule_files:

  # - "first.rules"

  # - "second.rules"

scrape_configs:

  - job_name: 'spring'

    metrics_path: '/actuator/prometheus'

    static_configs:

      - targets: ['自己本机ip:8080']
```
```bash
docker run --name prom --hostname prom -p 9090:9090 -v /Users/liukun/config/prometheus.yml:/etc/prometheus/prometheus.yml prom/prometheus
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

### Pushgateway


