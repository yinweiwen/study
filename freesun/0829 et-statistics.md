## 0829 et-statistics

+ 大监测设备统计接口扩展南昌县路灯和消防设备数据

+ 报表统计增加对安心云2.0报表数量的统计



配置增加：

大监测数据库配置：

```ini
djc.db.url=jdbc:postgresql://10.8.30.156:5432/jiancedaping
djc.db.user=${db.user}
djc.db.pwd=${db.pwd}
```



数据库脚本：

将南昌县现有设备数量录入数据库中：

```sql
create table bg_custom_data
(
   id serial not null
      constraint bg_custom_data_pk
         primary key,
   structure integer,
   devices jsonb,
   reports jsonb
);

comment on table bg_custom_data is '自定义数据统计量';

INSERT INTO public.bg_custom_data (structure, devices, reports) VALUES (2032, '{"路灯": 17315, "消防设备": 20717}', null);
```

> 将路灯和消防数据，绑定在安心云结构物 “**智慧工地-南昌县特勤消防站项目**”上。

