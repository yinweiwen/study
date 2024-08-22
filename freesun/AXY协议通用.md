## HTTP协议

URL地址： https://datahub.anxinyun.cn:60000?id=设备唯一ID

方法：POST

内容JSON格式：

```json
{
    "timestamp":1404404000, // 时间戳(ms)
    "data":{  // 数据 （键值对）
        "k":0.2,
        "dis":0.3
    }
    "code":0, // 错误码（如有）
}
```





### MQTT协议

URL地址： tcp://datahub.anxinyun.cn:1883

内容JSON格式：(同上)

