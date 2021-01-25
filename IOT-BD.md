## IOT
```Internet of Things```


[时序库排名](https://db-engines.com/en/ranking/time+series+dbms)

[几种时序库的测试 TimeSeriersDB-Benchmarks](https://github.com/micli/timeseriesdb-benchmarks/blob/main/docs/index.md)

[DolphinDB与InfluxDB对比测试报告](https://zhuanlan.zhihu.com/p/42287416)

# InfluxDB

# DolphinDB
[官方文档](https://www.dolphindb.cn/cn/help/index.html)


### 优劣
+ 性能极高(优于TDEngine和InfluxDb)
+ 贵、不支持分布式

# KDB+

# TD-Engine

物联网数据的特点：
1. 所有数据都是时序的
2. 数据是结构化的
3. 一个采集点的数据是唯一的
4. 更新、删除操作很少
5. 数据一般是按到期时间删除
6. 操作以写操作为主，读操作为辅
7. 数据流量平稳，可以较为准确的估算
8. 数据都有统计、聚合等实时计算
9. 数据是按时间段查询的
10. 数据量巨大

TD-Engine 物联网大数据全栈解决方案

`TD-Engine = 时序库 + 流式计算 + 缓存（实时数据/状态） + 数据订阅`

分布式：数据节点异步同步。一台设备一张表。

### 优劣
性能高(x10) 部署简单
集群版收费
