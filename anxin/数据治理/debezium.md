## debezium

 ![image-20220516090710092](imgs/debezium/image-20220516090710092.png)

https://debezium.io/documentation/reference/stable/connectors/postgresql.html

**Debezium PostgreSQL** 提供行级变更捕获能力。

第一次连接到PG服务器，连接器对所有数据结构创建连续快照。然后开始连续捕获行级插入、更新和删除提交。该连接器产生数据改变事件，并提交到Kafka主题上。对于每个表，默认行为是连接器将所有生成的事件流式传输到该表的**单独 Kafka 主题**。应用程序和服务使用来自该主题的数据更改事件记录。

PostgreSQL 连接器包含两个主要部分：

- 一个逻辑解码输出插件。您可能需要安装您选择使用的输出插件。在运行 PostgreSQL 服务器之前，您必须配置一个使用您选择的输出插件的复制槽。插件可以是以下之一：
  - [`decoderbufs`](https://github.com/debezium/postgres-decoderbufs)基于 Protobuf 并由 Debezium 社区维护。
  - [`wal2json`](https://github.com/eulerto/wal2json)基于 JSON 并由 wal2json 社区维护（已弃用，计划在 Debezium 2.0 中删除）。
  - `pgoutput`是 PostgreSQL 10+ 中的标准逻辑解码输出插件。它由 PostgreSQL 社区维护，并由 PostgreSQL 本身用于[逻辑复制](https://www.postgresql.org/docs/current/logical-replication-architecture.html)。此插件始终存在，因此无需安装其他库。Debezium 连接器将原始复制事件流直接解释为更改事件。
- 读取所选逻辑解码输出插件产生的更改的 Java 代码（实际的 Kafka Connect 连接器）。它使用 PostgreSQL 的[*流复制协议*](https://www.postgresql.org/docs/current/static/logicaldecoding-walsender.html)，通过 PostgreSQL [*JDBC 驱动程序*](https://github.com/pgjdbc/pgjdbc)



WAL日志段会在一定时间后清除，所以连接器没有对数据库所做的所有更改的完整历史记录。因此，当PostgreSQL连接器第一次连接到特定数据库时，它首先对每个数据库模式执行一致快照。



### 工作原理

连接器第一次启动时的默认initial行为，可以通过*snapshot.mode*参数进行配置；

+ always 连接器在启动时始终执行快照 (start from )
+ never  : 从LSN（日志序列号）或Latest开始
+ initial_only  连接器执行数据库快照并在流式传输任何更改事件记录之前停止

Ad hoc snapshots(即席快照)

但是，在某些情况下，连接器在初始快照期间获得的数据可能会变得陈旧、丢失或不完整。为了提供一种重新捕获表数据的机制，Debezium 包含一个执行临时快照的选项。数据库中的以下更改可能会导致执行临时快照：

- 修改连接器配置以捕获一组不同的表。
- Kafka 主题被删除，必须重建。
- 由于配置错误或其他问题而发生数据损坏。



默认写入kafka的主题是：

*serverName.schemaName.tableName* 即 服务器名.结构名.表名



元信息

除了[*数据库更改事件*](https://debezium.io/documentation/reference/stable/connectors/postgresql.html#postgresql-events)之外，PostgreSQL 连接器生成的每条记录都包含一些元数据。元数据包括事件在服务器上发生的位置、源分区的名称以及事件应该去的 Kafka 主题和分区的名称，例如：

```json
"sourcePartition": {
     "server": "fulfillment"
 },
 "sourceOffset": {
     "lsn": "24023128",
     "txId": "555",
     "ts_ms": "1482918357011"
 },
 "kafkaPartition": null
```

### 数据更改实践

{ "schema": {    ...  }, "payload": {    ... }, "schema": {    ... }, "payload": {    ... }, }

第一个schema主键（唯一键）结构； 第二个schema是事件值的一部分。



创建一条数据（示例customer表包含id/firstName/lastName/email）:

```json
{
    "schema": { 
        "type": "struct",
        "fields": [
            {
                "type": "struct",
                "fields": [
                    {
                        "type": "int32",
                        "optional": false,
                        "field": "id"
                    },
                    {
                        "type": "string",
                        "optional": false,
                        "field": "first_name"
                    },
                    {
                        "type": "string",
                        "optional": false,
                        "field": "last_name"
                    },
                    {
                        "type": "string",
                        "optional": false,
                        "field": "email"
                    }
                ],
                "optional": true,
                "name": "PostgreSQL_server.inventory.customers.Value", 
                "field": "before"
            },
            {
                "type": "struct",
                "fields": [
                    {
                        "type": "int32",
                        "optional": false,
                        "field": "id"
                    },
                    {
                        "type": "string",
                        "optional": false,
                        "field": "first_name"
                    },
                    {
                        "type": "string",
                        "optional": false,
                        "field": "last_name"
                    },
                    {
                        "type": "string",
                        "optional": false,
                        "field": "email"
                    }
                ],
                "optional": true,
                "name": "PostgreSQL_server.inventory.customers.Value",
                "field": "after"
            },
            {
                "type": "struct",
                "fields": [
                    {
                        "type": "string",
                        "optional": false,
                        "field": "version"
                    },
                    {
                        "type": "string",
                        "optional": false,
                        "field": "connector"
                    },
                    {
                        "type": "string",
                        "optional": false,
                        "field": "name"
                    },
                    {
                        "type": "int64",
                        "optional": false,
                        "field": "ts_ms"
                    },
                    {
                        "type": "boolean",
                        "optional": true,
                        "default": false,
                        "field": "snapshot"
                    },
                    {
                        "type": "string",
                        "optional": false,
                        "field": "db"
                    },
                    {
                        "type": "string",
                        "optional": false,
                        "field": "schema"
                    },
                    {
                        "type": "string",
                        "optional": false,
                        "field": "table"
                    },
                    {
                        "type": "int64",
                        "optional": true,
                        "field": "txId"
                    },
                    {
                        "type": "int64",
                        "optional": true,
                        "field": "lsn"
                    },
                    {
                        "type": "int64",
                        "optional": true,
                        "field": "xmin"
                    }
                ],
                "optional": false,
                "name": "io.debezium.connector.postgresql.Source", 
                "field": "source"
            },
            {
                "type": "string",
                "optional": false,
                "field": "op"
            },
            {
                "type": "int64",
                "optional": true,
                "field": "ts_ms"
            }
        ],
        "optional": false,
        "name": "PostgreSQL_server.inventory.customers.Envelope" 
    },
    "payload": { 
        "before": null, 
        "after": { 
            "id": 1,
            "first_name": "Anne",
            "last_name": "Kretchmar",
            "email": "annek@noanswer.org"
        },
        "source": { 
            "version": "1.9.2.Final",
            "connector": "postgresql",
            "name": "PostgreSQL_server",
            "ts_ms": 1559033904863,
            "snapshot": true,
            "db": "postgres",
            "sequence": "[\"24023119\",\"24023128\"]"
            "schema": "public",
            "table": "customers",
            "txId": 555,
            "lsn": 24023128,
            "xmin": null
        },
        "op": "c", 
        "ts_ms": 1559033904863 
    }
}
```

schema数据的属性：

`PostgreSQL_server.inventory.customers.Value`是有效负载`before`和`after`字段的架构。此架构特定于`customers`表。

连接器source字段的架构：

`io.debezium.connector.postgresql.Source`是有效负载`source`字段的架构。此模式特定于 PostgreSQL 连接器。连接器将它用于它生成的所有事件

整体负载架构：

`PostgreSQL_server.inventory.customers.Envelope`是有效负载整体结构的架构，其中`PostgreSQL_server`是连接器名称，`inventory`是数据库，`customers`是表

事件类型：

+ c 创建 
+ d 删除
+ u 更新