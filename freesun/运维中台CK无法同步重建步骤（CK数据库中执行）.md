运维中台CK无法同步重建步骤（CK数据库中执行）

```sql
drop database xxx
```



到210 PG数据库中，删除对应Replication

```sql
-- 到对应的PG数据库下查询
select * from pg_catalog.pg_publication;  

-- 在PG数据库中执行，删除对应的副本槽
DROP PUBLICATION "AnxinCloud_ch_publication";
```



然后执行重建脚本（CK数据库中执行）

```sql

-- 几个库的同步
SET allow_experimental_database_materialized_postgresql=1;
CREATE DATABASE IF NOT EXISTS iota
ENGINE = MaterializedPostgreSQL('10.8.40.210', 'iOTA_console', 'postgres', 'postgres')
SETTINGS materialized_postgresql_max_block_size = 65536,
        materialized_postgresql_schema = 'public';

SET allow_experimental_database_materialized_postgresql=1;
CREATE DATABASE IF NOT EXISTS hrm
ENGINE = MaterializedPostgreSQL('10.8.40.223', 'HRM', 'postgres', 'postgres')
SETTINGS materialized_postgresql_max_block_size = 65536,
        materialized_postgresql_schema = 'public';

SET allow_experimental_database_materialized_postgresql=1;
CREATE DATABASE IF NOT EXISTS anxinyun
ENGINE = MaterializedPostgreSQL('10.8.40.210', 'AnxinCloud', 'postgres', 'postgres')
SETTINGS materialized_postgresql_max_block_size = 65536,
--         materialized_postgresql_tables_list = 't_factor_proto_item,t_alarm_group,t_alarm_group_unit,t_structure,t_alarm_code,t_alarm_type,t_video_ipc,t_sensor,t_video_ipc_station,t_project,t_project_structure,t_project_structuregroup,t_structuregroup_structure,t_project_construction,t_structure_site,t_factor,t_structure_factor,t_device_sensor',
        materialized_postgresql_schema = 'public';

SET allow_experimental_database_materialized_postgresql=1;
CREATE DATABASE IF NOT EXISTS camundaworkflow
ENGINE = MaterializedPostgreSQL('10.8.40.210', 'camunda', 'postgres', 'postgres')
SETTINGS materialized_postgresql_max_block_size = 65536,
        materialized_postgresql_schema = 'public';

SET allow_experimental_database_materialized_postgresql=1;
CREATE DATABASE IF NOT EXISTS peppm
ENGINE = MaterializedPostgreSQL('10.8.40.210', 'pep-pm', 'postgres', 'postgres')
SETTINGS materialized_postgresql_max_block_size = 65536,
        materialized_postgresql_schema = 'public';

SET allow_experimental_database_materialized_postgresql=1;
CREATE DATABASE IF NOT EXISTS pepca
ENGINE = MaterializedPostgreSQL('10.8.40.210', 'pepca', 'postgres', 'postgres')
SETTINGS materialized_postgresql_max_block_size = 65536,
        materialized_postgresql_schema = 'public';


SET allow_experimental_database_materialized_postgresql=1;
CREATE DATABASE IF NOT EXISTS video_access
ENGINE = MaterializedPostgreSQL('10.8.40.223', 'video-access', 'postgres', 'postgres')
SETTINGS materialized_postgresql_max_block_size = 65536,
        materialized_postgresql_schema = 'public';
```



还不行的话，到CK-MASTER上重启CK服务

```
192.168.1.141 ck-master  cloud/Freesun_*******
192.168.1.100 ck-n1  diantong/123456
192.168.1.101 ck-n2
192.168.1.102 ck-n3
192.168.1.103 ck-n4
192.168.1.104 ck-n5
192.168.1.105 zk-n1
192.168.1.106 zk-n2
192.168.1.107 zk-n3

```

