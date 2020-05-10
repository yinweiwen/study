
## Windows相关

### 环境准备
下载
`influxdb-1.7.9_windows_amd64` 
`chronograf-1.7.17_windows_amd64` 
`grafana-6.6.1.windows-amd64` 

解压后将目录加入系统环境变量 `influxd` 启动服务，`influx`进入CLI交互界面：

```shell
C:\Users\yww08>influx
Connected to http://localhost:8086 version 1.7.9
InfluxDB shell version: 1.7.9
> show databases
name: databases
name
----
_internal
```

解压运行chronograf，访问 [http://localhost:8888/](http://localhost:8888/)

### [TIGK技术栈](https://blog.csdn.net/liuajiehe1234567/article/details/81990461)
InfuluxDB go语言开发的时序数据库 时序数据库相比传统关系型数据库更关注数据的实时性和并发插入时的承受能力 开放有restfulAPI

Telegraf 同样是go语言开发的在目标机器上的agent采集工具，作为服务而言它很轻量级，并且扩展性也强，支持在linux系统下使用脚本对应用，容器等进行监控，监控采集的数据会发送给influxDB

Grafana 可视化的监控展示服务，提供包括折线图，饼图，仪表盘等多种监控数据可视化UI，支持多种不同的时序数据库数据源，Grafana对每种数据源提供不同的查询方法，而且能很好的支持每种数据源的特性。

Kapacitor TIGK技术栈的 __告警服务__，用户通过tickScript脚本来对时序数据库当中的数据进行过滤，筛选，批处理等进行告警，告警信息可以通过日志保存在本地，或回插到influxdb，还可以直接在告警产生后发起http请求到指定地址，kapacitor支持数据流（stream）和批处理（batch）数据


## [Node.js API](https://node-influx.github.io/class/src/index.js~InfluxDB.html)

```js
const Influx = require('influx');
const influx = new Influx.InfluxDB({
 host: 'localhost',
 database: 'express_response_db',
 username: 'root',
 password: '123'
 schema: [
   {
     measurement: 'response_times', // 类似数据表
     fields: { // 数据字段定义 FLOAT/INTEGER/STRING/BOOLEAN
       path: Influx.FieldType.STRING,
       duration: Influx.FieldType.INTEGER
     },
     tags: [
       'host'
     ]
   }
 ]
})

influx.writePoints([
  {
    measurement: 'response_times',
    tags: { host: os.hostname() },
    fields: { duration, path: req.path },
  }
]).then(() => {
  return influx.query(`
    select * from response_times
    where host = ${Influx.escape.stringLit(os.hostname())}
    order by time desc
    limit 10
  `)
}).then(rows => {
  rows.forEach(row => console.log(`A request to ${row.path} took ${row.duration}ms`))
})
```


## [InfluxQL](https://docs.influxdata.com/influxdb/v1.7/query_language/data_download/#download-and-write-the-data-to-influxdb)

[influxDB学习笔记](https://blog.csdn.net/vtnews/article/details/80197045)


```sql

导入数据
$ influx -import -path=NOAA_data.txt -precision=s -database=NOAA_water_database
$ influx -precision rfc3339 -database demo

插入数据
insert rainfall,sensorid=999 rain=2.5

查询`tem_hum_data`中`humi`不为空的个数
$ select count("humi") from tem_hum_data

查询`tem_hum_data`中时间最新的10条数据
select * from tem_hum_data order by time desc limit 10

重命名字段
select MEAN("water_level") AS "m_wat" from "h2o_feet"

选择子句
    选择所有field数据
 SELECT *::field FROM "h2o_feet"

 选择指定时间的数据
 SELECT * FROM "h2o_feet" WHERE time > now() - 7d

 GROUP BY
 GROUP BY time() 默认使用内置的时间划分区域
 SELECT <function>(<field_key>) FROM_clause WHERE <time_range> GROUP BY time(<time_interval>),[tag_key] [fill(<fill_option>)]

GROUP BY tags和time混用：例子
> SELECT MEAN("water_level") FROM "h2o_feet" WHERE time >= '2015-08-18T00:00:00Z' AND time <= '2015-08-18T00:42:00Z' GROUP BY *,time(12m) ORDER BY time DESC LIMIT 2 OFFSET 2 SLIMIT 1
`The SELECT clause specifies an InfluxQL function. The FROM clause specifies a single measurement. The WHERE clause specifies the time range for the query. The GROUP BY clause groups results by all tags (*) and into 12-minute intervals. The ORDER BY time DESC clause returns results in descending timestamp order. The LIMIT 2 clause limits the number of points returned to two. The OFFSET 2 clause excludes the first two averages from the query results. The SLIMIT 1 clause limits the number of series returned to one.
`


GROUP BY time intervals and fill()
fill() changes the value reported for time intervals that have no data.
如果聚集区段内没有值默认返回空，可以用fill改变返回内容:
1. 指定任何数值
2. linear 线性算法替换
3. none 不返回时间戳和空值
4. null  返回对应时间戳和空值
5. previous 返回上一粒度数据

INTO语法
The INTO clause writes query results to a user-specified measurement.

用INTO来复制一个数据库：
> SELECT * INTO "copy_NOAA_water_database"."autogen".:MEASUREMENT FROM "NOAA_water_database"."autogen"./.*/ GROUP BY *

后续引用的降采样 downsampling with backreferencing
> select MEAN(*) INTO "where_else"."autogen".:MEASUREMENT FROM /.*/ where time>'' and time <'' GROUP BY time(12m)

LIMIT和SLIMIT
LIMIT:限制返回的点数
SLIMIT：限制返回的序列数 series number
可以混用
select "water" from "hf" GROUP BY * LIMIT 3 SLIMIT 1

支持哪些聚集函数
count
distinct
integral 积分
    可以指定unit，默认1s
    SELECT INTEGRAL("water_level",1m) FROM "h2o_feet"
mean 平均
    > select mean(*) from tem_hum_data where sensorid='test06'
    name: tem_hum_data
    time                 mean_humi         mean_temp
    ----                 ---------         ---------
    1970-01-01T00:00:00Z 40.32099999999999 31.393857142857144
MEDIAN 中值
    Note: MEDIAN() is nearly equivalent to PERCENTILE(field_key, 50), 
MODE eturns the most frequent value in a list of field values.

SPREAD()
Returns the difference between the minimum and maximum field values.

STDDEV() 标准差

SUM 求和
聚集结果返回的时间默认是 epoch 0，如果where子句中包含下边界，结果时间为下边界（low time range）

有哪些选择器Selector
BOTTOM/TOP 最小的n个数
    select BOTTOM("humi",3),"sensorid","temp" from tem_hum_data;

FIRST/LAST 返回最早、新的一条数据
MAX/MIN
PERCENTILE Returns the Nth percentile field value.
    PERCENTILE(X,100) == MAX
    PERCENTILE(X,50) ≈ NEDIAN
    PERCENTILE(X,0) != MIN

SAMPLE(field,N) 返回N个随机数

预测机制Predictors
HOLT_WINTERS()

自带的科学分析器
CHANDE_MOMENTUM_OSCILLATOR()  钱德动量摆动指标
EXPONENTIAL_MOVING_AVERAGE()  指数滑动平均
DOUBLE_EXPONENTIAL_MOVING_AVERAGE()  双重指数移动平均线DEMA
KAUFMANS_EFFICIENCY_RATIO()
KAUFMANS_ADAPTIVE_MOVING_AVERAGE()
TRIPLE_EXPONENTIAL_MOVING_AVERAGE()
TRIPLE_EXPONENTIAL_DERIVATIVE()
RELATIVE_STRENGTH_INDEX()

Duration Literal 时间单位定义
u or µ microseconds 
ms     milliseconds 
s      seconds 
m      minutes 
h      hours 
d      days 
w      weeks
```

## Continuous Queries 连续查询

[官方文档](https://docs.influxdata.com/influxdb/v1.7/query_language/continuous_queries/)

```sql
CREATE CONTINUOUS QUERY <cq_name> ON <database>
[RESAMPLE [EVERY <interval>] [FOR <interval>]]
BEGIN
  SELECT <function>(<stuff>)[,<function>(<stuff>)]
  INTO <different_measurement>
  FROM <current_measurement> [WHERE <stuff>] GROUP BY time(<interval>)[,<stuff>]
END
```

连续聚集所有表到其他数据库
```sql
CREATE CONTINUOUS QUERY "cq_basic_br" ON "transportation"
BEGIN
  SELECT mean(*) INTO "downsampled_transportation"."autogen".:MEASUREMENT FROM /.*/ GROUP BY time(30m),*
END
```

OFFSET
```sql
CREATE CONTINUOUS QUERY "cq_basic_offset" ON "transportation"
BEGIN
  SELECT mean("passengers") INTO "average_passengers" FROM "bus_data" GROUP BY time(1h,15m)
END

RESULT:
At **8:15** `time >= '7:15' AND time < '8:15'`
At **9:15** `time >= '8:15' AND time < '9:15'`
```

RESAMPLE EVERY 每次执行的周期
```sql
CREATE CONTINUOUS QUERY "cq_advanced_every" ON "transportation"
RESAMPLE EVERY 30m
BEGIN
  SELECT mean("passengers") INTO "average_passengers" FROM "bus_data" GROUP BY time(1h)
END

RESULT:
At **8:00**'7:00' AND time < '8:00'`
At **8:30**'8:00' AND time < '9:00'`
```

RESAMPLE FOR 确定CQ数据时间范围 (在这个数据范围内进行select...into...操作) (for的周期不能小于groupby的周期，否则报错) （可以有意设置FOR大于GroupBy参数，防止丢失数据）
```sql
CREATE CONTINUOUS QUERY "cq_advanced_for" ON "transportation"
RESAMPLE FOR 1h
BEGIN
  SELECT mean("passengers") INTO "average_passengers" FROM "bus_data" GROUP BY time(30m)
END

RESULT:
At **8:00** '7:00' AND time < '8:00'` group by 30min -> 2 groups
At **8:30** '7:30' AND time < '8:30'` group by 30min -> 2 groups
```

RESAMPLE EVERY && FOR
```sql
CREATE CONTINUOUS QUERY "cq_advanced_every_for" ON "transportation"
RESAMPLE EVERY 1h FOR 90m
BEGIN
  SELECT mean("passengers") INTO "average_passengers" FROM "bus_data" GROUP BY time(30m)
END

RESULT:
At **8:00** '6:30' AND time < '8:00'` group by 30min -> 3 groups
At **9:00** '7:30' AND time < '9:00'` group by 30min -> 3 groups
```
显示所有连续查询
```sql
SHOW CONTINUOUS QUERIES
```

删除连续查询
```sql
DROP CONTINUOUS QUERY <cq_name> ON <database_name>
```

`在监测实例中的应用`：  
`需求`： `10min/时/日/周/月生成所有监测表中数据监测项的平均/最大/最小/均方根/变化幅值`
```sql
create database ten_min_agg
create database hour_agg
create database day_agg

-- TEST 'SELECT INTO' clause:
SELECT mean(*),max(*),min(*),SPREAD(*),STDDEV(*) INTO "ten_min_agg"."autogen".:MEASUREMENT FROM /.*/ where time>'2020-02-20 00:00:00' GROUP BY time(10m),*

`数据库`：`demo`

-- 10分钟持续聚集 (EVERY 10m)每10分钟执行 (FOR 1h)允许数据晚到1h内
CREATE CONTINUOUS QUERY "cq_ten_min_comm" ON "demo"
RESAMPLE EVERY 10m FOR 1h
BEGIN
  SELECT mean(*),max(*),min(*),spread(*),stddev(*) INTO "ten_min_agg"."autogen".:MEASUREMENT FROM /.*/ GROUP BY time(10m),*
END

-- 小时持续聚集 (EVERY 1h)每小时执行 (FOR 2h)允许数据晚到1h内
CREATE CONTINUOUS QUERY "cq_hour_comm" ON "demo"
RESAMPLE EVERY 1h FOR 2h
BEGIN
  SELECT mean(*),max(*),min(*),spread(*),stddev(*) INTO "hour_agg"."autogen".:MEASUREMENT FROM /.*/ GROUP BY time(1h),*
END

-- 天持续聚集 (EVERY 1h)每小时执行 (FOR 1d)
CREATE CONTINUOUS QUERY "cq_day_comm" ON "demo"
RESAMPLE EVERY 1h FOR 1d
BEGIN
  SELECT mean(*),max(*),min(*),spread(*),stddev(*) INTO "day_agg"."autogen".:MEASUREMENT FROM /.*/ GROUP BY time(1d),*
END
```


## DDL
[官方文档](https://docs.influxdata.com/influxdb/v1.7/query_language/database_management/)


## Kapacitor
[官方文档](https://docs.influxdata.com/kapacitor/v1.5/)

#### Key Feature

+ 处理批数据和流数据
+ 按计划查询InfluxDB中的数据，按照line protocol或其他influx支持的方法。
+ 进行InfluxQL支持的转换
+ 存储转换后的数据到InfluxDB
+ 增加用户定义函数检测异常
+ 和其他第三方工具集成：HipChat、Altera、Sensu、OagerDuty、Slack

定义告警脚本，如cpu_alert.tick:
```sql
dbrp "telegraf"."autogen"

stream
    // Select the CPU measurement from the `telegraf` database.
    |from()
        .measurement('cpu')
    // Triggers a critical alert when the CPU idle usage drops below 70%
    |alert()
        .crit(lambda: int("usage_idle") <  70)
        // Write each alert to a file.
        .log('/tmp/alerts.log')
```
`注意tick脚本中双引号和单引号的使用`：Double quotes denote data fields, single quotes string values, 如  `where(lambda: "host" == 'server1')`
```sql
# define  task:
kapacitor define cpu_alert -tick cpu_alert.tick
# show tasks:
kapacitor list tasks
# enable tasks
kapacitor enable cpu_alert
# show
kapacitor show cpu_alert
# 修改task
使用相同的task名称重新define，将更新并reload任务TICKscript
```


## 问题记录
#### influxdb unable to find time zone Asia/Shanghai
`windows`系统中可能出现此问题 [ref](https://blog.csdn.net/tony121lee/article/details/90899099)   解决方法：安装go

或者使用这种方法：
拷贝go安装后的 $GOROOT\lib\time\zoneinfo.zip  ,在目标机器设置$GOROOT重启influxd即可