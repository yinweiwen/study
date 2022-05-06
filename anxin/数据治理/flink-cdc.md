乱看

## 基于Flink-CDC打通业务数据实时入库

参考：https://www.51cto.com/article/665241.html

支持行级删除，有两种模式

+ Copy on Write(COW)模式 （保障读取性能）
+ Merge on Read(MOR)模式 (保障写入性能)  <-- iceberg



```sql
INSERT INTO IcebergTable /*+OPTIONS('equality-field-columns'='id')*/ SELECT * FROM KafkaTable; 
```



### 数据入湖任务运维 ★★

**a)压缩小文件**

Flink从Kafka消费的数据以checkpoint方式提交到Iceberg表，数据文件使用的是parquet格式，这种格式无法追加，而流式数据又不能等候太长时间，所以会不断commit提交数据产生小文件。目前Iceberg提供了一个批任务action来压缩小文件，需要定期周期性调用进行小文件的压缩功能。示例代码如下：

复制

```
Table table = ...  
Actions.forTable(table) 
.rewriteDataFiles() 
    .targetSizeInBytes(100 * 1024 * 1024) // 100 MB 
    .execute(); 
1.2.3.4.5.
```

**b)快照过期处理**

iceberg本身的架构设计决定了，对于实时入湖场景，会产生大量的snapshot文件，快照过期策略是通过额外的定时任务周期执行，过期snapshot文件和过期数据文件均会被删除。如果实际使用场景不需要time travel功能，则可以保留较少的snapshot文件。

复制

```
Table table = ...  
Actions.forTable(table) 
    .expireSnapshots() 
.expireOlderThan(System.currentTimeMillis()) 
.retainLast(5) 
    .execute(); 
1.2.3.4.5.6.
```

**c)清理orphan文件**

orphan文件的产生是由于正常或者异常的数据写入但是未提交导致的，长时间积累会产生大量脱离元数据的孤立数据文件，所以也需要类似JVM的垃圾回收一样，周期性清理这些文件。该功能不需要频繁运行，设置为3-5天运行一次即可。

复制

```
Table table = ... 
Actions.forTable(table) 
    .removeOrphanFiles() 
    .execute(); 
1.2.3.4.
```

**d)删除元数据文件**

每次提交snapshot均会自动产生一个新的metadata文件，实时数据入库会频繁的产生大量metadata文件，需要通过如下配置达到自动删除metadata文件的效果。

| Property                                   | Description                                                  |
| :----------------------------------------- | :----------------------------------------------------------- |
| write.metadata.delete-after-commit.enabled | Whether to delete old metadata files after each table commit |
| write.metadata.previous-versions-max       | The number of old metadata files to keep                     |





## Flink CDC 系列——Flink CDC 如何简化实时数据入湖入库

https://developpaper.com/flink-cdc-series-how-does-flink-cdc-simplify-real-time-data-warehousing-into-the-lake/



期望的cdc入湖黑匣子提供的功能

+ 全增量自动切换

+ 元数据自动发现

+ 表结构变更自动同步

+ 整库同步

  ​	可以减少源数据读取binlog的压力

  + 整库同步语句 CDAS

    ```sql
    CREATE DATABASE IF NOT　EXISTS hudi.ods AS DATABASE
    mysql.tpc_ds INCLUDING ALL TABLES;
    ```

    

  + 表级同步 CTAS

    ```sql
    CREATE TABLE IF NOT EXISTS hudi.ods.users
    AS TABLE mysql.`user_db[0-9]+`.`user[0-9]+`;
    ```

    

## [Flink 和 Iceberg 如何解决数据入湖面临的挑战](https://segmentfault.com/a/1190000040220308)

https://segmentfault.com/a/1190000040220308

实时入库痛点：

1. 程序中断后无法保证精确一次
2. 数据结构变更、分区变更太痛苦
3. 实时性增加导致小文件增多，分析扫描作业变慢
4. 全量增量切换

- **数据同步任务中断**
  - 无法有效隔离写入对分析的影响；
  - 同步任务不保证 exactly-once 语义。
- **端到端数据变更**
  - DDL 导致全链路更新升级复杂；
  - 修改湖/仓中存量数据困难。
- **越来越慢的近实时报表**
  - 频繁写入产生大量小文件；
  - Metadata 系统压力大, 启动作业慢；
  - 大量小文件导致数据扫描慢。
- **无法近实时分析 CDC 数据**
  - 难以完成全量到增量同步的切换；
  - 涉及端到端的代码开发，门槛高；
  - 开源界缺乏高效的存储系统。



Apache IceBerg核心特征

- **通用化标准设计**
  - 完美解耦计算引擎
  - Schema 标准化
  - 开放的数据格式
  - 支持 Java 和 Python
- **完善的 Table 语义**
  - Schema 定义与变更
  - 灵活的 Partition 策略
  - ACID 语义
  - Snapshot 语义
- **丰富的数据管理**
  - 存储的流批统一
  - 可扩展的 META 设计支持
  - 批更新和 CDC
  - 支持文件加密
- **性价比**
  - 计算下推设计
  - 低成本的元数据管理
  - 向量化计算
  - 轻量级索引