## 分布式替换表问题ReplicatedReplacingMergeTree

问题：运维中台中的告警数据没有替换

```sql
-- 告警数据
create table alarm.alarms_local ON CLUSTER perftest_3shards_2replicas
(
   AlarmId String,
   SourceId String,
   SourceTypeId Int32,
   SourceName String,
   StructureId Int32,
   InitialLevel Int32,
   CurrentLevel Int32,
   StartTime DateTime64(3, 'Asia/Shanghai'),
   EndTime DateTime64(3, 'Asia/Shanghai'),
   AlarmCount Int32,
   AlarmTypeCode LowCardinality(String),
   AlarmTypeId Int32,
   AlarmCode LowCardinality(String),
   AlarmContent String,
   State Int32,
   AlarmGroup Int32,
   AlarmGroupUnit Int32,
   AlarmAdviceProblem String,
   Notice String,
   SubDevices Array(String),
   Project Nullable(String)
)
engine = ReplicatedReplacingMergeTree ORDER BY AlarmId
PARTITION BY toYYYYMM(StartTime)
SETTINGS index_granularity = 8192;
CREATE TABLE alarm.alarms ON cluster perftest_3shards_2replicas AS alarm.alarms_local
ENGINE = Distributed(perftest_3shards_2replicas, alarm, alarms_local, rand());
```



该引擎与 MergeTree 的不同之处在于，它删除具有相同排序键值的重复条目（`ORDER BY` 表部分，而不是 `PRIMARY KEY` ）。

合并会在未知时间在后台发生，因此您无法对其进行计划。



需要设置版本列：

```sql
-- 告警数据
create table alarm.alarms_local ON CLUSTER perftest_3shards_2replicas
(
   AlarmId String,
   SourceId String,
   SourceTypeId Int32,
   SourceName String,
   StructureId Int32,
   InitialLevel Int32,
   CurrentLevel Int32,
   StartTime DateTime64(3, 'Asia/Shanghai'),
   EndTime DateTime64(3, 'Asia/Shanghai'),
   AlarmCount Int32,
   AlarmTypeCode LowCardinality(String),
   AlarmTypeId Int32,
   AlarmCode LowCardinality(String),
   AlarmContent String,
   State Int32,
   AlarmGroup Int32,
   AlarmGroupUnit Int32,
   AlarmAdviceProblem String,
   Notice String,
   SubDevices Array(String),
   Project Nullable(String)
)
engine = ReplicatedReplacingMergeTree(/clickhouse/tables/{shard}/{database}/{table},{replica},Version) ORDER BY AlarmId
PARTITION BY toYYYYMM(StartTime)
SETTINGS index_granularity = 8192;
CREATE TABLE alarm.alarms ON cluster perftest_3shards_2replicas AS alarm.alarms_local
ENGINE = Distributed(perftest_3shards_2replicas, alarm, alarms_local, rand());
```

