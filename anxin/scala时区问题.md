JSON中的日期格式：

```json
{"sensorId":22063,"structId":2385,"factorId":57,"factor_proto":"1005","collect_time":"2022-11-08 14:10:00.001","data":{"waterLevel":0.011401970356702802}}
```

解析完的数据

```sh
[upload] [structId:2385] [station:22063] [2022-11-08T14:10:00.001Z]
```

并没有按照东八区的时间解析正确。

解析部分代码：

```scala
private class DatetimeDeserializer extends JsonDeserializer[DateTime] {
        override def deserialize(jsonParser: JsonParser, deserializationContext: DeserializationContext): DateTime = {
            val node = jsonParser.getCodec.readTree[JsonNode](jsonParser)
            val l = node.asLong(-1L)
            if (l == -1L) {
                var str = node.asText()
                DateParser.parse(str).getOrElse(new DateTime())
            } else new DateTime(l)
        }
    }
```

```scala
val patterns = List(
        "yyyy-MM-dd'T'HH:mm:ssZ",
        "yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSSZ",
        "yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSZ",
        "yyyy-MM-dd'T'HH:mm:ss.SSSSSSSZ",
        "yyyy-MM-dd'T'HH:mm:ss.SSSSSSZ",
        "yyyy-MM-dd'T'HH:mm:ss.SSSSSZ",
        "yyyy-MM-dd'T'HH:mm:ss.SSSSZ",
        "yyyy-MM-dd'T'HH:mm:ss.SSSZ",
        "yyyy-MM-dd'T'HH:mm:ss.SSZ",
        "yyyy-MM-dd'T'HH:mm:ss.SZ",
        "yyyy/MM/dd HH:mm:ss",
        "yyyy-MM-dd HH:mm:ss",
        "yyyy/MM/dd HH:mm",
        "yyyy-MM-dd HH:mm",
        "yyyy/MM/dd HH:mm:ss.SSS",
        "yyyy-MM-dd HH:mm:ss.SSS",
        "yyyy/MM/dd HH:mm:ss.S",
        "yyyy-MM-dd HH:mm:ss.S",
        "yyyy-MM-dd",
        "yyyy/MM/dd"
    )

    val parsers: Array[DateTimeParser] = patterns.map(pat => DateTimeFormat.forPattern(pat).getParser).toArray
    val formatter: DateTimeFormatter = new DateTimeFormatterBuilder().append(null, parsers).toFormatter

    def parse(str: String): Option[DateTime] = {
        try {
            Some(formatter.parseDateTime(str))
        } catch {
            case e: Exception =>
                None
        }
    }
```



时区设置：

参考https://blog.csdn.net/weixin_43066097/article/details/103828786

```sh
sudo dpkg-reconfigure tzdata

timedatectl set-local-rtc 1


iota@iota-n11:~/flink1.9.3/flink-1.9.3/log$ timedatectl status
                      Local time: Wed 2022-11-09 10:56:21 CST
                  Universal time: Wed 2022-11-09 02:56:21 UTC
                        RTC time: Wed 2022-11-09 02:56:21
                       Time zone: Asia/Shanghai (CST, +0800)
       System clock synchronized: no
systemd-timesyncd.service active: yes
                 RTC in local TZ: no

```



最终解决方案：

将字符串重新格式化成*RFC3339* 