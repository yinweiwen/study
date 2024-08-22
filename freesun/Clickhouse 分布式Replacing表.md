## Clickhouse 分布式Replacing表

在 ClickHouse 集群中使用 `ReplicatedReplacingMergeTree` 时，如果遇到相同主键数据不能正确替换的情况，这通常与集群配置、数据分片、以及查询策略有关。以下是可能的原因及解决方法：

数据分片和复制配置

在集群环境中，数据可能分布在多个节点上，`ReplicatedReplacingMergeTree` 在各个分片上独立运行。由于数据在不同的分片上可能不一致，`ReplicatedReplacingMergeTree` 只能在同一分片内处理数据替换。因此，如果你希望实现全局替换，需要确保数据具有相同的主键，并且这些数据落在同一分片上。

 **解决方法：**

- **使用 `Distributed` 引擎**：在查询时使用 `Distributed` 表，查询会自动分发到各个节点并汇总结果。尽管 `ReplicatedReplacingMergeTree` 只在本地节点上执行数据替换，但 `Distributed` 表能在最终查询时汇总这些替换后的数据。
- **使用自定义分片键**：通过配置合理的分片键（`sharding key`），确保相同主键的数据被路由到相同的分片上。例如，基于 `AlarmId` 进行分片，以保证相同 `AlarmId` 的记录在同一分片上。

重建表步骤说明：

```sql
RENAME TABLE alarm.alarms TO alarm.alarms_old;

-- 增加字段表示版本--EndTime
create table alarm.alarms_local_new ON CLUSTER perftest_3shards_2replicas
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
engine = ReplicatedReplacingMergeTree('/clickhouse/tables/{shard}/alarm/alarms_local_new3', '{replica}',EndTime) ORDER BY AlarmId
PARTITION BY toYYYYMM(StartTime)
SETTINGS index_granularity = 8192;

-- 通过cityHash64指定分布式表 alarms，使用 sharding_key 参数指定 AlarmId 作为分片键，以确保相同的 AlarmId 被路由到同一分片。
CREATE TABLE alarm.alarms ON cluster perftest_3shards_2replicas AS alarm.alarms_local_new
ENGINE = Distributed(perftest_3shards_2replicas, alarm, alarms_local_new, cityHash64(AlarmId));

INSERT INTO alarm.alarms
SELECT *
FROM alarm.alarms_old;

```

插入报错了“

```sh

[1002] ClickHouse exception, message: Code: 252. DB::Exception: Too many partitions for single INSERT block (more than 100). The limit is controlled by 'max_partitions_per_insert_block' setting. Large number of partitions is a common misconception. It will lead to severe negative performance impact, including slow server startup, slow INSERT queries and slow SELECT queries. Recommended total number of partitions for a table is under 1000..10000. Please note, that partitioning is not intended to speed up SELECT queries (ORDER BY key is sufficient to make range queries fast). Partitions are intended for data manipulation (DROP PARTITION, etc). (TOO_MANY_PARTS) (version 24.3.2.23 (official build)) , host: 218.3.126.49, port: 18125;
```



分开时间段执行（太多partition了）

```sql
INSERT INTO alarm.alarms
SELECT *
FROM alarm.alarms_old
WHERE EndTime > '2024-08-01 00:00:00';

INSERT INTO alarm.alarms
SELECT *
FROM alarm.alarms_old
WHERE EndTime > '2024-05-01 00:00:00' AND '2024-06-01 00:00:00';
INSERT INTO alarm.alarms
SELECT *
FROM alarm.alarms_old
WHERE EndTime > '2024-06-01 00:00:00' AND '2024-07-01 00:00:00';
INSERT INTO alarm.alarms
SELECT *
FROM alarm.alarms_old
WHERE EndTime > '2024-07-01 00:00:00' AND '2024-08-01 00:00:00';

OPTIMIZE TABLE alarm.alarms FINAL;
-- 报错了，分布式表不支持手动OPTIMIZE
```



这样就可以正确使用 ReplicatedReplacingMergeTree。