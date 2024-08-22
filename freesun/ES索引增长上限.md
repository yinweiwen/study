## ES索引增长上限

https://www.elastic.co/docs/api/doc/elasticsearch-serverless/operation/operation-reindex



新建索引

```json
PUT /native_themes_new
{"settings":{"index":{"refresh_interval":"30s","number_of_shards":"3","number_of_replicas":0,"translog":{"sync_interval":"60s","durability":"async"}}},"mappings":{"_doc":{"dynamic":"false","_all":{"enabled":false},"properties":{"batchid":{"type":"keyword"},"collect_time":{"type":"date","format":"date_time || yyy-MM-dd HH:mm:ss"},"create_time":{"type":"date","format":"date_time || yyyy-MM-dd HH:mm:ss"},"factor":{"type":"integer"},"factor_proto_code":{"type":"keyword"},"iota_device":{"type":"keyword"},"sensor":{"type":"integer"},"sensor_name":{"type":"text","norms":false,"fields":{"keyword":{"type":"keyword","ignore_above":50}}},"structure":{"type":"long"}}}}}

```



```sh
# 取消现有reindex任务的方法
GET /_tasks?actions=*reindex
POST /_tasks/tl0HniarTK-AdBeR1ukjsg:114356493/_cancel

PUT /native_themes_new/_settings
{
  "index": {
    "refresh_interval": "-1"
  }
}

GET /native_themes_new/_settings

POST /_reindex?slices=1
{
  "source": {
    "index": "native_themes",
    "size": 10000
  },
  "dest": {
    "index": "native_themes_new"
  }
}

PUT /native_themes_new/_settings
{
  "index": {
    "refresh_interval": "30s"
  }
}

POST /_aliases
{
  "actions": [
    {
      "add": {
        "index": "native_themes_new",
        "alias": "native_themes2"
      }
    }
  ]
}


GET native_themes2/_search
{
  "sort": [
    {
      "collect_time": {
        "order": "desc"
      }
    }
  ]
}
```



以上修改时未指定themes索引里面data字段的dynamic为false，导致data中的字段均未索引。

```json

PUT /native_themes_new/_doc/_mapping?include_type_name=true
{
  "dynamic": "true"
}
 
```

