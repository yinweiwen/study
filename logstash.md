GETTING START
https://www.elastic.co/guide/en/logstash/current/index.html
https://www.elastic.co/guide/en/logstash/current/first-event.html

./logstash -e 'input {
  udp {
    port => 9998
  }
}

output {
  elasticsearch {
    hosts => "10.8.30.35"
  }
}'


精确匹配日志查找：
使用lucene语法：

{"match_phrase": {"message" : {"query" : "Bulk copy error"}}}

// 在106上部署：

nohup ./logstash -e 'input {
  udp {
    port => 9998
  }
}

output {
  elasticsearch {
    hosts => "192.168.0.106:19200"
  }
}' 1>/dev/null 2>&1 &


#启动kibana (5605)
nohup ./kibana 1>/dev/null 2>&1 &

开放端口：

firewall-cmd --zone=public --permanent --add-port=9998/udp
firewall-cmd --zone=public --permanent --add-port=5605/tcp

最新版kibana6不支持jdk1.7 (Unsupported major.minor version 52.0 (unable to load class) )
支持情况详情：
https://www.elastic.co/support/matrix#matrix_jvm


https://www.elastic.co/downloads/past-releases past-releases 

kibana版本  4.1.0
logstash版本 2.4.1



*** study logstash

INPUT => FILTER => OUTPUTS

bin/logstash -e 'input { stdin { } } output { stdout {} }'

Filebeats>
Filebeat是一个日志文件托运工具
	#filebeat.yml
	filebeat.prospectors:
	-	type: log
		paths:
		-	/path/to/file/xx.log
	output.logstash:
		hosts:["localhost:5044"]
	
	sudo ./filebeat -e -c filebeat.yml -d "publish"
		
	#logstash/first-pipeline.conf
	input {
			beats{
			port => "5044"
		}
	}
	filter {
		grok {
			match => { "message" => "%{COMBINEDAPACHELOG}"}
		}
		geoip {
			source => "clientip"
		}
	}
	output {
		stdout { codec => rubydebug }
		elasticsearch {
			hosts => [ "localhost:9200" ]
		}
		elasticsearch {
			hosts => [ "node1:9200","node2:9200" ]
		}
		file {
			path => "/path/to/target/file"
		}
	}
	
	>> config file test
	 ./logstash -f first-pipeline.conf --config.test_and_exit
	>> start
	 ./logstash -f first-pipeline.conf --config.reload.automatic	// 配置文件改变后会自动重新载入
	 // filebeat 重新加载日志文件
	 sudo rm data/registry

>> HOW LOGSTASH WORK
	INPUT (https://www.elastic.co/guide/en/logstash/current/input-plugins.html)
		file/syslog/redis/beats:

	FILTER (https://www.elastic.co/guide/en/logstash/current/filter-plugins.html)
		grok: **
		mutate/drop/clone/geoip
	
	OUTPUTS (https://www.elastic.co/guide/en/logstash/current/output-plugins.html)
		elasticsearch/file/graphite/statsd
	
	CODECS (https://www.elastic.co/guide/en/logstash/current/codec-plugins.html)
		json/msgpack/plain/multiline
		
	Each input stage in the Logstash pipeline runs in its own thread(java SynchronousQueue)

>>Settings
	logstash.yml
	写法：支持继承模式(hierarchical)和平铺模式(flag), bash-style的环境变量设置
	node.name 节点描述
	path.data persistent data directory
	pipeline.workers 默认是宿主机的CPU核数
	pipeline.batch.size 多少个事件event作为一批进行filter和output（提高性能，但会增加内存消耗（可能还需要增加JVM堆栈通过设置 LS_HEAP_SIZE）） 默认125
	pipeline.batch.delay 未满足大小，延迟多久dispatch到worker （ms） 默认5
	pipeline.unsafe_shutdown  true-支持强制退出
	path.config  主管道配置文件路径
	config.string 主管道配置字符串
	modules  XXXXX
	queue.type 事件缓存的内部存储模型 memory / persisted
	path.queue  queue类型为persist时文件存储路径
	http.host/port REST终端地址
	log.level 日志级别

>> Running as a service 
	systemctl start logstash.service

>> Examples:
```yaml
input {
    stdin {

    }
    
    jdbc {
        jdbc_driver_library => './logstash-core/lib/jars/postgresql-42.3.5.jar'
        jdbc_driver_class => 'org.postgresql.Driver'
        jdbc_connection_string => 'jdbc:postgresql://10.8.30.156:5432/tsydb1'
        jdbc_user => 'postgres'
        jdbc_password => 'postgres'
        use_column_value => 'true'
        tracking_column => 'acquisition_datetime'
        tracking_column_type => 'timestamp'
        record_last_run => 'true' 
        clean_run => false
        jdbc_validate_connection => 'true'
        schedule => '*/5 * * * * *'
        statement => 'select sensor_id,temperature_value,humility_value,acquisition_datetime from t_themes_envi_temp_humi where agg_type is null and acquisition_datetime > :sql_last_value order by acquisition_datetime ASC'
        jdbc_paging_enabled => true
        jdbc_paging_mode => 'auto'
        jdbc_page_size => '10'
        tags => 't_themes_envi_temp_humi'
        last_run_metadata_path => 'last_run'
    }
}

output {
    stdout {

    }
    
    http {
        url=>'http://10.8.30.183:4002/data'
        http_method=>'post'
        automatic_retries=>3
        retry_failed=>'false'
    }
    tcp {
        mode => 'client'
        host => '10.8.30.183'
        port => '8888'
        reconnect_interval => 10
    }
}
```



## LogStash Windows 自启动

https://www.elastic.co/guide/en/logstash/current/running-logstash-windows.html

