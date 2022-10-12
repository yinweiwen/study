## 实战

REF：**[利用clickhouse-local实现MergeTree数据文件的导入](https://www.aboutyun.com/thread-30780-1-1.html)**

```shell
# Server端
CREATE TABLE test_batch (
  id Int64,
  EventTime Date
) ENGINE =  MergeTree()
PARTITION BY toYYYYMM(EventTime)
ORDER BY id

INSERT INTO TABLE test_batch SELECT number,'2021-04-05' FROM `system`.numbers LIMIT 10000

SELECT
    partition,
    name,
    rows
FROM system.parts
WHERE table = 'test_batch'

Query id: 90bbe5aa-30e8-43e6-91be-8c70869e7a11

┌─partition─┬─name─────────┬──rows─┐
│ 202104    │ 202104_1_1_0 │ 10000 │
└───────────┴──────────────┴───────┘

1 rows in set. Elapsed: 0.003 sec.


# Local端
echo -e "1\n2\n3" |  clickhouse-local -S "id Int64" -N "tmp_table" -q "CREATE TABLE test_batch (id Int64,EventTime Date) ENGINE = MergeTree() PARTITION BY toYYYYMM(EventTime) ORDER BY id;INSERT INTO TABLE test_batch SELECT id,'2021-05-05' FROM tmp_table;" --logger.console  --path /tmp/local


# Server端
mv  /tmp/local/data/_local/test_batch/202105_1_1_0/ /var/lib/clickhouse/data/default/test_batch/detached/

ALTER TABLE test_batch ATTACH PART '202105_1_1_0'
```



## 定义分区

```sql
SELECT
    partition,
    name,
    active
FROM system.parts
WHERE table = 'visits'
```



```text
┌─partition─┬─name───────────┬─active─┐
│ 201901    │ 201901_1_3_1   │      0 │
│ 201901    │ 201901_1_9_2   │      1 │
│ 201901    │ 201901_8_8_0   │      0 │
│ 201901    │ 201901_9_9_0   │      0 │
│ 201902    │ 201902_4_6_1   │      1 │
│ 201902    │ 201902_10_10_0 │      1 │
│ 201902    │ 201902_11_11_0 │      1 │
└───────────┴────────────────┴────────┘
```



`partition` 列存储分区的名称。此示例中有两个分区：`201901` 和 `201902`。在 [ALTER … PARTITION](https://clickhouse.com/docs/zh/engines/table-engines/mergetree-family/custom-partitioning-key/#alter_manipulations-with-partitions) 语句中你可以使用该列值来指定分区名称。

`name` 列为分区中数据片段的名称。在 [ALTER ATTACH PART](https://clickhouse.com/docs/zh/engines/table-engines/mergetree-family/custom-partitioning-key/#alter_attach-partition) 语句中你可以使用此列值中来指定片段名称。

这里我们拆解下第一个数据片段的名称：`201901_1_3_1`：

- `201901` 是分区名称。
- `1` 是数据块的最小编号。
- `3` 是数据块的最大编号。
- `1` 是块级别（即在由块组成的合并树中，该块在树中的深度）。

!!! info "注意" 旧类型表的片段名称为：`20190117_20190123_2_2_0`（最小日期 - 最大日期 - 最小块编号 - 最大块编号 - 块级别）。

`active` 列为片段状态。`1` 代表激活状态；`0` 代表非激活状态。非激活片段是那些在合并到较大片段之后剩余的源数据片段。损坏的数据片段也表示为非活动状态。

正如在示例中所看到的，同一分区中有几个独立的片段（例如，`201901_1_3_1`和`201901_1_9_2`）。这意味着这些片段尚未合并。ClickHouse 会定期的对插入的数据片段进行合并，大约是在**插入后15分钟**左右。此外，你也可以使用 [OPTIMIZE](https://clickhouse.com/docs/zh/sql-reference/statements/misc#misc_operations-optimize) 语句发起一个计划外的合并。例如：

```sql
OPTIMIZE TABLE visits PARTITION 201902;
```



## ERRS:

1. ```
    481. DB::Exception: Received from localhost:9000. DB::ErrnoException. DB::ErrnoException: Cannot set modification time for file: /var/lib/clickhouse/store/436/4368e494-7397-49a1-8daf-894549bb70f7/detached/attaching_202105_1_1_0, errno: 1, strerrermitted. (PATH_ACCESS_DENIED)
   ```

   chown -R clickhouse  /var/lib/clickhouse



2. 



