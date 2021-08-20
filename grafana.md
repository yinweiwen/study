run in docker

```sh
docker run -d --name=grafana -p 3000:3000 grafana/grafana
```

一般使用步骤：

+ 导出数据矩阵或日志
+ 建立Dashboard
+ 注解Dashboard
+ 建立Alert

grafana

1. Add Source
2. Add Pannel
3. Add Anotionation