# 从安心云服务器导出ES天气数据

目录结构：

conf.d/

​	convert_csv.conf

data/

​	xxx.csv

logstash.yml





logstash.yml

```yml
path.config: /usr/share/logstash/conf.d/*.conf
path.logs: /var/log/logstash
```

convert_csv.conf
```yml
input {
 elasticsearch {
    hosts => "10.8.25.250:19201"
    index => "anxinyun_weather"
    query => '{  
    "query": {
        "bool": {
		  "must": [
			{"term": {
			  "cityName.keyword": {
				"value": "吉安"
			  }
			}},
			{
			  "term": {
				"dataType.keyword": {
				  "value": "hour"
				}
			  }
			},
			{
			  "range": {
			    "time": {
			      "gte": "2021-02-03T00:00:00.000+0800",
			      "lte": "2021-05-16T00:00:00.000+0800"
			    }
			  }
			}
		  ]
		}
      }
    }'
  }
}
 
output {
  csv {
    # This is the fields that you would like to output in CSV format.
    # The field needs to be one of the fields shown in the output when you run your
    # Elasticsearch query
 
    fields => ["cityName", "precip", "time"]
  
    # This is where we store output. We can use several files to store our output
    # by using a timestamp to determine the filename where to store output.    
    path => "/tmp/csv-export.csv"
  }
}
```


```shell
docker run --rm -ti -v /home/anxin/es-dump/logstash/data:/tmp -v /home/anxin/es-dump/logstash/logstash.yml:/usr/share/logstash/config/logstash.yml -v /home/anxin/es-dump/logstash/conf.d/:/usr/share/logstash/conf.d/ logstash:6.8.2
```



.

# ESDUMP

```shell
docker run --rm -ti -v /data:/tmp elasticdump/elasticsearch-dump \
  --input=http://10.8.25.250:19201/anxinyun_weather \
  --output=/tmp/res.csv \
  --limit = 1000 \
  --type=data \ 
  --searchBody='{"query":{"bool":{"must":[{"term":{"cityName.keyword":{"value":"吉安"}}},{"term":{"dataType.keyword":{"value":"hour"}}},{"range":{"time":{"gte":"2021-02-03T00:00:00.000+0800","lte":"2021-05-16T00:00:00.000+0800"}}}]}}}'
```



