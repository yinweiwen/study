## 概述

为开源产品，构建在Kubernetes之上，并将本机容器化应用程序编排和设备管理扩展到边缘主机。

![KubeEdge Architecture](imgs/KubeEdge/kubeedge_arch.png)

优势：

+ 原生支持kubernetes
+ 云边缘可靠协作
+ 边缘自治
+ 边缘设备管理：通过CRD实现的Kubernetes本地api管理边缘设备。
+ 极轻量边缘代理 （EdgeCore）

组件：

+ Edged  运行在边缘节点的代理，管理容器化应用程序
+ EdgeHub 管理和云服务交互的web socket客户端。包括同步云端资源到边缘，上报边端主机和设备状态变更到云。
+ CloudHub 在云端运行的websocket服务，监听边端的事件
+ EdgeController： Kubernetes Controller的扩展，管理边缘node和pod元数据
+ EventBus： Mqtt的客户端用于连接Mqtt服务(mosquitto)，提供到其他组件的订阅和发布。
+ DeviceTwin：存储设备状态、同步设备状态到云端。并提供应用的查询接口
+ MetaManager：Edged和edgehub之间的消息处理器，它还负责在轻量级数据库（SQLite）中存储/检索元数据。

- [ServiceBus](https://kubeedge.io/en/docs/architecture/edge/servicebus): a HTTP client to interact with HTTP servers (REST), offering HTTP client capabilities to components of cloud to reach HTTP servers running at edge.



<img src="imgs/KubeEdge/avatar_hu6dd63758e9c8a7a1da773a2d4193fd13_641557_250x250_fill_box_center_2.png" height=50 align="left">

官网：https://kubeedge.io/en/docs/setup/local/

<img src="imgs/KubeEdge/image-20210709140821500.png" height=50 align="left">

Github: https://github.com/kubeedge/kubeedge



| 术语 | 描述 |
| ---- | ---- |
|      |      |

## 安装

[下载](https://github.com/kubeedge/kubeedge/releases)

+ [keadm-v1.7.1-linux-amd64.tar.gz](F:\H\edge) keadm安装文件
+ [kubeedge-v1.7.1-linux-amd64.tar.gz](F:\H\edge)  二进制文件安装
+ [edgesite-v1.7.1-linux-amd64.tar](F:\H\edge)



### [通过Keadm来安装](https://kubeedge.io/en/docs/setup/keadm/)

keadm 用来安装 KubeEdge 的云和端组件。

#### 云端

需要提前安装kubernetes。

```sh
keadm init --advertise-address="10.8.30.38"
```

将在服务器上安装 cloudcore，生成证书并安装CRDs。

获取token，将在添加边缘节点时使用。

```sh
 keadm gettoken
```



#### 边端

```sh
keadm join --cloudcore-ipport=10.8.30.38:10000 --token=xxxx
```

将在客户端安装edgecore和mqtt。



### [通过二进制文件安装](https://kubeedge.io/en/docs/setup/local/)

或者参考https://www.cnblogs.com/kkbill/p/12600541.html

安装环境**准备**：

`Cloud:10.8.30.38`：kubernetes

`Edge:10.8.30.35` ： golang docker mqtt 

| 系统   | ubuntu 16.04         |
| :----- | -------------------- |
| golang | go1.10.4 linux/amd64 |
| docker | 19.03.12             |
| k8s    | 1.18                 |



准备安装KubeEdnge1.7.1：

将安装文件 `[kubeedge-v1.7.1-linux-amd64.tar.gz] 拷贝到服务器和边缘节点

#### 云端

创建 CRDs

```shell
kubectl apply -f https://raw.githubusercontent.com/kubeedge/kubeedge/master/build/crds/devices/devices_v1alpha2_device.yaml
kubectl apply -f https://raw.githubusercontent.com/kubeedge/kubeedge/master/build/crds/devices/devices_v1alpha2_devicemodel.yaml
kubectl apply -f https://raw.githubusercontent.com/kubeedge/kubeedge/master/build/crds/reliablesyncs/cluster_objectsync_v1alpha1.yaml
kubectl apply -f https://raw.githubusercontent.com/kubeedge/kubeedge/master/build/crds/reliablesyncs/objectsync_v1alpha1.yaml
```

创建config文件

```sh
cloudcore --minconfig > cloudcore.yaml
```

[配置](https://kubeedge.io/en/docs/setup/config/#configuration-cloud-side-kubeedge-master)

```yaml
apiVersion: cloudcore.config.kubeedge.io/v1alpha1
kind: CloudCore
kubeAPIConfig:
  kubeConfig: /root/.kube/config
  master: ""
modules:
  cloudHub:
    advertiseAddress:
    - 10.8.30.38
    enable: true
    https:
      address: 0.0.0.0
      enable: true
      port: 10002
    nodeLimit: 1000
    tlsCAFile: /etc/kubeedge/ca/rootCA.crt
    tlsCAKeyFile: /etc/kubeedge/ca/rootCA.key
    tlsCertFile: /etc/kubeedge/certs/edge.crt
    tlsPrivateKeyFile: /etc/kubeedge/certs/edge.key
    unixsocket:
      address: unix:///var/lib/kubeedge/kubeedge.sock
      enable: true
    websocket:
      address: 0.0.0.0
      enable: true
      port: 10000
  router:
    address: 0.0.0.0
    enable: true
    port: 9443
    restTimeout: 60

```

运行

```sh
cloudcore --config cloudcore.yaml
```

生成证书

```sh
 wget https://raw.githubusercontent.com/kubeedge/kubeedge/master/build/tools/certgen.sh
chmod +x certgen.sh
./certgen.sh genCertAndKey edge
 
root@node38:/home/anxin/edge# ls /etc/kubeedge
ca  certs  crds

拷贝到边缘节点：
 scp /etc/kubeedge/ca/rootCA.crt root@node35:/etc/kubeedge/ca/
 scp /etc/kubeedge/certs/edge.crt root@node35:/etc/kubeedge/certs/
 scp /etc/kubeedge/certs/edge.key root@node35:/etc/kubeedge/certs/
```



获取token （将用于edge配置）

```sh
kubectl get secret -nkubeedge tokensecret -o json # 找到token 加入到edgecore.yml中
 
# kubectl get secret -nkubeedge default-token-pdrnq -o=jsonpath='{.data.tokendata}' | base64 -d
# sed -i -e "s|token: .*|token: ${token}|g" edgecore.yaml
```





#### 边端

>  需要提前安装docker



初始化配置

```
./edgecore --minconfig > edgecore.yaml
```



将云端token配置到edgecore.yaml

```yaml
# With --minconfig , you can easily used this configurations as reference.
# It's useful to users who are new to KubeEdge, and you can modify/create your own configs accordingly. 
# This configuration is suitable for beginners.

apiVersion: edgecore.config.kubeedge.io/v1alpha1
database:
  dataSource: /var/lib/kubeedge/edgecore.db
kind: EdgeCore
modules:
  edgeHub:
    enable: true
    heartbeat: 15
    httpServer: https://10.8.30.38:10002
    tlsCaFile: /etc/kubeedge/ca/rootCA.crt
    tlsCertFile: /etc/kubeedge/certs/server.crt
    tlsPrivateKeyFile: /etc/kubeedge/certs/server.key
    token: 5e630e7bf11b00a77a513233e067788e12d36179be1d4b83d804bf77c463781e.eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE2MjYyNDE2ODd9.oRE1mzeRVC7ohsA5KF9QP_EvxE83BSTjP6fuZuVL2Yk
    websocket:
      enable: true
      handshakeTimeout: 30
      readDeadline: 15
      server: 10.8.30.38:10000
      writeDeadline: 15
  edged:
    #cgroupDriver: cgroupfs 通过docker info查看本机安装的cgroup-driver
    cgroupDriver: systemd
    cgroupRoot: ""
    cgroupsPerQOS: true
    clusterDNS: ""
    clusterDomain: ""
    devicePluginEnabled: false
    dockerAddress: unix:///var/run/docker.sock
    enable: true
    registerNode: true
    gpuPluginEnabled: false
    hostnameOverride: node35
    nodeIP: 10.8.30.35
    podSandboxImage: kubeedge/pause:3.1
    remoteImageEndpoint: unix:///var/run/dockershim.sock
    remoteRuntimeEndpoint: unix:///var/run/dockershim.sock
    runtimeType: docker
  eventBus:
    enable: true
    mqttMode: 2
    mqttQOS: 0
    mqttRetain: false
    mqttServerExternal: tcp://127.0.0.1:1883
    mqttServerInternal: tcp://127.0.0.1:1884
```



启动

```shell
edgecore --config edgecore.yaml
```

启动后 docker ps:

![image-20210713140207317](imgs/KubeEdge/image-20210713140207317.png)



##### 常见报错

1. Error: token credentials are in the wrong format

   ```sh
   配置的token格式不对，从cloud端拿到的是base64，需要取解密后字符串、
   ```

2.  cgroup-driver

   需要通过docker info命令查看自己安装的docker的cgroup-driver，然后修改配置文件中的cgroup-driver

3. 无法访问平台侧的证书文件
   F0709 14:54:31.063677    3153 certmanager.go:92] Error: failed to get edge certificate from the cloudcore, error: Get "https://10.8.30.38:10002/edge.crt": x509: cannot validate certificate for 10.8.30.38 because it doesn't contain any IP SANs

```shell
I0709 15:27:42.098408   14300 server.go:243] Ca and CaKey don't exist in local directory, and will read from the secret
I0709 15:27:42.103401   14300 server.go:288] CloudCoreCert and key don't exist in local directory, and will read from the secret
```

**解决方法**：

删除原来的配置

```shell
kubectl delete secret casecret -nkubeedge
kubectl delete secret cloudcoresecret -nkubeedge
# both cloud and edge side
mv /etc/kubeedge/ca  /etc/kubeedge/ca.bak 
mv  /etc/kubeedge/certs /etc/kubeedge/certs.bak
```



执行： ./certgen.sh genCertAndKey edge

新问题：

`Can't load /root/.rnd`  (第一次生成的时候可能就错在这里)

```shell
cd /root

openssl rand -writerand .rnd
```

