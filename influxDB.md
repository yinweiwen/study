# InfluxDB

[DB Rank](https://db-engines.com/en/ranking)

[InfluxDB特点](https://www.jianshu.com/p/5968e7aa1e1f)
+ 专为时间序列数据编写的自定义高性能数据存储。TSM引擎允许高摄取速度和数据压缩
+ 完全GO编写无外部依赖
+ 简单/高效的写入和查询HTTP API
+ 插件支持其他数据提取协议 如Graphite collectd和OpenTSDB
+ 专为类似SQL的查询语言量身定制
+ 标签Tag允许对系列进行索引以实现高效查询
+ 保留策略有效使过时数据过期
+ 连续查询自动计算聚合数据，提高频繁查询的效率

-----

## GET-STARTED
[offical](https://v2.docs.influxdata.com/v2.0/get-started/)
### 安装

```shell
wget https://dl.influxdata.com/influxdb/releases/influxdb_1.7.7_amd64.deb
sudo dpkg -i influxdb_1.7.7_amd64.deb
```

V2.0
```shell
# Unpackage contents to the current working directory
tar xvzf path/to/influxdb_2.0.0-alpha.21_linux_amd64.tar.gz

# Copy the influx and influxd binary to your $PATH
sudo cp influxdb_2.0.0-alpha.21_linux_amd64/{influx,influxd} /usr/local/bin/

```

### 启动

`sudo influxd`

启动：chronograf
 `docker run -p 8888:8888 -d chronograf:latest`

### Set up InfluxDB | CLI(command line interface)

#### CLI
V1.7.7

`influx`

`influx -precision rfc3339`
```shell
yww@yww-GE62-2QC:~$ influx
Connected to http://localhost:8086 version 1.7.7
InfluxDB shell version: 1.7.7
> 


    # Use influx in a non-interactive mode to query the database "metrics" and pretty print json:
    $ influx -database 'metrics' -execute 'select * from cpu' -format 'json' -pretty

    # Connect to a specific database on startup and set database context:
    $ influx -database 'metrics' -host 'localhost' -port '8086'
```

#### 操作

https://jasper-zhang1.gitbooks.io/influxdb/content/Query_language/schema_exploration.html

补充笔记 https://www.yuque.com/xingo/iiul0g/edif49

```shell
## 数据库操作
show databases
create database xx
drop database xx
use database

## 数据表操作
show measurements
## 创建表
insert disk_free,hostname=server01 value=312421i
## 删除表
drop measurement disk_free

##  SHOW SERIES


# 数据保存策略
show retention policies on "db_name"

name    duration shardGroupDuration replicaN default
----    -------- ------------------ -------- -------
autogen 0s       168h0m0s           1        true
duration - 持续时间，0代表无限制
shardGroupDuration--shardGroup的存储时间，shardGroup是InfluxDB的一个基本储存结构，应该大于这个时间的数据在查询效率上应该有所降低
replicaN--全称是REPLICATION，副本个数
default--是否是默认策略

create retention policy "rp_name" on "db_name" duration 3w replication 1 default

ALTER RETENTION POLICY "autogen" ON "IotaRaw" DURATION 1825d DEFAULT
```

#### CQ 连续查询
```sql
CREATE CONTINUOUS QUERY <cq_name> ON <database_name>
[RESAMPLE [EVERY <interval>] [FOR <interval>]]
BEGIN SELECT <function>(<stuff>)[,<function>(<stuff>)] INTO <different_measurement>
FROM <current_measurement> [WHERE <stuff>] GROUP BY time(<interval>)[,<stuff>]
END

CREATE CONTINUOUS QUERY wj_30m ON shhnwangjian BEGIN SELECT mean(connected_clients), MEDIAN(connected_clients), MAX(connected_clients), MIN(connected_clients) INTO redis_clients_30m FROM redis_clients GROUP BY ip,port,time(30m) END

```

在FSLocal系统中应用
```sql
create database ten_min_agg;

-- 数据库 ${db}

-- 10分钟持续聚集 (EVERY 10m)每10分钟执行 (FOR 1h)允许数据晚到1h内
CREATE CONTINUOUS QUERY "cq_ten_min_comm" ON "${db}"
RESAMPLE EVERY 10m FOR 1h
BEGIN
  SELECT mean(*),max(*),min(*),spread(*),stddev(*) INTO "ten_min_agg"."autogen".:MEASUREMENT FROM /.*/ GROUP BY time(10m),*
END;
```
V2.0

WEB UI   http://localhost:9999/

------

## 概念

[ref](https://www.cnblogs.com/shhnwangjian/p/6897216.html?utm_source=itdadao&utm_medium=referral)

### Line protocol
`mem,host=host1 used_percent=23.43234543 1556892576842902000`

+ database/buckets 不同数据库中数据文件隔离存放
+ retention policy 存储策略（数据保存时间/集群副本数量/shard group覆盖时间范围）
+ tags（tag sets） key/value键值对 数据的key
+ fields key/value键值对 对应数据
+ messurement 作为tags
+ point由时间错(time)/数据(field)/标签(tag)组成
+ series 同一databse/retention policy/messurement/tag sets完全相同的数据属于一个series，同一series在物理上按照时间顺序存储在一起
+ shard 和retention policy相关联。数据按时间戳划分不同的时间段，创建不同的Shard，每一个shard都有自己的cache/wal/tsm file/compactor. (shard group > shard > series)
+ shard duration 
+ shard group
+ Continuous Query (CQ)是预先置好的查询命令，定期执行并将结果写入制定的messurement中。这个功能主要用于数据聚集
+ 存储引擎(TSM) 
    类似`LSM`树
    + Cache 是Wal中所有数据的内存副本。
    + Wal (Write Ahead Log) wal文件，系统崩溃恢复
    + TSM File: 用于存放数据，单个tsm file最大为2GB
    + Compactor 压缩服务。定时将cache写入tsm,或将多个小tsm文件合并成一个（删除过期数据）

对应传统数据库概念：
+ database -> 数据库
+ messurement -> 表
+ points -> 记录
+ point中time -> 每条数据记录的时间，数据库中的主索引
+ point中field -> 各种记录值(没有索引的)
+ point中tags -> 各种有索引的记录值

UI中的相关概念：
+ Data Explorer 数据查询界面(支持fiter/agg等)
+ Dashboards 创建数据仪表盘
+ Task 定时任务
+ Monitoring & Altering 可以定义中断和阈值警告，定义Notification Endpoints和rules。
+ LoadData
    + buckets 数据库
    + scraper [create scaper endpoint](https://v2.docs.influxdata.com/v2.0/write-data/scrape-data/scrapable-endpoints/)
    + telegraf 插件定义，输出token和telegraf的config配置文集
    + client libraries C#/node.js/java等SDK


### [Telegraf](https://docs.influxdata.com/telegraf/v1.13/introduction/getting-started/)
[install](https://docs.influxdata.com/telegraf/v1.13/introduction/installation/)
```shell
wget -qO- https://repos.influxdata.com/influxdb.key | sudo apt-key add -
source /etc/lsb-release
echo "deb https://repos.influxdata.com/${DISTRIB_ID,,} ${DISTRIB_CODENAME} stable" | sudo tee /etc/apt/sources.list.d/influxdb.list

sudo apt-get update && sudo apt-get install telegraf
sudo service telegraf start

## 配置
/etc/telegraf.conf
或创建一个
telegraf -sample-config -input-filter cpu:mem -output-filter influxdb > telegraf.conf

## 启动
telegraf --config telegraf.conf
sudo service telegraf start
systemctl start telegraf

## 查看结果
在influxdb中查看telegraf数据库
> SHOW MEASUREMENTS
> SHOW FIELD KEYS
```

##### 相关概念
+ messurement name: description and namespace for the metric
+ tags: key/value string pairs and usually used to identify the metric
+ fields: key/value pairs that are typed and usually contain the metric data
+ timestamp: Date and time associated with the fields


## 写入数据

```
web,host=host2,region=us_west firstByte=15.0 1559260800000000000
--- -------------------------                -------------------
 |               |                                    |
Measurement   Tag set                             Timestamp
```
重复的数据(相同的measurement/tags/timestamp)会进行组合(union),

优化写入：
+ Batch Writes （The optimal batch size is 5000 lines of line prootcol）
+ 写入之前 按tag key的字母排序(sort tags by key in lexicographic order. 
+ 使用粗力度的时间精度(coarsest precision)

## 查询

```sql
from(bucket:"example-bucket")
  |> range(start:-1h)
  |> filter(fn:(r) =>
    r._measurement == "cpu" and
    r.cpu == "cpu-total"
  )
  |> aggregateWindow(every: 1m, fn: mean)
```
+ 在UI中执行
+ REPL （read-eval-print-loop）
```js
influx repl -o freesun --token a33_t4NX1-sToPNr-Ca8Y_PGW3Qd5frcPBigdIbM-JxoAbupxC0t8tOO9Sy86hrmuNbT4-JObu7rueZNuGe3Hg==

> from(bucket:"demo") |> range(start:-1h) |> filter(fn:(r)=>r._measurement=="cpu" and r.cpu=="cpu-total") |> aggregateWindow(every:1m,fn:mean)

```
+ influx query @/path/to/query.flux
+ InfluxDB API
```js
curl http://localhost:9999/api/v2/query?org=my-org -XPOST -sS \
  -H 'Authorization: Token YOURAUTHTOKEN' \
  -H 'Accept: application/csv' \
  -H 'Content-type: application/vnd.flux' \
  -H 'Accept-Encoding: gzip' \
  -d 'from(bucket:"example-bucket")
        |> range(start:-1000h)
        |> group(columns:["_measurement"], mode:"by")
        |> sum()'
```

CRUD

```sql
show databases

use "testData"

SELECT * FROM "testData"."autogen"."t_themes_vibration_wave" WHERE  "sensorid"='259' and time>'2021-06-10'

delete from t_themes_vibration_wave where time between '2021-06-10T00:00:00+08:00' and '2021-06-15T00:00:00+08:00'
```



## 关于性能

https://www.jianshu.com/p/979d8853210d

单节点 60w pps的写入吞吐量

## Prometheus

[Doc](https://prometheus.io/docs/introduction/overview/)
Features
+ 开源监测和报警工具 CloudNativeComputingFoundation
+ 多维度时序数据
+ PromQL 弹性查询语句
+ 自主单节点，不依赖分布式存储
+ 采集通过HTTP的pull模式
+ 支持推送（通过实时gateway）
+ 通过静态配置或服务发现发现目标
+ 多种类型图形和仪表支持

Components
+ Prometheus server
+ client libraries(SDK)
+ push gateway ...

![architecture](imgs/promethus.png)

二战题材：
穿条纹睡衣的男孩
少年乔乔的异想世界
钢琴家
美丽人生