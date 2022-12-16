Kubesphere启用告警

admin登录 > Platform > CRDs > ClusterConfiguration

![edit-yaml](https://v3-0.docs.kubesphere.io/images/docs/enable-pluggable-components/kubesphere-alerting-and-notification/edit-yaml.png)

```YAML
alerting:
    enabled: true # Change "false" to "true"
notification:
    enabled: true # Change "false" to "true"
```

检查是否启用:

```sh
kubectl logs -n kubesphere-system $(kubectl get pod -n kubesphere-system -l app=ks-installer -o jsonpath='{.items[0].metadata.name}') -f
```



设置邮件服务器 (平台设置>通知设置)

![image-20221128170447716](imgs/Kubesphere启用告警/image-20221128170447716.png)



在具体的项目下，设置告警策略：

```promql
increase(kube_pod_container_status_restarts_total{container="taskmanager",namespace="ops"}[1h]) > 0
```

![image-20221128185247129](imgs/Kubesphere启用告警/image-20221128185247129.png)

设置接收人接收策略(平台设置>通知设置)

