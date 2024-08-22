## CK-PG数据同步方式



这个错误消息表明 PostgreSQL 数据库中的所有复制槽（replication slots）已被占用，导致无法为新的复制进程创建复制槽。复制槽用于保留 WAL 日志，直到所有使用该复制槽的消费者都已处理这些日志。

1. **检查当前使用的复制槽**: 你可以使用以下 SQL 查询来查看当前存在的所有复制槽及其状态：

   ```sql
   SELECT 
       slot_name, 
       plugin, 
       slot_type, 
       database, 
       active, 
       restart_lsn 
   FROM 
       pg_replication_slots;
   ```

   这将返回所有的复制槽，并显示它们的名称、类型、是否活跃、以及相应的 WAL 日志位置等信息。

2. **删除不再需要的复制槽**: 如果有不再使用的复制槽，可以通过以下命令删除它们，以释放资源：

   ```sql
   SELECT pg_drop_replication_slot('slot_name');
   ```

   将 `'slot_name'` 替换为要删除的复制槽的名称。删除不需要的复制槽可以释放占用的资源。

3. **增加 `max_replication_slots`**: 如果所有复制槽都在使用中，并且你需要更多的复制槽，可以通过增加 PostgreSQL 配置中的 `max_replication_slots` 参数来允许更多的复制槽。你可以通过修改 PostgreSQL 配置文件（通常是 `postgresql.conf`）中的该参数来实现，或者通过 SQL 命令：

   ```sql
   ALTER SYSTEM SET max_replication_slots = new_value;
   ```

   将 `new_value` 替换为你需要的复制槽数量。然后重新加载 PostgreSQL 配置使更改生效：

   ```
   SELECT pg_reload_conf();
   ```



```sql
-- 到对应的PG数据库下查询
select * from pg_catalog.pg_publication;  

-- 在PG数据库中执行，删除对应的副本槽
DROP PUBLICATION "AnxinCloud_ch_publication";
```



通过AirByte进行数据同步

启动：在ck-master服务器上

```sql
root@ck-master:/home/cloud/airbyte/airbyte-master# docker-compose up -d
# 如无法启动执行下面语句
root@ck-master:/home/cloud/airbyte/airbyte-master# ./run-ab-platform.sh 
```



建立PG源连接

```sql
CREATE USER airbyte PASSWORD 'xxx';

GRANT USAGE ON SCHEMA public TO airbyte

GRANT SELECT ON ALL TABLES IN SCHEMA public TO airbyte;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT SELECT ON TABLES TO airbyte;
ALTER USER airbyte REPLICATION;
```

| wal_level             | Type of coding used within the Postgres write-ahead log      | `logical`                                                    |
| --------------------- | ------------------------------------------------------------ | ------------------------------------------------------------ |
| max_wal_senders       | The maximum number of processes used for handling WAL changes | `min: 1`                                                     |
| max_replication_slots | The maximum number of replication slots that are allowed to stream WAL changes | `1` (if Airbyte is the only service reading subscribing to WAL changes. More than 1 if other services are also reading from the WAL) |

```sql
SELECT pg_create_logical_replication_slot('airbyte_slot', 'pgoutput');
-- 设置表的复制标识（replica identity）(默认是主键，不需要执行)
ALTER TABLE t_structure REPLICA IDENTITY DEFAULT;
CREATE PUBLICATION airbyte_publication FOR TABLE t_structure;
CREATE PUBLICATION airbyte_publication FOR TABLE <tbl1, tbl2, tbl3>;`

```



重启Airbyte提示网络不存在，解决办法：

```sh
docker network prune
docker network create airbyte_internal
docker network create airbyte_public
docker-compose down
docker-compose up -d
```



image无法下载“

```sh
sudo docker save -o source_postres airbyte/source-postgres:2.0.33
sudo scp source_postres.tar cloud@ck-master:/home/cloud/
```



 INFO i.a.i.s.r.AbstractDbSource(check):101 Exception while checking connection: io.airbyte.commons.exceptions.ConfigErrorException: User 'airbyte' does not have enough privileges for CDC replication. Please read the docs and add required privileges

```sh

```

