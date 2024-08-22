## Docker部署EMIS-API时出现的问题

1. 出现kafka host无法访问

   怀疑docker中dns问题，将宿主机hosts文件映射进去

2. 出现异常请求127.0.0.1:80

   怀疑配置未生效，修改环境变量文件中，去掉所有单引号。

最终执行的语句

```
docker run --rm  -p 9230:8080 -v /etc/hosts:/etc/hosts --name emisapitest --env-file cm-emistestapi.list registry.ngaiot.com/pep/emis-api:91
```



环境变量文件：

```list
ANXINCLOUD_QINIU_ACCESSKEY=5XrM4wEB9YU6RQwT64sPzzE6cYFKZgssdP5Kj3uu
ANXINCLOUD_QINIU_BUCKET_RESOURCE=pep-resource
ANXINCLOUD_QINIU_DOMAIN_QNDMN_RESOURCE=http://rg2gmjtg7.hd-bkt.clouddn.com
ANXINCLOUD_QINIU_SECRETKEY=
CAMUNDA_HOST=http://10.244.100.194:19081
CAMUNDA_ROOT=fs-workflow
EXPIRATION=1440
FS_EMIS_WEB=https://pepca-demo.anxinyun.cn
FS_EMIS_WEB_URL=https://pepca-demo.anxinyun.cn
FS_PEP_DOMAIN=.anxinyun.cn
FS_PEP_WEB_URL=https://pep-demo.anxinyun.cn/signin
FS_PM_API=http://10.244.45.125:9998
FS_PM_API_URL=https://peppm-demo.anxinyun.cn/_api
FS_UNIAPP_FC_LOCAL_SVR_ORIGIN=http://10.8.40.101:9230
FS_UNIAPP_DB=postgres://FashionAdmin:Fas123_@10.8.40.210:5432/pepca-test
IOTA_REDIS_SERVER_HOST=10.244.160.244
IOTA_REDIS_SERVER_PORT=6378
IOTA_REDIS_SERVER_PWD=''
MAXLOGNUM=999999
NODE_ENV=product
PEP_KAFKA_BROKERS=anxinyun-n1:6667
PEP_KAFKA_CONSUMER_BROKERS=anxinyun-n1:6667
PEP_KAFKA_CONSUMER_GROUPID=emis-camunda-group-test1
PEP_KAFKA_CONSUMER_TOPIC=camundaKafkatest
PEP_SOURCE_QINIU_ACCESSKEY=
PEP_SOURCE_QINIU_BUCKET_RESOURCE=pep-resource
PEP_SOURCE_QINIU_DOMAIN_QNDMN_RESOURCE=http://rg2gmjtg7.hd-bkt.clouddn.com
PEP_SOURCE_QINIU_SECRETKEY=
WXCHAT_ADDRESS_CORPSECRET=
WXCHAT_AGENTID=1000012
WXCHAT_CORPID=
WXCHAT_CORPSECRET=
WXCHAT_REDIRECT_URL=https://open.weixin.qq.com/connect/oauth2/authorize?appid={corpid}&redirect_uri={redirectUrl}&response_type=code&scope=snsapi_base&state=STATE#wechat_redirect
WXCHAT_REQURL=https://qyapi.weixin.qq.com/cgi-bin
NODE_OPTIONS=--max-old-space-size=4096
```



需要从k8s的web中请求该接口，需要创建一个指向固定IP的service。这里需要手动创建EndPoints：（参考ops命名空间下的clickhouse处理）

```yaml
apiVersion: v1
kind: Endpoints
metadata:
  name: emis-test-service
  namespace: smart-xxx
subsets:
- addresses:
  - ip: 10.8.40.210
  ports:
  - name: web
    port: 9230
    protocol: TCP


---
apiVersion: v1
kind: Service
metadata:
  name: emis-test-service
  namespace: smart-xxx
spec:
  ports:
  - name: web
    port: 9230
    protocol: TCP
    targetPort: 9230
  sessionAffinity: None
  type: ClusterIP
```

web-demo的访问请求的路由文件：

```yaml
kind: Ingress
apiVersion: extensions/v1beta1
metadata:
  name: emis-api-test
  namespace: smart-xxx
spec:
  tls:
    - hosts:
        - pep-demo.anxinyun.cn
        - pepca-demo.anxinyun.cn
      secretName: smart-xxx-root-secret
  rules:
    - host: pepca-demo.anxinyun.cn
      http:
        paths:
          - path: /_api(/|$)(.*)
            pathType: Prefix
            backend:
              serviceName: emis-test-service
              servicePort: 9230
```

