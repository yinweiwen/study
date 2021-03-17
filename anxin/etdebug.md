# AXY3.0数据积压导致数据中断 2021-2
## 现象
安心云平台数据出现大面积中断。收到监控报警短信提示： `【安心云】于2021-02-25 19:56:12  平台：安心云3.0平台 详情： 数据中断结构物超过40个`

## 排查
### Step 1 问题定位 <2020-02-20>
1. 查看ET日志，均为正常。但日志中最新处理数据时间为中断点时间
2. 查看kafka消息消费情况
```shell
root@anxinyun-m1:/usr/hdp/current/kafka-broker/bin# ./kafka-consumer-groups.sh --new-consumer --bootstrap-server anxinyun-n1:6667 --group et.mainx --describe
GROUP                          TOPIC                          PARTITION  CURRENT-OFFSET  LOG-END-OFFSET  LAG             OWNER
et.mainx                       anxinyun_upload                0          2544752         2544752         0               consumer-1_/192.168.0.205
et.mainx                       anxinyun_alarmMsg              0          55943156        55943156        0               consumer-1_/192.168.0.205
et.mainx                       anxinyun_data                  0          369219350       369219351       14123           consumer-1_/192.168.0.205
et.mainx                       anxinyun_agg                   0          2680692         2680692         0               consumer-1_/192.168.0.205
et.mainx                       anxinyun_data                  1          72797679        72797683        40000           consumer-1_/192.168.0.205
et.mainx                       anxinyun_data                  2          72790524        72790525        12345           consumer-1_/192.168.0.205
et.mainx                       anxinyun_capture_data          0          24              24              0               consumer-2_/192.168.0.205
```
3. 如上显示是anxinyun_data消费者出现了较大LAG，即积压。那问题应该是kafka topic anxinyun_data（以太数据）的消费者 __et__ 出现了性能瓶颈问题。 到et所在服务器节点查看进程内存和CPU(~200% 4G)。 

### Step 2 <2020-02-26>
以上不能定位具体问题，考虑到积压性能可能是由于部分设备数据频率过高或整体数据规模上涨，导致ET处理上的阻塞。打算通过prometheus提取et中的关键性能指标。

首先引入`io.prometheus.simpleclient`包。在et进程中作以下指标的exporter，实现统计
  1. 采集处理相关 (设备以及测点理论/实际数据量)
  2. 应用性能指标(JVM GC/CPU/MEM)
  3. Kafka各主题消费情况Lag

部署Prometheus
[svn](FS-Anxinyun\trunk\codes\services\prometheus\axy-prometheus\k8s)

1. 使用k8s部署prometheus，配置prometheus.yml
```yml
# my global config
global:
  scrape_interval:     15s # Set the scrape interval to every 15 seconds. Default is every 1 minute.
  evaluation_interval: 15s # Evaluate rules every 15 seconds. The default is every 1 minute.
  
rule_files:
  # - "first.rules"
  # - "second.rules"

# A scrape configuration containing exactly one endpoint to scrape:
# Here it's Prometheus itself.
scrape_configs:
  # The job name is added as a label `job=<job_name>` to any timeseries scraped from this config.
  # Others
    # metrics_path defaults to '/metrics'
    # scheme defaults to 'http'.
  - job_name: 'et'
    static_configs:
      - targets: ['anxinyun-et:8899']
```
2. 可在 http://192.168.0.206:19090/ 查询响应指标

通过PromQL查询关键指标
+ `topk(10,raw_data_count_total)` 查询数据最多前十个
+ `topk(10,delta(raw_data_count_total[1h]))` : 查询每小时数据量前十的设备
+ `jvm_threads_current` 当前线程数
+ `jvm_buffer_pool_`  jvm buffer pool
+ `kafka_lag_count{topic="anxinyun_data"}` 当前kafka消费积压
+ `topk(10,sum(raw_data_count_total) by (thing))` 数据最多的10个结构物

## 解决思路
1. 如果是部分高频数据的接入导致： 限制/关闭高频数据的接入
2. 如果是整体数据规模提升： 使用 `et-flink`

## 具体解决
待进一步排查