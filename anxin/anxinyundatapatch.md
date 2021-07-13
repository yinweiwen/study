# 服务约定

服务端口：18010

地址：/data-patch/websocket

## 请求

``` json
{
    "src": "iota", // 固定，从iota获取原始数据补数据
    "token": "123456", // 密钥，启动参数token控制
    "topic": "savoir_data", // et消费的kafka topic, 不给默认是anxinyun_data
    "thingId": "9d9d14b3-cf77-4cba-934b-faeaf2d153ba", // thingid
    "devices": ["0fc608c2-5d7c-45a0-a5f7-c423a08fa0d0","e2d26fce-7362-4036-925b-5125453b2d4e","f586ebba-5f93-4634-9e29-f329183cf61e","67bbd235-7357-41b7-8e37-ff9ccf6cf9eb","b34299aa-625e-4404-a580-fde1d7cbc3e8","bfefe2f7-ff9c-40dc-be66-a3c6e0032017","08d2cb15-33c0-42bc-9ff8-523bdb157afd"], // 设备id
    "from": "2019-01-11T21:03:00.000+0800", // 起始时间 支持iso或者utc或者yyyy-MM-DD hh:mm:ss格式
    "to": "2019-01-15T14:30:00.000+0800" // 结束时间 支持iso或者utc或者yyyy-MM-DD hh:mm:ss格式
}
```
json
{
	"src": "iota",
	"token": "123456",
	"topic": "savoir_data",
	"thingId": "7dea63f9-d15f-4ef2-8d70-d8e256d637a0",
	"devices": ["6ba6c6a1-f96d-4e33-bc0c-4b40411f3a88",
		"f63f4033-01f4-4de1-ac1d-661164ecbf70",
		"46449009-b7c6-4265-b07a-4e033be83624",
		"9abdb3de-2ce3-49eb-8f6f-c827e9e89d72",
		"7b9aa5e4-a3cd-418b-9219-1302ffee2750",
		"6b009f76-b356-4365-8c1c-b2e4608a71ae",
		"46a95110-ec3f-4f13-9a21-6ef4bb83dcc4",
		"76cdbad9-15e4-488e-8c81-3c8fda22d01e",
		"2cf24193-4111-43c1-b57e-a22b80fe89b6",
		"ccbb2ec8-944c-456c-84af-41cc5417d17a",
		"b5c7cc36-1a86-4a37-836c-e5b00cc892b3"
	],
	"from": "2019-01-11T21:03:00.000+0800",
	"to": "2019-01-21T16:30:00.000+0800"
}
## 响应
请求成功的响应：

```json
{
    "type": "data-patch",
    "erorr": null,
    "res": "ok",
    "payload": {
        "taskId": "xxxxxxxxxxxxuuid",
        "msg": "task created."
    }
}
```

错误信息：

```json
{
    "type": "theme-data",
    "erorr": "error message:xxxxx",
    "res": "error",    
    "payload": {
        "ex": "异常信息"
    }
}
```

处理日志：

```json
{
    "type": "theme-data",
    "erorr": null,
    "res": "ok",
    "payload": {
        "msg": "device: b34299aa-625e-4404-a580-fde1d7cbc3e8 - 静力水准仪06 has 178 rows to publish"
    }
}
```

```json
{
    "type": "theme-data",
    "erorr": null,
    "res": "ok",
    "payload": {
        "msg": "iota data published(178 / 178). device: b34299aa-625e-4404-a580-fde1d7cbc3e8"
    }
}
```