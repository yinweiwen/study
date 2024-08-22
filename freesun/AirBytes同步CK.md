# AirBytes同步CK

## 通过k8s HELM安装Airbytes

https://docs.airbyte.com/deploying-airbyte/on-kubernetes-via-helm

> 测试环境 10.8.30.50 fastest/******

```sh
> 
root@master:/home/fastest# export http_proxy=http://10.8.30.183:7890/
root@master:/home/fastest# export https_proxy=http://10.8.30.183:7890/


# 安装helm
curl -fsSL -o get_helm.sh https://raw.githubusercontent.com/helm/helm/main/scripts/get-helm-3
chmod 700 get_helm.sh
./get_helm.sh

# 更新仓库
helm repo add airbyte https://airbytehq.github.io/helm-charts
helm repo update
helm search repo airbyte

# 按住
helm install %release_name% airbyte/airbyte
```

自定义参数：创建 values.yaml ，参考https://github.com/airbytehq/airbyte-platform/blob/main/charts/airbyte/values.yaml

```sh
helm install --values path/to/values.yaml %release_name% airbyte/airbyte
```



CSDN上安装办法

```sh
helm repo add airbyte https://airbytehq.github.io/helm-charts

helm upgrade --install airbyte airbyte/airbyte -n airbyte --create-namespace --set global.airbyteUrl=10.8.30.150:30080 --set webapp.service.type=NodePort --set global.logs.minio.enabled=true   ##global.airbyteUrl 的值根据实际情况修改

```

