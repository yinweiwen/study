## RTU告警

### 1. 功能说明

接收硬件设备RTU的告警，直接在安心云平台生成如下告警码类型的告警。

这几类告警均为**设备类告警**，不进行升级、合并，并在告警生成的时候默认为**待审核**，在安心云**[告警服务](https://jenkins.ngaiot.com/job/alarm-flink/)**增加审核处理机制。

| 告警名称                     | 告警码   | 告警类型 |
| ---------------------------- | -------- | -------- |
| 监测数据超阈值               | 70200001 | 7020     |
| 疑似滑坡                     | 70210001 | 7021     |
| 监测数据超阈值且现场疑似滑坡 | 70220001 | 7022     |



SQL脚本:

```sql
INSERT INTO "public"."t_alarm_type"("code", "name", "description", "category", "enabled", "upgrade_strategy","alarm_group","alarm_group_unit", "advice_problem") VALUES
('7020', '监测数据超阈值', '边坡相关设备告警', 1, 't', '{"independent": true,"need_confirm":true}',3,37,'非软件处理');
INSERT INTO "public"."t_alarm_code"("code", "name", "type_code", "level", "upgrade_strategy", "enable", "advice_problem", "alarm_group", "alarm_group_unit") VALUES
( '70200001', '监测数据超阈值', '7020', 1, NULL, 't', '非软件处理', 3, 37);



INSERT INTO "public"."t_alarm_type"("code", "name", "description", "category", "enabled", "upgrade_strategy","alarm_group","alarm_group_unit", "advice_problem") VALUES
('7021', '疑似滑坡', '边坡相关设备告警', 1, 't', '{"independent": true,"need_confirm":true}',3,37,'非软件处理');
INSERT INTO "public"."t_alarm_code"("code", "name", "type_code", "level", "upgrade_strategy", "enable", "advice_problem", "alarm_group", "alarm_group_unit") VALUES
( '70210001', '疑似滑坡', '7021', 1, NULL, 't', '非软件处理', 3, 37);




INSERT INTO "public"."t_alarm_type"("code", "name", "description", "category", "enabled", "upgrade_strategy","alarm_group","alarm_group_unit", "advice_problem") VALUES
('7022', '监测数据超阈值且现场疑似滑坡', '边坡相关设备告警', 1, 't', '{"independent": true,"need_confirm":true}',3,37,'非软件处理');
INSERT INTO "public"."t_alarm_code"("code", "name", "type_code", "level", "upgrade_strategy", "enable", "advice_problem", "alarm_group", "alarm_group_unit") VALUES
( '70220001', '监测数据超阈值且现场疑似滑坡', '7022', 1, NULL, 't', '非软件处理', 3, 37);
```



### 2. 模拟自测

测试环境

```sh
kafka.brokers=10.8.30.84:30992
kafka.group.id=flink_alarm_test_new1

es.nodes=10.8.30.155:9200
es.type=_doc

redis.host=10.8.30.150
redis.port=30069
redis.timeout=30

db.url=jdbc:postgresql://10.8.30.166:5432/Anxinyun0726
db.user=FashionAdmin
db.pwd=123456
```



模拟Kafka告警消息

> ```
> -- 826 "江西地灾rtu" "c4c3f173-dc37-4f04-920d-e87f4a4decc2" 位移 24
> -- 826 "江西地灾rtu" "356070fa-1d47-4aa5-aa0d-56fb4d8d40d2" 测斜 63
> -- 826 "江西地灾rtu" "ffb9b4c7-2e39-4592-ab23-3b48ca1cbb7f" 温湿度 160
> ```



```json
{
    "messageMode": "AlarmGeneration",
    "structureId": 826,
    "structureName": "江西地灾rtu",
    "sourceId": "c4c3f173-dc37-4f04-920d-e87f4a4decc2",
    "sourceName": "位移",
    "alarmTypeCode": "7020",
    "alarmCode": "70200001",
    "content": "RTU数据超阈值",
    "time": "2024-09-23T10:42:49.000+0800",
    "sourceTypeId": 1,
    "sponsor": "et.recv",
    "extras": null,
    "subDevices": null
}
{
    "messageMode": "AlarmGeneration",
    "structureId": 826,
    "structureName": "江西地灾rtu",
    "sourceId": "c4c3f173-dc37-4f04-920d-e87f4a4decc2",
    "sourceName": "位移",
    "alarmTypeCode": "7020",
    "alarmCode": "70200001",
    "content": "RTU数据超阈值",
    "time": "2024-09-23T11:12:00.000+0800",
    "sourceTypeId": 1,
    "sponsor": "et.recv",
    "extras": null,
    "subDevices": null
}
```

查询ES **自测通过**（生成两条独立的告警，并且status=1待审核状态）

```json
{
    "_index" : "anxinyun_alarms",
    "_type" : "_doc",
    "_id" : "98fe94cf-25d3-369f-9b8f-348ab39cf299",
    "_score" : 1.2039728,
    "_source" : {
        "subDevices" : [ ],
        "start_time" : "2024-09-23T02:42:49.000Z",
        "alarm_type_code" : "7020",
        "state" : 0,
        "alarm_count" : 1,
        "alarm_type_id" : 55,
        "end_time" : "2024-09-23T02:42:49.000Z",
        "structure_id" : 826,
        "notice" : "",
        "source_type_id" : 1,
        "alarm_group_unit" : 37,
        "alarm_content" : "监测数据超阈值",
        "source_name" : "位移",
        "status" : 1,
        "alarm_group" : 3,
        "source_id" : "c4c3f173-dc37-4f04-920d-e87f4a4decc2",
        "initial_level" : 1,
        "current_level" : 1,
        "detail" : "监测数据超阈值:RTU数据超阈值",
        "alarm_code" : "70200001",
        "alarm_advice_problem" : "非软件处理"
    }
}
```

其他几个类型Kafka消息：

```json
{
    "messageMode": "AlarmGeneration",
    "structureId": 826,
    "structureName": "江西地灾rtu",
    "sourceId": "356070fa-1d47-4aa5-aa0d-56fb4d8d40d2",
    "sourceName": "测斜",
    "alarmTypeCode": "7021",
    "alarmCode": "70210001",
    "content": "测斜数据超范围可能存在滑坡",
    "time": "2024-09-23T11:10:00.000+0800",
    "sourceTypeId": 1,
    "sponsor": "et.recv",
    "extras": null,
    "subDevices": null
}
{
    "messageMode": "AlarmGeneration",
    "structureId": 826,
    "structureName": "江西地灾rtu",
    "sourceId": "ffb9b4c7-2e39-4592-ab23-3b48ca1cbb7f",
    "sourceName": "温湿度",
    "alarmTypeCode": "7022",
    "alarmCode": "70220001",
    "content": "WSD疑似问题",
    "time": "2024-09-23T11:10:00.000+0800",
    "sourceTypeId": 1,
    "sponsor": "et.recv",
    "extras": null,
    "subDevices": null
}

```



发送手动审核命令 (告警消息中增加 alarmId， 审核、恢复指定的某条告警)

```json
{
    "messageMode": "AlarmConfirmFail",
    "alarmId":"7aa71e6d-a529-3b9b-b5dc-4e9723633616",
    "sourceId": "ffb9b4c7-2e39-4592-ab23-3b48ca1cbb7f",
    "alarmTypeCode": "7022",
    "sponsor": 1371,
    "content": "现场维护导致",
    "time": "2024-09-23T11:22:32.397+0800"
}
```





 ![img](imgs/RTU告警/0_0)