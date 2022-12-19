## 使用Logstash同步远程数据

![image-20221219110542490](imgs/logstash2server/image-20221219110542490.png)

| Src                     | Avaliable          | Des            | Avaliable          |
| ----------------------- | ------------------ | -------------- | ------------------ |
| Postgresql (AXY 2.0)    | :white_check_mark: | ClickHouse@    | :white_check_mark: |
| Postgresql (2.0LEGACY)  | :x:                | ElasticSearch@ | :x:                |
| SqlServer (2.0LEGACY)   | :x:                | Hdfs@          | :x:                |
| ElasticSearch (AXY 3.0) | :white_check_mark: |                |                    |
|                         |                    |                |                    |
|                         |                    |                |                    |

> 使用PROJECT_NAME 作为每个本地化项目唯一标识。

## 本地部署（内嵌JDK)

使用的`logstash`版本为8.5.1 

![image-20221219110117968](imgs/logstash2server/image-20221219110117968.png)

P.S. `start.bat` 启动脚本，加入windows启动任务。

```bat
REM START LOGSTASH ADD IT TO BOOTSTART

set DB_CONNECTION=jdbc:postgresql://10.8.30.32:5432/haiwen20200401
set DB_USER=postgres
set DB_PASSWORD=123
set PROJECT_NAME=nbjj

./bin/logstash.bat -f ./config/local-warning.conf
```



其中配置 local-warning.conf:

```yaml
input {
    stdin {

    }
    
    jdbc {
        jdbc_driver_library => './logstash-core/lib/jars/postgresql-42.3.5.jar'
        jdbc_driver_class => 'org.postgresql.Driver'
        jdbc_connection_string => '${DB_CONNECTION}'
        jdbc_user => '${DB_USER:postgres}'
        jdbc_password => '${DB_PASSWORD}'
        clean_run => false
        jdbc_validate_connection => 'true'
        schedule => '0 0 */4 * * *'
        statement_filepath => './script/get_all_sensor.sql'
        jdbc_paging_enabled => true
        jdbc_paging_mode => 'auto'
        jdbc_page_size => '100'
        tags => ['${PROJECT_NAME}','sensor']
    }
    
    jdbc {
        jdbc_driver_library => './logstash-core/lib/jars/postgresql-42.3.5.jar'
        jdbc_driver_class => 'org.postgresql.Driver'
        jdbc_connection_string => '${DB_CONNECTION}'
        jdbc_user => '${DB_USER:postgres}'
        jdbc_password => '${DB_PASSWORD}'
        jdbc_default_timezone => 'Asia/Shanghai'
        use_column_value => 'true'
        tracking_column => 'last_time'
        tracking_column_type => 'timestamp'
        record_last_run => 'true' 
        last_run_metadata_path => './last_run/last_alarm'
        clean_run => false
        jdbc_validate_connection => 'true'
        schedule => '0 */15 * * * *'
        statement_filepath => './script/get_alarm.sql'
        jdbc_paging_enabled => true
        jdbc_paging_mode => 'auto'
        jdbc_page_size => '1000'
        tags => ['${PROJECT_NAME}','alarm','alarm']
    }
    
    jdbc {
        jdbc_driver_library => './logstash-core/lib/jars/postgresql-42.3.5.jar'
        jdbc_driver_class => 'org.postgresql.Driver'
        jdbc_connection_string => '${DB_CONNECTION}'
        jdbc_user => '${DB_USER:postgres}'
        jdbc_password => '${DB_PASSWORD}'
        jdbc_default_timezone => 'Asia/Shanghai'
        use_column_value => 'true'
        tracking_column => 'last_time'
        tracking_column_type => 'timestamp'
        record_last_run => 'true' 
        last_run_metadata_path => './last_run/last_alarm_archive'
        clean_run => false
        jdbc_validate_connection => 'true'
        schedule => '0 */15 * * * *'
        statement_filepath => './script/get_alarm_archive.sql'
        jdbc_paging_enabled => true
        jdbc_paging_mode => 'auto'
        jdbc_page_size => '1000'
        tags => ['${PROJECT_NAME}','alarm','alarm_archive']
    }
    
    
    jdbc {
        jdbc_driver_library => './logstash-core/lib/jars/postgresql-42.3.5.jar'
        jdbc_driver_class => 'org.postgresql.Driver'
        jdbc_connection_string => '${DB_CONNECTION}'
        jdbc_user => '${DB_USER:postgres}'
        jdbc_password => '${DB_PASSWORD}'
        jdbc_default_timezone => 'Asia/Shanghai'
        use_column_value => 'true'
        tracking_column => 'time'
        tracking_column_type => 'timestamp'
        record_last_run => 'true' 
        last_run_metadata_path => './last_run/last_alarm_detail'
        clean_run => false
        jdbc_validate_connection => 'true'
        schedule => '0 */15 * * * *'
        statement_filepath => './script/get_alarm_detail.sql'
        jdbc_paging_enabled => true
        jdbc_paging_mode => 'auto'
        jdbc_page_size => '1000'
        tags => ['${PROJECT_NAME}','alarm','alarm_detail']
    }
}

output {
    http {
        url=>'http://pipeline.ngaiot.com/data'
        http_method=>'post'
        automatic_retries=>3
        retry_failed=>'false'
    }
}
```



## 服务端部署

进程服务 LocalStash

conf.ini

```ini
[db]
type = postgres
; 安心云数据库连接地址
url = postgres://postgres:postgres@10.8.30.166:5432/Anxinyun0726?sslmode=disable
; 安心云数据库连接地址
dsn = host=10.8.30.166 user=postgres password=postgres dbname=Anxinyun0726 port=5432 sslmode=disable TimeZone=Asia/Shanghai

[web]
; web服务监听地址
url = :4002

[ck]
; Clickhouse地址(9000端口，非REST端口)
url = 10.8.30.161:30900
;数据库名称
db = default
;用户名称
usr = default
;密码
pwd =
;每批次导入数量
batchsize = 1000
;导入并行度
parallelism = 1
```



projects.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<projects>
    <project>
        <name>nbjj</name>
        <description>宁波交检本地化项目</description>
        <handles>
            <handle>sensor</handle>
            <handle>alarm</handle>
        </handles>
    </project>
</projects>
```

