#### PostgreSQL 分表实践

背景

>  宁波交检沉降数据表之前建立的主键类型为integer，目前数据超过21亿，新数据无法写入。



> PostgreSQL在PostgreSQL 10引用分表功能，PostgreSQL10支持的分表策略是
>
> - 区域分区(Range Partition)：根据某一列或者多列的值划分范围，
> - 列表分区(List Partition)：表按照一个给定的列表来分区
>
> 虽然宁波交检的PG数据库版本为9.5，但是通过pg官网还是找到了9.5 Partition Table相关的介绍，Let's have try!!



分区指的是将逻辑上的一个大表分割成更小的物理单元。分区可以提供多种好处：

+ 在某些情况下，特别是当表的大部分访问量集中在单个分区或少数几个分区时，可以显著提高查询性能。分区可以替代索引的前导列，减小索引大小，并增加索引的部分能够适应内存的可能性。

+ 当查询或更新访问单个分区的大部分数据时，可以通过利用该分区的顺序扫描来提高性能，而不是使用索引和在整个表中分散的随机访问读取。

+ 通过添加或删除分区，可以实现批量加载和删除数据，如果这个需求已经在分区设计中考虑到。ALTER TABLE NO INHERIT 和 DROP TABLE 命令的速度都比批量操作快得多。这些命令也完全避免了由批量删除引起的 VACUUM 开销。

+ 很少使用的数据可以迁移到更便宜和更慢的存储介质上。



创建步骤：

要设置一个分区表，按照以下步骤进行操作：

1. 创建一个“主”表，所有分区都将从这个表继承。

   这个表不包含任何数据。不要在这个表上定义任何检查约束，除非你希望它们应用于所有分区。在这个表上定义任何索引或唯一约束也没有意义。

2. 创建几个“子”表，每个子表都从主表继承。通常情况下，这些表不会添加任何列到从主表继承的列集中。

   我们将这些子表称为分区，尽管它们在每个方面都是普通的 PostgreSQL 表（或者可能是外部表）。

3. 在分区表上添加表约束，以定义每个分区中允许的键值。

   典型的例子包括：

```
CHECK ( x = 1 )
CHECK ( county IN ( 'Oxfordshire', 'Buckinghamshire', 'Warwickshire' ))
CHECK ( outletID >= 100 AND outletID < 200 )
```

​	确保这些约束保证了不同分区中允许的键值之间没有重叠。一个常见的错误是设置类似于范围约束的情况，比如：

```
CHECK ( outletID BETWEEN 100 AND 200 )
CHECK ( outletID BETWEEN 200 AND 300 )
```

​	这是错误的，因为不清楚键值 200 属于哪个分区。

​	请注意，语法上没有区别，范围和列表分区仅仅是描述性的术语。

​	4. 对于每个分区，在键列上创建索引，以及任何其他你可能想要的索引。（键索引不是严格必需的，但在大多数情况下是有帮助的。如果你打算键值是唯一的，那么你应该为每个分区总是创建一个唯一约束或主键约束。）

5. 可选地，定义一个触发器或规则，将插入到主表的数据重定向到相应的分区。

6. 确保 `postgresql.conf` 中的 constraint_exclusion 配置参数没有被禁用。如果被禁用了，查询将无法按预期进行优化。



### 实践

```sql
alter table t_themes_deformation_settlement rename to t_themes_deformation_settlement_old;
-- 创建主表（监测数据表）
create table t_themes_deformation_settlement
(
	id serial8 primary key,
	sensor_id integer,
	safety_factor_type_id integer default 11 not null,
	physical_quantity_value numeric(18,6) default NULL::numeric,
	settlement_value numeric(18,6) default NULL::numeric,
	settlement_score smallint,
	acquisition_datetime timestamp(6) default NULL::timestamp without time zone,
	orderby_column integer,
	description varchar(300) default NULL::character varying,
	temperature_value numeric(18,6) default NULL::numeric,
	reserved_field_2 varchar(30) default NULL::character varying,
	reserved_field_3 varchar(30) default NULL::character varying,
	reserved_field_4 varchar(30) default NULL::character varying,
	reserved_field_5 varchar(30) default NULL::character varying,
	press_original numeric(18,6) default NULL::numeric,
	settlement_value_original numeric(18,6) default NULL::numeric,
	agg_type integer,
	agg_way integer default 2
);

create index "ix_t_themes_deformation_settlement_datetime_sensorid"
	on t_themes_deformation_settlement (acquisition_datetime, sensor_id);

create index "ix_t_themes_deformation_settlement_datetime_sensorid_agg"
	on t_themes_deformation_settlement (sensor_id asc, acquisition_datetime desc, agg_type asc, agg_way asc);

-- 创建分表（设置Check时间范围约束）
create table t_themes_deformation_settlement_x_old ( CHECK ( acquisition_datetime < DATE '2024-03-01')) inherits (t_themes_deformation_settlement);
create table t_themes_deformation_settlement_2024 ( CHECK ( acquisition_datetime >= DATE '2024-03-01' AND acquisition_datetime < DATE '2025-01-01' )) inherits (t_themes_deformation_settlement);
create table t_themes_deformation_settlement_2025 ( CHECK ( acquisition_datetime >= DATE '2025-01-01' AND acquisition_datetime < DATE '2026-01-01' )) inherits (t_themes_deformation_settlement);

-- 创建RULE规则使得插入原表自动插入到指定分表
CREATE OR REPLACE FUNCTION t_themes_deformation_settlement_insert_trigger()
RETURNS TRIGGER AS $$
BEGIN
    IF ( NEW.acquisition_datetime < DATE '2024-03-01' ) THEN
        INSERT INTO t_themes_deformation_settlement_x_old VALUES (NEW.*);
    ELSIF ( NEW.acquisition_datetime >= DATE '2024-03-01' AND
            NEW.acquisition_datetime < DATE '2025-01-01' ) THEN
        INSERT INTO t_themes_deformation_settlement_2024 VALUES (NEW.*);
    ELSIF ( NEW.acquisition_datetime >= DATE '2025-01-01' AND
            NEW.acquisition_datetime < DATE '2026-01-01' ) THEN
        INSERT INTO t_themes_deformation_settlement_2025 VALUES (NEW.*);
    ELSE
        RAISE EXCEPTION 'Date out of range.';
    END IF;
    RETURN NULL;
END;
$$
LANGUAGE plpgsql;

-- 绑定到主表插入的触发器中
CREATE TRIGGER insert_t_themes_deformation_settlement_trigger
    BEFORE INSERT ON t_themes_deformation_settlement
    FOR EACH ROW EXECUTE PROCEDURE t_themes_deformation_settlement_insert_trigger();

-- 模拟数据
-- INSERT INTO public.x (sensor_id, safety_factor_type_id, physical_quantity_value, settlement_value, settlement_score, acquisition_datetime, orderby_column, description, temperature_value, reserved_field_2, reserved_field_3, reserved_field_4, reserved_field_5, press_original, settlement_value_original, agg_type, agg_way) VALUES
-- (653, 118, null, 0.000000, null, '2024-02-07 08:47:02.000000', null, null, null, null, null, null, null, null, null, null, 2);
-- INSERT INTO public.x (sensor_id, safety_factor_type_id, physical_quantity_value, settlement_value, settlement_score, acquisition_datetime, orderby_column, description, temperature_value, reserved_field_2, reserved_field_3, reserved_field_4, reserved_field_5, press_original, settlement_value_original, agg_type, agg_way) VALUES
-- (653, 118, null, 0.000000, null, '2024-03-07 08:47:02.000000', null, null, null, null, null, null, null, null, null, null, 2);

SET constraint_exclusion =on; -- 开启这个后，只查询指定时间区间内的表
EXPLAIN  select * from t_themes_deformation_settlement where acquisition_datetime>'2024-03-03';

-- 查看子表中是否有触发器
SELECT tgname AS trigger_name
FROM pg_trigger
WHERE tgrelid = 'public.t_themes_deformation_settlement_x_old'::regclass;

-- 21Y条数据,耗时： Minutes （未完成，换成下面说明的循环执行方式）
ALTER TABLE t_themes_deformation_settlement DISABLE TRIGGER ALL;
insert into t_themes_deformation_settlement_x_old select * from t_themes_deformation_settlement;
ALTER TABLE t_themes_deformation_settlement ENABLE TRIGGER ALL;

select count(*) from t_themes_deformation_settlement_2025;

-- 数据拷贝
-- 2146055989
CREATE OR REPLACE FUNCTION copy_data_in_batches()
RETURNS VOID AS $$
DECLARE
    batch_size INT := 100000; -- 每个批次复制的行数
    max_id integer :=0;
BEGIN
    SELECT MAX(id) INTO max_id FROM t_themes_deformation_settlement_old;

    WHILE max_id >0 LOOP
        RAISE NOTICE 'Copying data from ID % to %', max_id, max_id - batch_size + 1;

        INSERT INTO t_themes_deformation_settlement_x_old (sensor_id, safety_factor_type_id, physical_quantity_value, settlement_value, settlement_score, acquisition_datetime, orderby_column, description, temperature_value, reserved_field_2, reserved_field_3, reserved_field_4, reserved_field_5, press_original, settlement_value_original, agg_type, agg_way)
        SELECT sensor_id, safety_factor_type_id, physical_quantity_value, settlement_value, settlement_score, acquisition_datetime, orderby_column, description, temperature_value, reserved_field_2, reserved_field_3, reserved_field_4, reserved_field_5, press_original, settlement_value_original, agg_type, agg_way
        FROM t_themes_deformation_settlement_old
        WHERE id BETWEEN max_id - batch_size + 1 AND max_id
        AND agg_type is not null
        ORDER BY id DESC;

        max_id:=max_id-batch_size;
        end loop;
END;
$$ LANGUAGE plpgsql;

select copy_data_in_batches();
```



## 重要补充

PG9.5中继承的表，没有自动继承主表的索引。

手动在各个分表中创建索引：

```sql
create index ix_t_themes_deformation_surface_displacement_2025_sensor_time
   on public.t_themes_deformation_surface_displacement_2025 (acquisition_datetime, sensor_id);
```

同时，发现主键也么有明显标识出来。在`DataGrip`中修改表结构，将Id列标注为`Promary Key`.