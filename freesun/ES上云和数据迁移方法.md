# ES上云和数据迁移

Elasticsearch 数据迁移是将数据从一个 Elasticsearch 集群或索引迁移到另一个集群或索引的过程。迁移的原因可能包括集群升级、索引重构、数据备份、跨集群迁移等。以下是几种常见的 Elasticsearch 数据迁移方法：

------

## 1. **使用 Elasticsearch 快照和恢复（Snapshot and Restore）**

这是最推荐的方式，适用于大规模数据迁移，尤其是跨集群迁移。

### 步骤：

1. **配置快照仓库**：
   在源集群上配置一个共享存储（如 AWS S3、HDFS、NFS 等）作为快照仓库。

   ```json
   PUT /_snapshot/my_backup
   {
     "type": "fs",
     "settings": {
       "location": "/mnt/shared_storage/my_backup"
     }
   }
   ```

2. **创建快照**：
   将源集群中的数据备份到快照仓库。

   ```json
   PUT /_snapshot/my_backup/snapshot_1
   {
     "indices": "index_1,index_2",
     "ignore_unavailable": true,
     "include_global_state": false
   }
   ```

3. **恢复快照**：
   在目标集群上配置相同的快照仓库，并恢复数据。

   ```json
   POST /_snapshot/my_backup/snapshot_1/_restore
   {
     "indices": "index_1,index_2",
     "ignore_unavailable": true,
     "include_global_state": false
   }
   ```

### 优点：

- 支持大规模数据迁移。
- 支持增量备份和恢复。
- 适用于跨集群迁移。

### 缺点：

- 需要共享存储（如 S3、NFS 等）。
- 需要提前配置快照仓库。

------

## 2. **使用 Elasticsearch Reindex API**

`Reindex API` 是 Elasticsearch 提供的一个内置工具，用于将一个索引的数据复制到另一个索引。适用于同集群或跨集群的索引迁移。

### 步骤：

1. **同集群迁移**：
   如果源索引和目标索引在同一个集群中，可以直接使用 `Reindex API`。

   ```
   POST /_reindex
   {
     "source": {
       "index": "source_index"
     },
     "dest": {
       "index": "target_index"
     }
   }
   ```

2. **跨集群迁移**：
   如果源索引和目标索引在不同的集群中，需要先在目标集群中配置远程集群信息。

   - 在目标集群中配置远程集群：

     ```
     PUT /_cluster/settings
     {
       "persistent": {
         "cluster.remote.remote_cluster.seeds": "source_cluster_host:9300"
       }
     }
     ```

   - 使用 `Reindex API` 从远程集群迁移数据：

     ```
     POST /_reindex
     {
       "source": {
         "remote": {
           "host": "http://source_cluster_host:9200"
         },
         "index": "source_index"
       },
       "dest": {
         "index": "target_index"
       }
     }
     ```

### 优点：

- 简单易用，无需额外工具。
- 支持跨集群迁移。

### 缺点：

- 对于大规模数据迁移，性能可能较差。
- 需要网络带宽支持。

------

## 3. **使用 Logstash**

Logstash 是一个数据管道工具，可以从源集群读取数据并写入目标集群。

### 步骤：

1. **安装 Logstash**：
   下载并安装 Logstash。

2. **配置 Logstash 管道**：
   创建一个配置文件（如 `logstash.conf`），配置输入（源集群）和输出（目标集群）。

   ```json
   input {
     elasticsearch {
       hosts => ["http://source_cluster_host:9200"]
       index => "source_index"
       size => 1000
       scroll => "5m"
     }
   }
   
   output {
     elasticsearch {
       hosts => ["http://target_cluster_host:9200"]
       index => "target_index"
     }
   }
   ```

3. **运行 Logstash**：
   启动 Logstash 并执行迁移。

   ```
   bin/logstash -f logstash.conf
   ```

### 优点：

- 支持复杂的数据转换和过滤。
- 适用于异构数据源和目标。

### 缺点：

- 需要额外的资源运行 Logstash。
- 对于大规模数据迁移，性能可能较差。

------

## 4. **使用 Elasticsearch Dump**

`elasticsearch-dump` 是一个开源工具，用于导入和导出 Elasticsearch 数据。

### 步骤：

1. **安装 elasticsearch-dump**：
   使用 npm 安装。

   ```
   npm install elasticdump -g
   ```

2. **导出数据**：
   将源集群中的数据导出到文件。

   ```
   elasticdump \
     --input=http://source_cluster_host:9200/source_index \
     --output=/path/to/data.json \
     --type=data
   ```

3. **导入数据**：
   将数据导入到目标集群。

   ```
   elasticdump \
     --input=/path/to/data.json \
     --output=http://target_cluster_host:9200/target_index \
     --type=data
   ```

### 优点：

- 简单易用。
- 支持数据备份和恢复。

### 缺点：

- 对于大规模数据迁移，性能较差。
- 需要额外的存储空间保存导出文件。



------

## 总结

| 方法                   | 适用场景                   | 优点                   | 缺点                           |
| :--------------------- | :------------------------- | :--------------------- | :----------------------------- |
| **快照和恢复**         | 大规模数据迁移、跨集群迁移 | 高效、支持增量备份     | 需要共享存储                   |
| **Reindex API**        | 同集群或跨集群迁移         | 简单易用、无需额外工具 | 性能较差、依赖网络带宽         |
| **Logstash**           | 复杂数据迁移、异构数据源   | 支持数据转换和过滤     | 需要额外资源、性能较差         |
| **elasticsearch-dump** | 小规模数据迁移、数据备份   | 简单易用               | 性能较差、需要额外存储         |
| **手动迁移**           | 小规模数据迁移             | 无需额外工具           | 手动操作繁琐、不适合大规模数据 |

根据你的需求和数据规模，选择合适的方法进行迁移。对于大规模数据迁移，推荐使用 **快照和恢复** 或



建库脚本（数据按年存储）

```json
PUT /_template/anxinyun_vbraws-template
{
    "index_patterns" : [
      "anxinyun_vbraws*",
      "anxincloud_vbraws*"
    ],
    "settings" : {
      "index" : {
        "refresh_interval" : "120s",
        "unassigned" : {
          "node_left" : {
            "delayed_timeout" : "5m"
          }
        },
        "number_of_shards" : "6",
        "translog" : {
          "flush_threshold_size" : "1024mb",
          "sync_interval" : "120s",
          "durability" : "async"
        },
        "number_of_replicas" : "1"
      }
    },
    "mappings" : {
        "dynamic" : "strict",
        "properties" : {
          "structId" : {
            "type" : "long"
          },
          "create_time" : {
            "format" : "date_time || yyyy-MM-dd HH:mm:ss",
            "type" : "date"
          },
          "data" : {
            "dynamic" : "strict",
            "properties" : {
              "raw" : {
                "type" : "float"
              }
            }
          },
          "param" : {
            "dynamic" : "strict",
            "properties" : {
              "sampleFreq" : {
                "type" : "float"
              },
              "gainAmplifier" : {
                "type" : "long"
              },
              "filterFreq" : {
                "type" : "float"
              },
              "triggerType" : {
                "type" : "long"
              },
              "version" : {
                "type" : "long"
              }
            }
          },
          "iota_device" : {
            "type" : "keyword"
          },
          "iota_device_name" : {
            "norms" : false,
            "type" : "text",
            "fields" : {
              "keyword" : {
                "ignore_above" : 100,
                "type" : "keyword"
              }
            }
          },
          "collect_time" : {
            "format" : "date_time || yyy-MM-dd HH:mm:ss",
            "type" : "date"
          }
        }
      
    },
    "aliases" : {
      "anxincloud_vbraws" : { }
    }
  }

PUT /_template/anxinyun-raw-data-template
{
    "index_patterns" : [
      "anxinyun_raws*",
      "anxincloud_raws*"
    ],
    "settings" : {
      "index" : {
        "refresh_interval" : "120s",
        "number_of_shards" : "6",
        "translog" : {
          "flush_threshold_size" : "1024mb",
          "sync_interval" : "120s",
          "durability" : "async"
        },
        "number_of_replicas" : "1"
      }
    },
    "mappings" : {
        "dynamic" : "false",
        "properties" : {
          "structId" : {
            "type" : "long"
          },
          "create_time" : {
            "format" : "date_time || uuuu-MM-dd HH:mm:ss",
            "type" : "date"
          },
          "data" : {
            "dynamic" : "true",
            "properties" : {
              "totrainfall" : {
                "type" : "long"
              },
              "temprature" : {
                "type" : "long"
              },
              "showstring" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "WindSpeed" : {
                "type" : "float"
              },
              "alarmPic" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "device_outAddCount" : {
                "type" : "long"
              },
              "peccancy" : {
                "type" : "long"
              },
              "Radiusdeg" : {
                "type" : "float"
              },
              "humidity" : {
                "type" : "float"
              },
              "m_water" : {
                "type" : "long"
              },
              "easting" : {
                "type" : "float"
              },
              "am" : {
                "type" : "float"
              },
              "overload" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "winddirection" : {
                "type" : "long"
              },
              "maxnf" : {
                "type" : "float"
              },
              "high11" : {
                "type" : "long"
              },
              "high12" : {
                "type" : "long"
              },
              "checkTime" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "obliguityY" : {
                "type" : "long"
              },
              "obliguityX" : {
                "type" : "long"
              },
              "ax" : {
                "type" : "float"
              },
              "ay" : {
                "type" : "float"
              },
              "A_power_factor" : {
                "type" : "float"
              },
              "valid_date_end" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "ltde" : {
                "type" : "float"
              },
              "dyStatu" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "returntime" : {
                "type" : "date"
              },
              "wind" : {
                "type" : "long"
              },
              "B_phase_volt" : {
                "type" : "float"
              },
              "F" : {
                "type" : "float"
              },
              "strfx" : {
                "type" : "long"
              },
              "AElectric" : {
                "type" : "long"
              },
              "BVoltage" : {
                "type" : "long"
              },
              "ein" : {
                "type" : "float"
              },
              "DI2" : {
                "type" : "long"
              },
              "sfStatu" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "DI1" : {
                "type" : "long"
              },
              "stemp" : {
                "type" : "long"
              },
              "DI4" : {
                "type" : "long"
              },
              "maxliftingweight" : {
                "type" : "float"
              },
              "DI3" : {
                "type" : "long"
              },
              "DI6" : {
                "type" : "long"
              },
              "P" : {
                "type" : "float"
              },
              "averflowspeed" : {
                "type" : "long"
              },
              "result" : {
                "type" : "long"
              },
              "DI5" : {
                "type" : "long"
              },
              "Q" : {
                "type" : "float"
              },
              "alarmCount" : {
                "type" : "long"
              },
              "uid" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "DI8" : {
                "type" : "long"
              },
              "DI7" : {
                "type" : "long"
              },
              "humanid" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "remainParkNum" : {
                "type" : "long"
              },
              "stayCount" : {
                "type" : "long"
              },
              "X" : {
                "type" : "float"
              },
              "Y" : {
                "type" : "float"
              },
              "national" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "allRotationAngle" : {
                "type" : "long"
              },
              "csLevle" : {
                "type" : "long"
              },
              "address" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "dq_state" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "e" : {
                "type" : "float"
              },
              "data2" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "sex" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "B_active_power" : {
                "type" : "float"
              },
              "ydStatu" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "data0" : {
                "type" : "long"
              },
              "flowspeed" : {
                "type" : "long"
              },
              "co" : {
                "type" : "float"
              },
              "plateNumber" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "addDate" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "CH4" : {
                "type" : "long"
              },
              "n" : {
                "type" : "float"
              },
              "prjName" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "device_inAddCount" : {
                "type" : "long"
              },
              "u" : {
                "type" : "float"
              },
              "p_name" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "x" : {
                "type" : "float"
              },
              "y" : {
                "type" : "long"
              },
              "z" : {
                "type" : "float"
              },
              "arch" : {
                "type" : "long"
              },
              "currentState" : {
                "type" : "long"
              },
              "ack" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "pm100" : {
                "type" : "long"
              },
              "DO" : {
                "type" : "long"
              },
              "xvalue" : {
                "type" : "float"
              },
              "down" : {
                "type" : "long"
              },
              "refresh_time" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "waterlevel1" : {
                "type" : "float"
              },
              "dx" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "RRange" : {
                "type" : "float"
              },
              "dy" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "dz" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "swVal" : {
                "type" : "float"
              },
              "warning" : {
                "type" : "long"
              },
              "radius" : {
                "type" : "float"
              },
              "windSpeed" : {
                "type" : "float"
              },
              "signal" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "Leakage" : {
                "type" : "long"
              },
              "image" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "stype" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "H2S" : {
                "type" : "float"
              },
              "count" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "VOC" : {
                "type" : "float"
              },
              "Weight" : {
                "type" : "long"
              },
              "offCount" : {
                "type" : "long"
              },
              "WindLevel" : {
                "type" : "long"
              },
              "LBFH_PIR" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "Roll" : {
                "type" : "long"
              },
              "alarm_id" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "projectId" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "LBFH_LB1" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "codmn" : {
                "type" : "float"
              },
              "isReply" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "C_power_factor" : {
                "type" : "float"
              },
              "LBFH_LB2" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "VOCS" : {
                "type" : "float"
              },
              "ATemperature" : {
                "type" : "long"
              },
              "angle_y" : {
                "type" : "float"
              },
              "idcard_number" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "angle_x" : {
                "type" : "long"
              },
              "total_active_power" : {
                "type" : "float"
              },
              "strlicense" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "timeget" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "localtion" : {
                "type" : "long"
              },
              "back" : {
                "type" : "long"
              },
              "crossRoad" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "voc" : {
                "type" : "long"
              },
              "Xangle" : {
                "type" : "float"
              },
              "gX" : {
                "type" : "float"
              },
              "xDegree" : {
                "type" : "float"
              },
              "gY" : {
                "type" : "float"
              },
              "gZ" : {
                "type" : "float"
              },
              "video_url" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "load" : {
                "type" : "float"
              },
              "total_reactive_power" : {
                "type" : "float"
              },
              "lamp_ctrl_id" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "image_hkurl" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "manageStatus" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "Multiple" : {
                "type" : "long"
              },
              "AuthorState" : {
                "type" : "long"
              },
              "gatewayName" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "h2s" : {
                "type" : "long"
              },
              "length" : {
                "type" : "float"
              },
              "pm10" : {
                "type" : "long"
              },
              "inCount" : {
                "type" : "long"
              },
              "A_active_power" : {
                "type" : "float"
              },
              "powerQuantity" : {
                "type" : "long"
              },
              "gx" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "gy" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "lampControllerType" : {
                "type" : "long"
              },
              "gz" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "totalrainfall" : {
                "type" : "float"
              },
              "PM10" : {
                "type" : "long"
              },
              "START" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "age" : {
                "type" : "long"
              },
              "hum" : {
                "type" : "long"
              },
              "doorState" : {
                "type" : "long"
              },
              "onlineStatus" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "pmpm10" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "dq_weight" : {
                "type" : "long"
              },
              "lamp_ctrl_type" : {
                "type" : "long"
              },
              "readingNumber" : {
                "type" : "long"
              },
              "organizationId" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "number" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "toPower" : {
                "type" : "long"
              },
              "payType" : {
                "type" : "long"
              },
              "LBFH_HC" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "displacement" : {
                "type" : "float"
              },
              "DEV_sn" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "join" : {
                "type" : "long"
              },
              "cableforce" : {
                "type" : "long"
              },
              "enterTime" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "flow" : {
                "type" : "float"
              },
              "lgte" : {
                "type" : "float"
              },
              "TotalDE" : {
                "type" : "float"
              },
              "leaveTime" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "zdValue" : {
                "type" : "float"
              },
              "serialNum" : {
                "type" : "long"
              },
              "stateCodestr" : {
                "type" : "long"
              },
              "PM25" : {
                "type" : "long"
              },
              "LBFH_H1" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "LBFH_H2" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "waterPresure" : {
                "type" : "long"
              },
              "TotalDN" : {
                "type" : "float"
              },
              "male_stay" : {
                "type" : "long"
              },
              "paidFee" : {
                "type" : "long"
              },
              "TotalDH" : {
                "type" : "float"
              },
              "tflow" : {
                "type" : "long"
              },
              "pm25" : {
                "type" : "long"
              },
              "hourRainfall" : {
                "type" : "long"
              },
              "Ia" : {
                "type" : "long"
              },
              "instantFlow" : {
                "type" : "long"
              },
              "Ib" : {
                "type" : "long"
              },
              "male_in" : {
                "type" : "long"
              },
              "port_id" : {
                "type" : "long"
              },
              "Ic" : {
                "type" : "long"
              },
              "interval" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "third_stay" : {
                "type" : "long"
              },
              "gatewayId" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "waterrate" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "gateway_addr" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "status" : {
                "type" : "long"
              },
              "phValue" : {
                "type" : "long"
              },
              "CVoltage" : {
                "type" : "long"
              },
              "city" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "Io" : {
                "type" : "long"
              },
              "windLevel" : {
                "type" : "long"
              },
              "BAT_DL" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "fsz" : {
                "type" : "long"
              },
              "persionName" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "stringFactor" : {
                "type" : "long"
              },
              "timeBegin" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "waterlevel" : {
                "type" : "float"
              },
              "Weightdeg" : {
                "type" : "float"
              },
              "so2" : {
                "type" : "float"
              },
              "third_out" : {
                "type" : "long"
              },
              "noise" : {
                "type" : "long"
              },
              "runstate" : {
                "type" : "long"
              },
              "COD" : {
                "type" : "long"
              },
              "angle" : {
                "type" : "float"
              },
              "advName" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "elecNumber" : {
                "type" : "float"
              },
              "Phy" : {
                "type" : "float"
              },
              "C2H5OH" : {
                "type" : "float"
              },
              "device_sn" : {
                "type" : "long"
              },
              "forward" : {
                "type" : "long"
              },
              "t65mp" : {
                "type" : "float"
              },
              "sectionId" : {
                "type" : "long"
              },
              "dblControlZigbeeLamp" : {
                "type" : "boolean"
              },
              "pre_on" : {
                "type" : "long"
              },
              "Yangle" : {
                "type" : "float"
              },
              "recordTime" : {
                "type" : "date"
              },
              "cod" : {
                "type" : "float"
              },
              "strcolor" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "LBFH_DS" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "weightK" : {
                "type" : "long"
              },
              "cos" : {
                "type" : "float"
              },
              "pic" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "vibration" : {
                "type" : "long"
              },
              "device_outTotalCount" : {
                "type" : "long"
              },
              "Rotationdeg" : {
                "type" : "float"
              },
              "alarm_status" : {
                "type" : "long"
              },
              "total_power_factor" : {
                "type" : "float"
              },
              "divisionId" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "oxygen" : {
                "type" : "long"
              },
              "lat" : {
                "type" : "float"
              },
              "axisWeight" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "longitude" : {
                "type" : "float"
              },
              "tiltPercent" : {
                "type" : "float"
              },
              "valid_date_start" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "liftheight" : {
                "type" : "float"
              },
              "o3" : {
                "type" : "float"
              },
              "weightk" : {
                "type" : "long"
              },
              "humidy" : {
                "type" : "long"
              },
              "level" : {
                "type" : "long"
              },
              "cmdstr" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "pressure" : {
                "type" : "float"
              },
              "codValue" : {
                "type" : "long"
              },
              "trash" : {
                "type" : "long"
              },
              "TSP" : {
                "type" : "long"
              },
              "BTemperature" : {
                "type" : "long"
              },
              "alarmStatus" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "NH3" : {
                "type" : "long"
              },
              "nh3" : {
                "type" : "long"
              },
              "paramData" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "cmsLength" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "B_power_factor" : {
                "type" : "float"
              },
              "axisnum" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "totalBase" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "minnf" : {
                "type" : "float"
              },
              "Obliquitydeg" : {
                "type" : "float"
              },
              "yvalue" : {
                "type" : "float"
              },
              "birthday" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "anglex" : {
                "type" : "float"
              },
              "isTrack" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "angley" : {
                "type" : "float"
              },
              "temperture" : {
                "type" : "float"
              },
              "adValue" : {
                "type" : "float"
              },
              "axisWeights" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "ptype" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "tsp" : {
                "type" : "long"
              },
              "pm0" : {
                "type" : "float"
              },
              "total" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "carType" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "oil" : {
                "type" : "float"
              },
              "C_active_power" : {
                "type" : "float"
              },
              "Height" : {
                "type" : "float"
              },
              "axisNum" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "floor" : {
                "type" : "long"
              },
              "lang" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "sleeptype" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "lane" : {
                "type" : "long"
              },
              "direction" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "on" : {
                "type" : "long"
              },
              "CSQ" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "averFlowSpeed" : {
                "type" : "long"
              },
              "OU" : {
                "type" : "long"
              },
              "ou" : {
                "type" : "long"
              },
              "toWater" : {
                "type" : "long"
              },
              "device_inTotalCount" : {
                "type" : "long"
              },
              "Cidentitynumber" : {
                "type" : "long"
              },
              "intensity" : {
                "type" : "long"
              },
              "alarmType" : {
                "type" : "long"
              },
              "lengths" : {
                "type" : "float"
              },
              "UserId" : {
                "type" : "long"
              },
              "PH" : {
                "type" : "float"
              },
              "cidMessage" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "ph" : {
                "type" : "long"
              },
              "online" : {
                "type" : "long"
              },
              "payStatus" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "pm" : {
                "type" : "float"
              },
              "total_energy" : {
                "type" : "float"
              },
              "reason" : {
                "type" : "long"
              },
              "alarmMsg" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "alarmDeviceName" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "idCard" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "lampPoleType" : {
                "type" : "long"
              },
              "sxbStatu" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "C_phase_volt" : {
                "type" : "float"
              },
              "physicalvalue" : {
                "type" : "float"
              },
              "pm2_5" : {
                "type" : "float"
              },
              "bri" : {
                "type" : "long"
              },
              "state" : {
                "type" : "long"
              },
              "cidCode" : {
                "type" : "long"
              },
              "height" : {
                "type" : "float"
              },
              "C_phase_current" : {
                "type" : "float"
              },
              "angleX" : {
                "type" : "float"
              },
              "angleY" : {
                "type" : "float"
              },
              "heartrate" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "pre_refresh_time" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "runoff" : {
                "type" : "long"
              },
              "h641mi" : {
                "type" : "float"
              },
              "Authmode" : {
                "type" : "long"
              },
              "Authresult" : {
                "type" : "long"
              },
              "female_in" : {
                "type" : "long"
              },
              "node" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "male_out" : {
                "type" : "long"
              },
              "CElectric" : {
                "type" : "long"
              },
              "yDegree" : {
                "type" : "float"
              },
              "telnum" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "stateMessagestr" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "latitude" : {
                "type" : "float"
              },
              "torque" : {
                "type" : "long"
              },
              "enable_alarm" : {
                "type" : "long"
              },
              "long" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "moment" : {
                "type" : "long"
              },
              "current" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "flowSpeed" : {
                "type" : "long"
              },
              "temperature" : {
                "type" : "float"
              },
              "xang" : {
                "type" : "float"
              },
              "loadValue" : {
                "type" : "long"
              },
              "elongationIndicator" : {
                "type" : "float"
              },
              "totalPhosphorus" : {
                "type" : "float"
              },
              "Heightdeg" : {
                "type" : "float"
              },
              "jyLevle" : {
                "type" : "float"
              },
              "residence_address" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "plateCity" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "tunnelface" : {
                "type" : "long"
              },
              "rotationAngle" : {
                "type" : "float"
              },
              "windspeed" : {
                "type" : "float"
              },
              "ibs" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "alarmReason" : {
                "type" : "long"
              },
              "peopleCnt" : {
                "type" : "long"
              },
              "ryValue" : {
                "type" : "long"
              },
              "wateryield" : {
                "type" : "long"
              },
              "tilt" : {
                "type" : "float"
              },
              "frequency" : {
                "type" : "float"
              },
              "no2" : {
                "type" : "float"
              },
              "hat" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "up" : {
                "type" : "long"
              },
              "eNumber" : {
                "type" : "float"
              },
              "weight" : {
                "type" : "long"
              },
              "SWZid" : {
                "type" : "long"
              },
              "Speeddeg" : {
                "type" : "long"
              },
              "Ua" : {
                "type" : "float"
              },
              "Ub" : {
                "type" : "float"
              },
              "isChecked" : {
                "type" : "long"
              },
              "Uc" : {
                "type" : "float"
              },
              "signals" : {
                "type" : "long"
              },
              "name" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "pitstate" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "electr" : {
                "type" : "long"
              },
              "inflow" : {
                "type" : "long"
              },
              "Uo" : {
                "type" : "long"
              },
              "pm2d5" : {
                "type" : "float"
              },
              "Odor" : {
                "type" : "float"
              },
              "statusget" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "gmsPower" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "distance" : {
                "type" : "float"
              },
              "B_phase_current" : {
                "type" : "float"
              },
              "AVoltage" : {
                "type" : "long"
              },
              "settling" : {
                "type" : "float"
              },
              "speed" : {
                "type" : "float"
              },
              "A_phase_current" : {
                "type" : "float"
              },
              "overweight" : {
                "type" : "long"
              },
              "cautch" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "mask" : {
                "type" : "boolean"
              },
              "wightPercent" : {
                "type" : "float"
              },
              "rotation" : {
                "type" : "long"
              },
              "RatedWeight" : {
                "type" : "long"
              },
              "voltage" : {
                "type" : "float"
              },
              "waterfolw" : {
                "type" : "long"
              },
              "deflection" : {
                "type" : "float"
              },
              "yang" : {
                "type" : "float"
              },
              "time" : {
                "type" : "long"
              },
              "image_hk_url" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "curweight" : {
                "type" : "float"
              },
              "lampPoleNumber" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "totalFlow" : {
                "type" : "float"
              },
              "trend" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "BElectric" : {
                "type" : "long"
              },
              "windForce" : {
                "type" : "long"
              },
              "waterTemperature" : {
                "type" : "float"
              },
              "PersonType" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "alarmCode" : {
                "type" : "long"
              },
              "type" : {
                "type" : "long"
              },
              "northing" : {
                "type" : "float"
              },
              "windDirection2" : {
                "type" : "long"
              },
              "powerState" : {
                "type" : "long"
              },
              "female_stay" : {
                "type" : "long"
              },
              "zang" : {
                "type" : "float"
              },
              "ssagee" : {
                "type" : "float"
              },
              "inputState" : {
                "type" : "long"
              },
              "ctemp" : {
                "type" : "float"
              },
              "personname" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "CTemperature" : {
                "type" : "long"
              },
              "dataContex" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "isDownline" : {
                "type" : "long"
              },
              "tol_water" : {
                "type" : "float"
              },
              "safeLoad" : {
                "type" : "long"
              },
              "Uab" : {
                "type" : "float"
              },
              "strimage" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "force" : {
                "type" : "float"
              },
              "high2" : {
                "type" : "long"
              },
              "turbidity" : {
                "type" : "long"
              },
              "stepvalue" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "typeName" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "range" : {
                "type" : "float"
              },
              "lon" : {
                "type" : "float"
              },
              "DEV_type" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "strdeviceName" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "gpsStatu" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "amplitude" : {
                "type" : "float"
              },
              "speedofWind" : {
                "type" : "float"
              },
              "Ubc" : {
                "type" : "float"
              },
              "humi" : {
                "type" : "float"
              },
              "power" : {
                "type" : "long"
              },
              "torquePercent" : {
                "type" : "long"
              },
              "lamp_post_type" : {
                "type" : "long"
              },
              "directionAngle" : {
                "type" : "long"
              },
              "heightPercent" : {
                "type" : "long"
              },
              "phy_y" : {
                "type" : "long"
              },
              "timeEnd" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "phy_x" : {
                "type" : "long"
              },
              "liftnumber" : {
                "type" : "float"
              },
              "waterLever" : {
                "type" : "float"
              },
              "solarRadiation" : {
                "type" : "long"
              },
              "alarmPicUrl" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "A_phase_volt" : {
                "type" : "float"
              },
              "outflow" : {
                "type" : "long"
              },
              "GPS_DEV_J" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "Uca" : {
                "type" : "float"
              },
              "Angle" : {
                "type" : "long"
              },
              "waterLevel" : {
                "type" : "float"
              },
              "axieSpeed" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "TAN" : {
                "type" : "long"
              },
              "azimuthofWind" : {
                "type" : "float"
              },
              "addUser" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "total_phase_current" : {
                "type" : "float"
              },
              "pictureHex" : {
                "type" : "float"
              },
              "ltype" : {
                "type" : "long"
              },
              "modifyUser" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "Moment" : {
                "type" : "long"
              },
              "conductivity" : {
                "type" : "long"
              },
              "reactive_power" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "windDirection" : {
                "type" : "long"
              },
              "data_out" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "mileage" : {
                "type" : "float"
              },
              "gmValue" : {
                "type" : "float"
              },
              "licence" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "rain" : {
                "type" : "long"
              },
              "ver" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "orgName" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "modifyDate" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "axieWeight" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "strtime" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "lvdt" : {
                "type" : "float"
              },
              "runtime" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "manufactor" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "controlState" : {
                "type" : "long"
              },
              "pEnergy" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "oxy" : {
                "type" : "float"
              },
              "accStatu" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "p100" : {
                "type" : "float"
              },
              "termStatu" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "zlValue" : {
                "type" : "float"
              },
              "sEnergy" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "Pitch" : {
                "type" : "long"
              },
              "active_power" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "angele_x" : {
                "type" : "long"
              },
              "proversion" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "angele_y" : {
                "type" : "float"
              },
              "female_out" : {
                "type" : "long"
              },
              "elevationAngle" : {
                "type" : "long"
              },
              "temperatur" : {
                "type" : "long"
              },
              "qEnergy" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "axisSpeed" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "isOnline" : {
                "type" : "long"
              },
              "_acq_number" : {
                "type" : "long"
              },
              "third_in" : {
                "type" : "long"
              },
              "Obliguity" : {
                "type" : "long"
              },
              "Temp" : {
                "type" : "float"
              },
              "outCount" : {
                "type" : "long"
              },
              "batvolt" : {
                "type" : "float"
              },
              "dumpnumber" : {
                "type" : "long"
              },
              "alarm" : {
                "type" : "long"
              },
              "flowRate" : {
                "type" : "float"
              },
              "CertificateNumber" : {
                "type" : "long"
              },
              "useTime" : {
                "type" : "long"
              },
              "wsValue" : {
                "type" : "float"
              },
              "addr" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "value" : {
                "type" : "float"
              },
              "data_in" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "rainfall" : {
                "type" : "long"
              },
              "temp" : {
                "type" : "float"
              },
              "obliquity" : {
                "type" : "float"
              },
              "electric" : {
                "type" : "long"
              },
              "winddir" : {
                "type" : "long"
              },
              "humid" : {
                "type" : "float"
              },
              "organ_issue" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "zvalue" : {
                "type" : "float"
              },
              "_mode" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "rainFall" : {
                "type" : "long"
              },
              "grossWeight" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "langStatu" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "StrUserName" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              }
            }
          },
          "iota_device" : {
            "type" : "keyword"
          },
          "iota_device_name" : {
            "norms" : false,
            "type" : "text",
            "fields" : {
              "keyword" : {
                "ignore_above" : 100,
                "type" : "keyword"
              }
            }
          },
          "collect_time" : {
            "format" : "date_time || uuuu-MM-dd HH:mm:ss",
            "type" : "date"
          }
        }
      
    },
    "aliases" : {
      "anxincloud_raws" : { }
    }
  }

PUT /_template/anxinyun_themes-template
{
    "index_patterns" : [
      "anxinyun_themes*",
      "anxincloud_themes*"
    ],
    "settings" : {
      "index" : {
        "mapping" : {
          "total_fields" : {
            "limit" : "2100"
          }
        },
        "refresh_interval" : "60s",
        "unassigned" : {
          "node_left" : {
            "delayed_timeout" : "5m"
          }
        },
        "number_of_shards" : "6",
        "translog" : {
          "flush_threshold_size" : "1024mb",
          "sync_interval" : "60s",
          "durability" : "async"
        },
        "number_of_replicas" : "1"
      }
    },
    "mappings" : {
        "dynamic" : "false",
        "properties" : {
          "create_time" : {
            "format" : "date_time || uuuu-MM-dd HH:mm:ss",
            "type" : "date"
          },
          "data" : {
            "dynamic" : "true",
            "properties" : {
              "total_energy" : {
                "type" : "float"
              },
              "totrainfall" : {
                "type" : "float"
              },
              "alarmMsg" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "tds" : {
                "type" : "float"
              },
              "pv" : {
                "type" : "double"
              },
              "peccancy" : {
                "type" : "float"
              },
              "crack" : {
                "type" : "float"
              },
              "C_phase_volt" : {
                "type" : "float"
              },
              "pm2_5" : {
                "type" : "float"
              },
              "bri" : {
                "type" : "float"
              },
              "humidity" : {
                "type" : "float"
              },
              "state" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "height" : {
                "type" : "float"
              },
              "C_phase_current" : {
                "type" : "float"
              },
              "watertemp" : {
                "type" : "float"
              },
              "axleCount" : {
                "type" : "float"
              },
              "heartrate" : {
                "type" : "float"
              },
              "overload" : {
                "type" : "float"
              },
              "winddirection" : {
                "type" : "float"
              },
              "female_in" : {
                "type" : "float"
              },
              "high10" : {
                "type" : "long"
              },
              "high11" : {
                "type" : "long"
              },
              "high12" : {
                "type" : "long"
              },
              "high13" : {
                "type" : "long"
              },
              "obliguityY" : {
                "type" : "float"
              },
              "obliguityX" : {
                "type" : "float"
              },
              "xTotal" : {
                "type" : "float"
              },
              "male_out" : {
                "type" : "float"
              },
              "A_power_factor" : {
                "type" : "float"
              },
              "ltde" : {
                "type" : "float"
              },
              "dyStatu" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "CElectric" : {
                "type" : "float"
              },
              "telnum" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "B_phase_volt" : {
                "type" : "float"
              },
              "test_c" : {
                "type" : "float"
              },
              "test_b" : {
                "type" : "float"
              },
              "test_a" : {
                "type" : "float"
              },
              "latitude" : {
                "type" : "float"
              },
              "turb" : {
                "type" : "float"
              },
              "AElectric" : {
                "type" : "float"
              },
              "BVoltage" : {
                "type" : "float"
              },
              "DI2" : {
                "type" : "float"
              },
              "carSpeed" : {
                "type" : "float"
              },
              "DI1" : {
                "type" : "float"
              },
              "long" : {
                "type" : "float"
              },
              "DI3" : {
                "type" : "float"
              },
              "moment" : {
                "type" : "float"
              },
              "alarmCount" : {
                "type" : "float"
              },
              "current" : {
                "type" : "float"
              },
              "min" : {
                "type" : "double"
              },
              "humanid" : {
                "type" : "float"
              },
              "temperature" : {
                "type" : "double"
              },
              "allRotationAngle" : {
                "type" : "float"
              },
              "personType" : {
                "type" : "float"
              },
              "arrAcc" : {
                "type" : "float"
              },
              "address" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "totalPhosphorus" : {
                "type" : "float"
              },
              "B_active_power" : {
                "type" : "float"
              },
              "h" : {
                "type" : "float"
              },
              "co" : {
                "type" : "float"
              },
              "plateNumber" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "CH4" : {
                "type" : "float"
              },
              "tunnelface" : {
                "type" : "float"
              },
              "ppv" : {
                "type" : "double"
              },
              "x" : {
                "type" : "float"
              },
              "windspeed" : {
                "type" : "float"
              },
              "y" : {
                "type" : "float"
              },
              "z" : {
                "type" : "float"
              },
              "arch" : {
                "type" : "float"
              },
              "ibs" : {
                "type" : "float"
              },
              "currentState" : {
                "type" : "float"
              },
              "sewage" : {
                "type" : "float"
              },
              "displacement_z" : {
                "type" : "float"
              },
              "peopleCnt" : {
                "type" : "float"
              },
              "displacement_x" : {
                "type" : "float"
              },
              "displacement_y" : {
                "type" : "float"
              },
              "pm100" : {
                "type" : "float"
              },
              "do" : {
                "type" : "float"
              },
              "tilt" : {
                "type" : "float"
              },
              "deviceName" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "down" : {
                "type" : "float"
              },
              "frequency" : {
                "type" : "float"
              },
              "no2" : {
                "type" : "float"
              },
              "trms" : {
                "type" : "double"
              },
              "dx" : {
                "type" : "float"
              },
              "dz" : {
                "type" : "float"
              },
              "directionstr" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "warning" : {
                "type" : "float"
              },
              "up" : {
                "type" : "float"
              },
              "windSpeed" : {
                "type" : "float"
              },
              "humnum" : {
                "type" : "float"
              },
              "Leakage" : {
                "type" : "float"
              },
              "image" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "H2S" : {
                "type" : "float"
              },
              "count" : {
                "type" : "float"
              },
              "weight" : {
                "type" : "float"
              },
              "VOC" : {
                "type" : "float"
              },
              "Weight" : {
                "type" : "float"
              },
              "offCount" : {
                "type" : "float"
              },
              "volume" : {
                "type" : "float"
              },
              "signals" : {
                "type" : "float"
              },
              "name" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "electr" : {
                "type" : "float"
              },
              "inflow" : {
                "type" : "float"
              },
              "Odor" : {
                "type" : "float"
              },
              "codmn" : {
                "type" : "float"
              },
              "C_power_factor" : {
                "type" : "float"
              },
              "cableForce" : {
                "type" : "float"
              },
              "VOCS" : {
                "type" : "float"
              },
              "ATemperature" : {
                "type" : "float"
              },
              "color" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "distance" : {
                "type" : "float"
              },
              "B_phase_current" : {
                "type" : "float"
              },
              "total_active_power" : {
                "type" : "float"
              },
              "localtion" : {
                "type" : "float"
              },
              "back" : {
                "type" : "float"
              },
              "axleLoad" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "AVoltage" : {
                "type" : "float"
              },
              "voc" : {
                "type" : "float"
              },
              "humanidstr" : {
                "type" : "float"
              },
              "settling" : {
                "type" : "float"
              },
              "speed" : {
                "type" : "float"
              },
              "load" : {
                "type" : "float"
              },
              "A_phase_current" : {
                "type" : "float"
              },
              "isKeepout" : {
                "type" : "float"
              },
              "total_reactive_power" : {
                "type" : "float"
              },
              "vocs" : {
                "type" : "float"
              },
              "manageStatus" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "strOverload" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "AuthorState" : {
                "type" : "float"
              },
              "gatewayName" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "h2s" : {
                "type" : "float"
              },
              "pm10" : {
                "type" : "float"
              },
              "humname" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "seepage" : {
                "type" : "float"
              },
              "A_active_power" : {
                "type" : "float"
              },
              "powerQuantity" : {
                "type" : "float"
              },
              "voltage" : {
                "type" : "float"
              },
              "deflection" : {
                "type" : "double"
              },
              "gravity" : {
                "type" : "float"
              },
              "PM10" : {
                "type" : "float"
              },
              "time" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "lampPoleNumber" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "doorState" : {
                "type" : "float"
              },
              "totalFlow" : {
                "type" : "float"
              },
              "yTotal" : {
                "type" : "float"
              },
              "BElectric" : {
                "type" : "float"
              },
              "humility" : {
                "type" : "double"
              },
              "onlineStatus" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "stateMessage" : {
                "type" : "float"
              },
              "number" : {
                "type" : "float"
              },
              "payType" : {
                "type" : "float"
              },
              "powerState" : {
                "type" : "float"
              },
              "female_stay" : {
                "type" : "float"
              },
              "displacement" : {
                "type" : "float"
              },
              "inputState" : {
                "type" : "float"
              },
              "enterTime" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "flow" : {
                "type" : "long"
              },
              "lgte" : {
                "type" : "float"
              },
              "leaveTime" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "CTemperature" : {
                "type" : "float"
              },
              "high1" : {
                "type" : "long"
              },
              "male_stay" : {
                "type" : "float"
              },
              "paidFee" : {
                "type" : "float"
              },
              "soundLevel" : {
                "type" : "double"
              },
              "pm25" : {
                "type" : "float"
              },
              "safeLoad" : {
                "type" : "float"
              },
              "high4" : {
                "type" : "long"
              },
              "instantFlow" : {
                "type" : "float"
              },
              "high5" : {
                "type" : "long"
              },
              "male_in" : {
                "type" : "float"
              },
              "force" : {
                "type" : "float"
              },
              "high2" : {
                "type" : "long"
              },
              "high3" : {
                "type" : "long"
              },
              "high8" : {
                "type" : "long"
              },
              "high9" : {
                "type" : "long"
              },
              "high6" : {
                "type" : "long"
              },
              "high7" : {
                "type" : "long"
              },
              "humanidstring" : {
                "type" : "float"
              },
              "diffX" : {
                "type" : "float"
              },
              "CVoltage" : {
                "type" : "float"
              },
              "stepvalue" : {
                "type" : "float"
              },
              "weightPercent" : {
                "type" : "float"
              },
              "gender" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "city" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "windLevel" : {
                "type" : "float"
              },
              "range" : {
                "type" : "float"
              },
              "lon" : {
                "type" : "float"
              },
              "gpsStatu" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "instant" : {
                "type" : "float"
              },
              "stringFactor" : {
                "type" : "float"
              },
              "waterlevel" : {
                "type" : "float"
              },
              "so2" : {
                "type" : "float"
              },
              "noise" : {
                "type" : "float"
              },
              "runstate" : {
                "type" : "float"
              },
              "angle" : {
                "type" : "float"
              },
              "power" : {
                "type" : "float"
              },
              "torquePercent" : {
                "type" : "float"
              },
              "directionAngle" : {
                "type" : "float"
              },
              "heightPercent" : {
                "type" : "float"
              },
              "stress" : {
                "type" : "float"
              },
              "C2H5OH" : {
                "type" : "float"
              },
              "max" : {
                "type" : "double"
              },
              "forward" : {
                "type" : "float"
              },
              "solarRadiation" : {
                "type" : "float"
              },
              "picture" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "A_phase_volt" : {
                "type" : "float"
              },
              "outflow" : {
                "type" : "float"
              },
              "cod" : {
                "type" : "float"
              },
              "waterLevel" : {
                "type" : "float"
              },
              "weightK" : {
                "type" : "float"
              },
              "total_phase_current" : {
                "type" : "float"
              },
              "vibration" : {
                "type" : "float"
              },
              "ibstr" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "totalphosp" : {
                "type" : "float"
              },
              "alarm_status" : {
                "type" : "float"
              },
              "total_power_factor" : {
                "type" : "float"
              },
              "lat" : {
                "type" : "float"
              },
              "longitude" : {
                "type" : "float"
              },
              "mileage" : {
                "type" : "float"
              },
              "humanidcard" : {
                "type" : "float"
              },
              "strain" : {
                "type" : "float"
              },
              "licence" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "liftheight" : {
                "type" : "float"
              },
              "o3" : {
                "type" : "float"
              },
              "coordinate_x" : {
                "type" : "float"
              },
              "level" : {
                "type" : "float"
              },
              "coordinate_y" : {
                "type" : "float"
              },
              "coordinate_z" : {
                "type" : "float"
              },
              "runtime" : {
                "type" : "float"
              },
              "manufactor" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "pressure" : {
                "type" : "float"
              },
              "controlState" : {
                "type" : "float"
              },
              "pEnergy" : {
                "type" : "float"
              },
              "expansion" : {
                "type" : "float"
              },
              "BTemperature" : {
                "type" : "float"
              },
              "license" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "alarmStatus" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "termStatu" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "NH3" : {
                "type" : "float"
              },
              "nh3" : {
                "type" : "float"
              },
              "proversion" : {
                "type" : "float"
              },
              "B_power_factor" : {
                "type" : "float"
              },
              "stateCode" : {
                "type" : "float"
              },
              "female_out" : {
                "type" : "float"
              },
              "elevationAngle" : {
                "type" : "float"
              },
              "adValue" : {
                "type" : "float"
              },
              "isOnline" : {
                "type" : "float"
              },
              "tsp" : {
                "type" : "float"
              },
              "total" : {
                "type" : "float"
              },
              "PM2" : {
                "properties" : {
                  "5" : {
                    "type" : "float"
                  }
                }
              },
              "dumpnumber" : {
                "type" : "float"
              },
              "C_active_power" : {
                "type" : "float"
              },
              "alarm" : {
                "type" : "float"
              },
              "flowRate" : {
                "type" : "float"
              },
              "CertificateNumber" : {
                "type" : "float"
              },
              "useTime" : {
                "type" : "float"
              },
              "Height" : {
                "type" : "float"
              },
              "floor" : {
                "type" : "float"
              },
              "lang" : {
                "type" : "float"
              },
              "lane" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              },
              "direction" : {
                "type" : "float"
              },
              "on" : {
                "type" : "float"
              },
              "rainfall" : {
                "type" : "float"
              },
              "temp" : {
                "type" : "float"
              },
              "OU" : {
                "type" : "float"
              },
              "obliquity" : {
                "type" : "float"
              },
              "concentration" : {
                "type" : "float"
              },
              "winddir" : {
                "type" : "float"
              },
              "totalnitrogen" : {
                "type" : "float"
              },
              "Cidentitynumber" : {
                "type" : "float"
              },
              "UserId" : {
                "type" : "float"
              },
              "ph" : {
                "type" : "float"
              },
              "online" : {
                "type" : "float"
              },
              "StrUserName" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "ignore_above" : 256,
                    "type" : "keyword"
                  }
                }
              }
            }
          },
          "iota_device" : {
            "type" : "keyword"
          },
          "sensor" : {
            "type" : "integer"
          },
          "collect_time" : {
            "format" : "date_time || uuuu-MM-dd HH:mm:ss",
            "type" : "date"
          },
          "batchid" : {
            "type" : "keyword"
          },
          "factor" : {
            "type" : "integer"
          },
          "factor_proto_code" : {
            "type" : "keyword"
          },
          "structure" : {
            "type" : "long"
          },
          "sensor_name" : {
            "norms" : false,
            "type" : "text",
            "fields" : {
              "keyword" : {
                "ignore_above" : 50,
                "type" : "keyword"
              }
            }
          }
        }
      
    },
    "aliases" : {
      "anxincloud_themes" : { }
    }
  }
```

