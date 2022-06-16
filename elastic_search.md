# ElasticSearch

启动：
	bin/elasticsearch -d
关闭：
	ps -ef | grep elastic
	kill -9
	

## 创建索引

```
PUT raw_data
{
 "mappings":{
	...
 }
}
```

### 安装head插件

https://github.com/mobz/elasticsearch-head
	git clone git://github.com/mobz/elasticsearch-head.git
	cd elasticsearch-head
	npm install
	npm run start
	open http://localhost:9100/


### 安装x-pack监视性能：
	bin/elasticsearch-plugin install x-pack
 没学会。。。

```
 ES性能监视
	监视内存
	ps -ef | grep elastic
	top -p xxx -d 1
	监视存储
	head http://10.8.30.35:9100/
		savoir_raws
		size: 56.9Gi (117Gi)
		docs: 812,228 (1,624,456)
		
		GET _cat/indices/savoir_raws
				health status index uuid pri rep docs.count docs.deleted   store.size  pri.store.size
				green open savoir_raws X3OCrzSwSwKEZrQhkn9Rrw 5 1 819361 0 118gb 57.2gb
```

+
LUCENSE:
document -> 索引和搜索的主要数据载体，对应写入到ES中的一个doc
field -> 字段
term -> 词项，搜索时单位
token -> 词项(term)在字段(field)中的一次出现,包括词项的文本、开始和结束的位移、类型等信息

index 类似db
shard is an instance of Lucene. It is a fully functional search engine in its own right. primary shard&& replica shard ,提供容错和读取吞吐量
segment 每个shard包含多个segment，在shard中的搜索会按顺序手搜索该shard上所有的segment。 segment(逆向索引)不可变

分片处理机制：
https://blog.csdn.net/mgxcool/article/details/49250341

/_cat/allocation
	shards disk.indices disk.used disk.avail disk.total disk.percent host          ip            node
   101      655.7mb   172.8gb     50.5gb    223.4gb           77 192.168.0.106 192.168.0.106 node-0
   101      655.6mb    48.3gb    853.3gb    901.7gb            5 192.168.0.109 192.168.0.109 node-1
   ?v 显示详细信息

/_cat/shards
	所有分片大小位置
/_cat/shards/{index}
	
/_cat/master 主节点信息
/_cat/nodes 节点信息
/_cat/indices 索引信息
/_cat/indices/{index}
/_cat/segments 
/_cat/segments/{index}
/_cat/count
/_cat/count/{index}
/_cat/recovery
/_cat/recovery/{index}
/_cat/health
/_cat/pending_tasks
/_cat/aliases
/_cat/aliases/{alias}
/_cat/thread_pool
/_cat/plugins
/_cat/fielddata
/_cat/fielddata/{fields}
/_cat/nodeattrs
/_cat/repositories
/_cat/snapshots/{repository}


## ES Script

> Stored Scripts

获取当前所有脚本
```shell
GET _cluster/state/metadata?pretty&filter_path=**.stored_scripts
```
获取脚本信息
```shell
GET _scripts/savoir-alarm-update
```
新建脚本
```shell
curl -X POST "localhost:9200/_scripts/calculate-score" -H 'Content-Type: application/json' -d'
{
  "script": {
    "lang": "painless",
    "source": "Math.log(_score * 2) + params.my_modifier"
  }
}
'
```

> Script Fields
返回经过脚本加工的数据
```json
GET savoir_alarms/_search
{
  "query": {
    "match_all": {}
  },
  "script_fields": {
    "test1": {
      "script": {
        "lang": "painless",
        "source":"doc['alarm_count'].value* params.factor",
        "params": {
          "factor":500
        }
      }
    }
  }
}
```

使用`doc['my_field'].value`和`params['_source']['my_field']`区别：
1. 前者将匹配项加载到内存(快)
2. 后者每次使用时解析，执行慢
3. 前者只支持简单数据类型(例如不能返回json对象)

[脚本支持的语言](https://www.elastic.co/guide/en/elasticsearch/reference/current/modules-scripting.html)
+ painless <built-in>
	+ inline
	+ stored
+ expression <built-in>
+ mustache <built-in>
+ java

> Prefer parameters
同样的脚本会编译后缓存，使用相同的脚本，并使用params可以提高脚本执行效率
；脚本编译受限script.max_compilations_rate，超出会抛出异常


## 数据更新 UPDATE
-- 部分更新
GET anxinyun_alarms/alarm/4506871f-5671-4c04-bf94-0338f8b3b69c/_update
{
  "doc":{
    "state":1
  }
}


## 包含指定字段
GET anxinyun_aggregation/_search
{
  "query": {
    "exists": {
      "field": "data.wqLevel"
    }
  }
}

## ARRAY | NESTED 下字段查询
GET savoir_alarms/_search
{
  "query": {
    "nested": {
      "path": "details",
      "query": {
        "bool": {
          "must": [
            {"exists": {"field": "details.alarm_pic"}}
          ]
        }
      }
    }
  }
}

## update_by_query
GET anxinyun_aggregation/_update_by_query?timeout=20m
{
  "script":{
    "source": "ctx._source['data']['physicalvalue']=ctx._source['data']['physicalvalue']*10"
  },,
  "query": {
    "exists": {
      "field": "data.wqLevel"
    }
  }
}

## aggs
### TOP HIT EX.
GET anxinyun_themes/_search
{
  "query": {
    "bool": {
      "must": [
        {
          "range": {
            "structure": {
              "gte": 10,
              "lte": 2000
            }
          }
        }
      ]
    }
  },
  "size": 0, 
  "aggs": {
    "struct": {
      "terms": {
        "field": "structure",
        "size": 10000
      },
      "aggs": {
        "LATEST": {
          "top_hits": {
            "size": 1,
            "sort": [
              {
                "collect_time": {
                  "order": "desc"
                }
              }  
            ],
            "_source": {
              "includes": ["structure","collect_time"]
            }
          }
        }
      }
    }
  }
}

## 输出指定项
GET iota-log-2021.05.10/_search?_source=log
{
  "query": {
    
    "bool": {
      "must": [
        {"match": {
          "log": "60774519"
        }}
      ]
    }
  }
}

## ES-DUMP

```
# 导出，服务器上执行
docker run --rm -ti -v /data:/tmp elasticdump/elasticsearch-dump \
  --input=http://10.8.25.211:9200/raw_data \
  --output=/tmp/my_index_mapping.json \
  --type=data \
  --searchBody='{"query": { "bool": { "must": [{"term": {"deviceId": {"value": "e155b882-d2df-4092-b57d-0a02fae8fca6"}}}, { "range": { "triggerTime": { "lt": "2020-10-24T00:00:00.000+08:00","gt":"2020-10-01T00:00:00.000+08:00" } } } ] } } }'
  

# 测试环境导入
docker run --rm -ti  -v /data:/tmp elasticdump/elasticsearch-dump --input=./data1.json --output=http://10.8.30.36:9200 --type=data
```



```shell
elasticdump: Import and export tools for elasticsearch
version: %%version%%

Usage: elasticdump --input SOURCE --output DESTINATION [OPTIONS]

--input
                    Source location (required)
--input-index
                    Source index and type
                    (default: all, example: index/type)
--output
                    Destination location (required)
--output-index
                    Destination index and type
                    (default: all, example: index/type)
--overwrite
                    Overwrite output file if it exists
                    (default: false)                    
--limit
                    How many objects to move in batch per operation
                    limit is approximate for file streams
                    (default: 100)
--size
                    How many objects to retrieve
                    (default: -1 -> no limit)
--concurrency
                    The maximum number of requests the can be made concurrently to a specified transport.
                    (default: 1)       
--concurrencyInterval
                    The length of time in milliseconds in which up to <intervalCap> requests can be made
                    before the interval request count resets. Must be finite.
                    (default: 5000)       
--intervalCap
                    The maximum number of transport requests that can be made within a given <concurrencyInterval>.
                    (default: 5)
--carryoverConcurrencyCount
                    If true, any incomplete requests from a <concurrencyInterval> will be carried over to
                    the next interval, effectively reducing the number of new requests that can be created
                    in that next interval.  If false, up to <intervalCap> requests can be created in the
                    next interval regardless of the number of incomplete requests from the previous interval.
                    (default: true)                                                                                       
--throttleInterval
                    Delay in milliseconds between getting data from an inputTransport and sending it to an
                    outputTransport.
                     (default: 1)
--debug
                    Display the elasticsearch commands being used
                    (default: false)
--quiet
                    Suppress all messages except for errors
                    (default: false)
--type
                    What are we exporting?
                    (default: data, options: [settings, analyzer, data, mapping, policy, alias, template, component_template, index_template])
--filterSystemTemplates
                    Whether to remove metrics-*-* and logs-*-* system templates 
                    (default: true])
--templateRegex
                    Regex used to filter templates before passing to the output transport 
                    (default: ((metrics|logs|\\..+)(-.+)?)
--delete
                    Delete documents one-by-one from the input as they are
                    moved.  Will not delete the source index
                    (default: false)
--searchBody
                    Preform a partial extract based on search results
                    when ES is the input, default values are
                      if ES > 5
                        `'{"query": { "match_all": {} }, "stored_fields": ["*"], "_source": true }'`
                      else
                        `'{"query": { "match_all": {} }, "fields": ["*"], "_source": true }'`
                    [As of 6.68.0] If the searchBody is preceded by a @ symbol, elasticdump will perform a file lookup
                    in the location specified. NB: File must contain valid JSON
--searchWithTemplate
                    Enable to use Search Template when using --searchBody
                    If using Search Template then searchBody has to consist of "id" field and "params" objects
                    If "size" field is defined within Search Template, it will be overridden by --size parameter
                    See https://www.elastic.co/guide/en/elasticsearch/reference/current/search-template.html for 
                    further information
                    (default: false)
--headers
                    Add custom headers to Elastisearch requests (helpful when
                    your Elasticsearch instance sits behind a proxy)
                    (default: '{"User-Agent": "elasticdump"}')
                    Type/direction based headers are supported .i.e. input-headers/output-headers 
                    (these will only be added based on the current flow type input/output)
--params
                    Add custom parameters to Elastisearch requests uri. Helpful when you for example
                    want to use elasticsearch preference
                    
                    --input-params is a specific params extension that can be used when fetching data with the scroll api
                    --output-params is a specific params extension that can be used when indexing data with the bulk index api
                    NB : These were added to avoid param pollution problems which occur when an input param is used in an output source
                    (default: null)
--sourceOnly
                    Output only the json contained within the document _source
                    Normal: {"_index":"","_type":"","_id":"", "_source":{SOURCE}}
                    sourceOnly: {SOURCE}
                    (default: false)
--ignore-errors
                    Will continue the read/write loop on write error
                    (default: false)
--scrollId
                    The last scroll Id returned from elasticsearch. 
                    This will allow dumps to be resumed used the last scroll Id &
                    `scrollTime` has not expired.
--scrollTime
                    Time the nodes will hold the requested search in order.
                    (default: 10m)
                    
--scroll-with-post
                    Use a HTTP POST method to perform scrolling instead of the default GET
                    (default: false)
                    
--maxSockets
                    How many simultaneous HTTP requests can we process make?
                    (default:
                      5 [node <= v0.10.x] /
                      Infinity [node >= v0.11.x] )
--timeout
                    Integer containing the number of milliseconds to wait for
                    a request to respond before aborting the request. Passed
                    directly to the request library. Mostly used when you don't
                    care too much if you lose some data when importing
                    but rather have speed.
--offset
                    Integer containing the number of rows you wish to skip
                    ahead from the input transport.  When importing a large
                    index, things can go wrong, be it connectivity, crashes,
                    someone forgetting to `screen`, etc.  This allows you
                    to start the dump again from the last known line written
                    (as logged by the `offset` in the output).  Please be
                    advised that since no sorting is specified when the
                    dump is initially created, there's no real way to
                    guarantee that the skipped rows have already been
                    written/parsed.  This is more of an option for when
                    you want to get most data as possible in the index
                    without concern for losing some rows in the process,
                    similar to the `timeout` option.
                    (default: 0)
--noRefresh
                    Disable input index refresh.
                    Positive:
                      1. Much increase index speed
                      2. Much less hardware requirements
                    Negative:
                      1. Recently added data may not be indexed
                    Recommended to use with big data indexing,
                    where speed and system health in a higher priority
                    than recently added data.
--inputTransport
                    Provide a custom js file to use as the input transport
--outputTransport
                    Provide a custom js file to use as the output transport
--toLog
                    When using a custom outputTransport, should log lines
                    be appended to the output stream?
                    (default: true, except for `$`)
--transform
                    A method/function which can be called to modify documents
                    before writing to a destination. A global variable 'doc'
                    is available.
                    Example script for computing a new field 'f2' as doubled
                    value of field 'f1':
                        doc._source["f2"] = doc._source.f1 * 2;
                    May be used multiple times.
                    Additionally, transform may be performed by a module. See [Module Transform](#module-transform) below.
--awsChain
                    Use [standard](https://aws.amazon.com/blogs/security/a-new-and-standardized-way-to-manage-credentials-in-the-aws-sdks/) location and ordering for resolving credentials including environment variables, config files, EC2 and ECS metadata locations
                    _Recommended option for use with AWS_
                    Use [standard](https://aws.amazon.com/blogs/security/a-new-and-standardized-way-to-manage-credentials-in-the-aws-sdks/) 
                    location and ordering for resolving credentials including environment variables, 
                    config files, EC2 and ECS metadata locations _Recommended option for use with AWS_
--awsAccessKeyId
--awsSecretAccessKey
                    When using Amazon Elasticsearch Service protected by
                    AWS Identity and Access Management (IAM), provide
                    your Access Key ID and Secret Access Key.
                    --sessionToken can also be optionally provided if using temporary credentials
--awsIniFileProfile
                    Alternative to --awsAccessKeyId and --awsSecretAccessKey,
                    loads credentials from a specified profile in aws ini file.
                    For greater flexibility, consider using --awsChain
                    and setting AWS_PROFILE and AWS_CONFIG_FILE
                    environment variables to override defaults if needed
--awsIniFileName
                    Override the default aws ini file name when using --awsIniFileProfile
                    Filename is relative to ~/.aws/
                    (default: config)
--awsService
                    Sets the AWS service that the signature will be generated for
                    (default: calculated from hostname or host)
--awsRegion
                    Sets the AWS region that the signature will be generated for
                    (default: calculated from hostname or host)
--awsUrlRegex
                    Overrides the default regular expression that is used to validate AWS urls that should be signed
                    (default: ^https?:\/\/.*\.amazonaws\.com.*$)
--support-big-int   
                    Support big integer numbers
--big-int-fields   
                    Sepcifies a comma-seperated list of fields that should be checked for big-int support
                    (default '')
--retryAttempts  
                    Integer indicating the number of times a request should be automatically re-attempted before failing
                    when a connection fails with one of the following errors `ECONNRESET`, `ENOTFOUND`, `ESOCKETTIMEDOUT`,
                    ETIMEDOUT`, `ECONNREFUSED`, `EHOSTUNREACH`, `EPIPE`, `EAI_AGAIN`
                    (default: 0)
                    
--retryDelay   
                    Integer indicating the back-off/break period between retry attempts (milliseconds)
                    (default : 5000)            
--parseExtraFields
                    Comma-separated list of meta-fields to be parsed  
--maxRows
                    supports file splitting.  Files are split by the number of rows specified
--fileSize
                    supports file splitting.  This value must be a string supported by the **bytes** module.     
                    The following abbreviations must be used to signify size in terms of units         
                    b for bytes
                    kb for kilobytes
                    mb for megabytes
                    gb for gigabytes
                    tb for terabytes
                    
                    e.g. 10mb / 1gb / 1tb
                    Partitioning helps to alleviate overflow/out of memory exceptions by efficiently segmenting files
                    into smaller chunks that then be merged if needs be.
--fsCompress
                    gzip data before sending output to file.
                    On import the command is used to inflate a gzipped file
--s3AccessKeyId
                    AWS access key ID
--s3SecretAccessKey
                    AWS secret access key
--s3Region
                    AWS region
--s3Endpoint        
                    AWS endpoint can be used for AWS compatible backends such as
                    OpenStack Swift and OpenStack Ceph
--s3SSLEnabled      
                    Use SSL to connect to AWS [default true]
                    
--s3ForcePathStyle  Force path style URLs for S3 objects [default false]
                    
--s3Compress
                    gzip data before sending to s3  
--s3ServerSideEncryption
                    Enables encrypted uploads
--s3SSEKMSKeyId
                    KMS Id to be used with aws:kms uploads                    
--s3ACL
                    S3 ACL: private | public-read | public-read-write | authenticated-read | aws-exec-read |
                    bucket-owner-read | bucket-owner-full-control [default private]

--retryDelayBase
                    The base number of milliseconds to use in the exponential backoff for operation retries. (s3)
--customBackoff
                    Activate custom customBackoff function. (s3)
--tlsAuth
                    Enable TLS X509 client authentication
--cert, --input-cert, --output-cert
                    Client certificate file. Use --cert if source and destination are identical.
                    Otherwise, use the one prefixed with --input or --output as needed.
--key, --input-key, --output-key
                    Private key file. Use --key if source and destination are identical.
                    Otherwise, use the one prefixed with --input or --output as needed.
--pass, --input-pass, --output-pass
                    Pass phrase for the private key. Use --pass if source and destination are identical.
                    Otherwise, use the one prefixed with --input or --output as needed.
--ca, --input-ca, --output-ca
                    CA certificate. Use --ca if source and destination are identical.
                    Otherwise, use the one prefixed with --input or --output as needed.
--inputSocksProxy, --outputSocksProxy
                    Socks5 host address
--inputSocksPort, --outputSocksPort
                    Socks5 host port
--handleVersion
                    Tells elastisearch transport to handle the `_version` field if present in the dataset
                    (default : false)
--versionType
                    Elasticsearch versioning types. Should be `internal`, `external`, `external_gte`, `force`.
                    NB : Type validation is handled by the bulk endpoint and not by elasticsearch-dump
--csvDelimiter        
                    The delimiter that will separate columns.
                    (default : ',')
--csvFirstRowAsHeaders        
                    If set to true the first row will be treated as the headers.
                    (default : true)
--csvRenameHeaders        
                    If you want the first line of the file to be removed and replaced by the one provided in the `csvCustomHeaders` option
                    (default : true)
--csvCustomHeaders  A comma-seperated listed of values that will be used as headers for your data. This param must
                    be used in conjunction with `csvRenameHeaders`
                    (default : null)
--csvWriteHeaders   Determines if headers should be written to the csv file.
                    (default : true)
--csvIgnoreEmpty        
                    Set to true to ignore empty rows. 
                    (default : false)
--csvSkipLines        
                    If number is > 0 the specified number of lines will be skipped.
                    (default : 0)
--csvSkipRows        
                    If number is > 0 then the specified number of parsed rows will be skipped
                    NB:  (If the first row is treated as headers, they aren't a part of the count)
                    (default : 0)
--csvMaxRows        
                    If number is > 0 then only the specified number of rows will be parsed.(e.g. 100 would return the first 100 rows of data)
                    (default : 0)
--csvTrim        
                    Set to true to trim all white space from columns.
                    (default : false)
--csvRTrim        
                    Set to true to right trim all columns.
                    (default : false)
--csvLTrim        
                    Set to true to left trim all columns.
                    (default : false)   
--csvHandleNestedData        
                    Set to true to handle nested JSON/CSV data. 
                    NB : This is a very optioninated implementaton !
                    (default : false)
--csvIdColumn        
                    Name of the column to extract the record identifier (id) from
                    When exporting to CSV this column can be used to override the default id (@id) column name
                    (default : null)   
--csvIndexColumn        
                    Name of the column to extract the record index from
                    When exporting to CSV this column can be used to override the default index (@index) column name
                    (default : null)
--csvTypeColumn        
                    Name of the column to extract the record type from
                    When exporting to CSV this column can be used to override the default type (@type) column name
                    (default : null)              
--help
                    This page
```







## ES 数据导出到CSV

[https://www.cnblogs.com/sanduzxcvbnm/p/12092412.html](https://www.cnblogs.com/sanduzxcvbnm/p/12092412.html)

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
    index => "anxinyun_api-logs"
    query => '{
      "query": {
        "exists": {
          "field": "action_log"
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
    # nested json fields
    fields => ["log_time", "[action_log][app]", "[action_log][event]", "[action_log][wx]", "[action_log][user]", "[action_log][appType]", "[action_log][object]"]
  
    # This is where we store output. We can use several files to store our output
    # by using a timestamp to determine the filename where to store output.    
    path => "/tmp/csv-export.csv"
  }
}
```
docker run --rm -ti -v /home/anxin/tools/logstash/data:/tmp -v /home/anxin/tools/logstash/logstash.yml:/usr/share/logstash/config/logstash.yml -v /home/anxin/tools/logstash/conf.d/:/usr/share/logstash/conf.d/ logstash:6.8.2


## kibana 使用的lucene语法：
https://zhuanlan.zhihu.com/p/33791813
https://segmentfault.com/a/1190000002972420

双引号匹配短语 ""

## ES管理工具

### cerebro

docker run -p 9000:9000 lmenezes/cerebro

![image-20210602091818019](imgs/elastic_search/image-20210602091818019.png)