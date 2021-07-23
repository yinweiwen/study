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
./edgecore --defaultconfig > edgecore.yaml
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



在cloud端查看

```sh
root@node38:/home/anxin# kubectl get no 
NAME     STATUS   ROLES        AGE    VERSION
node35   Ready    agent,edge   334d   v1.19.3-kubeedge-v1.7.1
node37   Ready    <none>       334d   v1.18.6
node38   Ready    master       334d   v1.18.6
```





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



### 在Raspberry Pi 上安装

#### 安装Lite系统

https://downloads.raspberrypi.org/raspios_lite_armhf/images/raspios_lite_armhf-2021-05-28/2021-05-07-raspios-buster-armhf-lite.zip

设置网络

```shell
interface eth0 
static ip_address=10.8.30.197/24 
static routers=10.8.30.1 
static domain_name_servers=114.114.114.114 223.5.5.5 223.6.6.6 

#设置密码
passwd pi

PasswordAuthentication yes

systemctl enable ssh
```

安装mosquitto

```shell
wget http://repo.mosquitto.org/debian/mosquitto-repo.gpg.key

sudo apt-key add mosquitto-repo.gpg.key

# sudo wget -P /etc/apt/sources.list.d/ http://repo.mosquitto.org/debian/mosquitto-jessie.list
# sudo wget -P /etc/apt/sources.list.d/ http://repo.mosquitto.org/debian/mosquitto-stretch.list
sudo wget -P /etc/apt/sources.list.d/ http://repo.mosquitto.org/debian/mosquitto-buster.list 

apt-get update

apt-get install mosquitto

systemctl status mosquitto
```



#### [安装docker](https://peppe8o.com/setup-a-docker-environment-with-raspberry-pi-os-lite-and-portainer/)

<img src="imgs/KubeEdge/raspberry-pi-docker-portainer-featured-image.jpg" width="100" align="left"/>

```shell
sudo apt-get update && sudo apt-get upgrade

curl -fsSL https://get.docker.com -o get-docker.sh

sudo sh get-docker.sh

sudo docker version

sudo docker run hello-world

sudo usermod -aG docker pi

```





#### [安装containerd](https://docs.docker.com/engine/install/debian/)

[Setting different container runtime with CRI](https://docs.kubeedge.io/en/docs/advanced/cri/)



修改`/etc/apt/sources.list`

```shell
deb http://mirrors.163.com/raspbian/raspbian/ buster main contrib non-free rpi
# Uncomment line below then 'apt-get update' to enable 'apt-get source'
#deb-src http://raspbian.raspberrypi.org/raspbian/ buster main contrib non-free rpi
```

安装

```shell
 sudo apt-get update
 sudo apt-get install \
    apt-transport-https \
    ca-certificates \
    curl \
    gnupg \
    lsb-release
    
curl -fsSL https://download.docker.com/linux/debian/gpg | sudo gpg --dearmor -o /usr/share/keyrings/docker-archive-keyring.gpg

 echo \
  "deb [arch=armhf signed-by=/usr/share/keyrings/docker-archive-keyring.gpg] https://download.docker.com/linux/debian \
  $(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
  
 sudo apt-get update
 sudo apt-get install containerd.io
```



```shell
# Configure containerd
mkdir -p /etc/containerd
containerd config default > /etc/containerd/config.toml

# Restart containerd
systemctl restart containerd
```



安装golang

```shell
sudo apt update
sudo apt full-upgrade

wget https://dl.google.com/go/go1.16.6.linux-armv6l.tar.gz -O go.tar.gz

```





#### 配置运行EdgeCore

配置EdgeCore.yaml支持containerd.。 默认cgroup 配置是 cgroupfs。

```yaml
remoteRuntimeEndpoint: unix:///var/run/containerd/containerd.sock
remoteImageEndpoint: unix:///var/run/containerd/containerd.sock
runtimeRequestTimeout: 2
podSandboxImage: k8s.gcr.io/pause:3.2
runtimeType: remote
```

配置

```yaml

```

启动：

![image-20210721161810471](imgs/KubeEdge/image-20210721161810471.png)

问题

1. Cgroup subsystem not mounted:[memory]

   ```shell
   Following Cgroup subsystem not mounted: [memory]
   ```

   https://zhuanlan.zhihu.com/p/384443481

   ```shell
   #解决问题
   #修改/boot/cmdline.txt
   sudo vim /boot/cmdline.txt
   cgroup_enable=memory cgroup_memory=1
   添加在同一行的最后面,接着内容后空格后添加, 注意:不要换行添加
   
   #重启机器配置生效
   reboot
   ```

2. 



## 配置

### 云端

设置边缘端自动注册 modules.edged.registerNode=true。 或者使用[手动注册](https://kubeedge.io/en/docs/setup/config/)。



### 边端

#### 设置基础镜像：

```sh
modules.edged.podSandboxImage
```

检查机器架构：

```shell
getconf LONG_BIT
```

+ `kubeedge/pause-arm:3.1` **for** arm arch
+ `kubeedge/pause-arm64:3.1` **for** arm64 arch 
+ `kubeedge/pause:3.1` **for** x86 arch

#### 容器运行时

```yaml
runtimeType: docker
```

or

```yaml
runtimeType: remote
```

#### MQTT模式

MQTT用于DeviceTwin和Devices之间的通信，支持3中MQTT模式：

0 internalMqttMode 内部MQTT代理使能

1 bothMqttMonde 内部和外部MQTT代理均使能

2 externalMqttMode 外部代理使能





## EdgeMesh

云端和边缘之间发生网络问题，集成EdgeMesh 支持DNS访问

```shell
grep hosts /etc/nsswitch.conf
hosts:          dns file mdns4_minimal # 确保dns在第一位
```

**IP Forward**设置

```shell
$ sudo echo "net.ipv4.ip_forward = 1" >> /etc/sysctl.conf
$ sudo sysctl -p
#check
$ sudo sysctl -p | grep ip_forward
net.ipv4.ip_forward = 1
```

部署一个nginx例子

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: nginx-deployment
  labels:
    app: nginx
spec:
  replicas: 1
  selector:
    matchLabels:
      app: nginx
  template:
    metadata:
      labels:
        app: nginx
    spec:
      containers:
      - name: nginx
        image: nginx
        ports:
        - containerPort: 80
          hostPort: 8050
```

`kubeclt apply -f pod-nginx.yaml`

![image-20210713171944426](imgs/KubeEdge/image-20210713171944426.png)

按照官网例子访问容器ip curl 10.0.35.3:8050，无法访问，直接访问http://10.8.30.35:8050 可以。

创建一个服务：

```yaml
apiVersion: v1
kind: Service
metadata:
  name: nginx-svc
  namespace: default
spec:
  clusterIP: None
  selector:
    app: nginx
  ports:
    - name: http-0
      port: 18050
      protocol: TCP
      targetPort: 80
```

![image-20210713174342754](imgs/KubeEdge/image-20210713174342754.png)

在边缘端访问： `<service_name>.<service_namespace>.svc.<cluster>.<local>:<port>`

curl http://nginx-svc.default.svc.cluster.local:18050



## 开发指南

### Device Model 设备原型

KubeEdge支持通过Kubernetes CRDS，以及Device Mapper进行设备管理

```yaml
apiVersion: devices.kubeedge.io/v1alpha2
kind: DeviceModel
metadata:
 name: sensor-tag-model
 namespace: default
spec:
 properties:
  - name: temperature
    description: temperature in degree celsius
    type:
     int:
      accessMode: ReadWrite
      maximum: 100
      unit: degree celsius
  - name: temperature-enable
    description: enable data collection of temperature sensor
    type:
      string:
        accessMode: ReadWrite
        defaultValue: 'OFF'
```

### Device Instance

```yaml
apiVersion: devices.kubeedge.io/v1alpha2
kind: Device
metadata:
  name: sensor-tag-instance-01
  labels:
    description: TISimplelinkSensorTag
    manufacturer: TexasInstruments
    model: CC2650
spec:
  deviceModelRef:
    name: sensor-tag-model
  protocol:
    modbus:
      slaveID: 1
    common:
      com:
        serialPort: '1'
        baudRate: 115200
        dataBits: 8
        parity: even
        stopBits: 1
      customizedValues: # 自定义属性值
        def1: def1-val
        def2: def2-val
  nodeSelector:
    nodeSelectorTerms:
    - matchExpressions:
      - key: ''
        operator: In
        values:
        - node1
  propertyVisitors:
    - propertyName: temperature
      modbus:
        register: CoilRegister
        offset: 2
        limit: 1
        scale: 1
        isSwap: true
        isRegisterSwap: true
    - propertyName: temperature-enable
      modbus:
        register: DiscreteInputRegister
        offset: 3
        limit: 1
        scale: 1.0
        isSwap: true
        isRegisterSwap: true
    - propertyName: temperature # 自定义协议写法
      collectCycle: 500000000
      reportCycle: 1000000000
      customizedProtocol:
        protocolName: MY-TEST-PROTOCOL
        configData:
          def1: def1-val
          def2: def2-val
          def3:
            innerDef1: idef-val
status:
  twins:
    - propertyName: temperature
      reported:
        metadata:
          timestamp: '1550049403598'
          type: int
        value: '10'
      desired:
        metadata:
          timestamp: '1550049403598'
          type: int
        value: '15'
data: # 定义通过mappers上报到边缘端MQTT代理的一系列时序属性
  dataTopic: "$ke/events/device/+/data/update"
  dataProperties: 
    - propertyName: pressure
      metadata:
        type: int
    - propertyName: temperature
      metadata:
        type: int
```

### Device Mapper

是连接和控制设备的应用程序，包含功能：

1) 扫描和连接设备

2) 报告设备孪生属性（ twin-attributes）的真实状态

3) 转换预期状态到真实状态

4) 采集遥感数据

5) 将设备的读取转换成KubeEdge可以接受的格式

6) 安排设备上的动作(schedule)

7) 检查设备的健康状态

![img](imgs/KubeEdge/mapper-design.png)

### 创建步骤

1. kubectl apply -f <path to device model yaml>
2. kubectl apply -f <path to device instance yaml>
3. 执行基于指定协议的mapper程序
4. 改变设备实例yaml中的状态段,应用改变将影响边缘端，通过设备控制和设备孪生组件（DeviceController和DeviceTwin）
5. 边缘端的mapper进程上报设备孪生的数据，并通过DeviceController同步回云端。



### Router Manager 路由管理

通过CRDs和Router模块，基于mqtt，实现云边消息通信。 控制用户数据的传输，一次传输的大小不超过12M。

使用场景：

+ 通过云端api发布自定义消息，最终到达终端mqtt代理
+ 边缘发布消息到mqtt代理，最终通过RESTApi返回云端
+ 通过云端api发布自定义消息，最终调用边缘rest api传送消息

#### 规则的定义

RuleEndpoint 和 Rule Definition:

+ RuleEndpoint 定义消息从哪里来的、或到哪里去，包括3中类型
  + rest
  + eventbus (MQTT)
  + servicebus (边缘端的Rest Api应用)
+ Rule 定义从源Endpoint到目标Endpoint消息传输的方式，包含3中类型：
  + rest->eventbus :  cloud api --> edge mqtt
  + eventbus->rest: edge mqtt --> cloud api
  + rest->servicebus: cloud api --> user's app

RuleEndpoint定义

```yaml
apiVersion: apiextensions.k8s.io/v1
kind: CustomResourceDefinition
metadata:
  name: ruleendpoints.rules.kubeedge.io
spec:
  group: rules.kubeedge.io
  versions:
    - name: v1
      served: true
      storage: true
      schema:
        openAPIV3Schema:
          type: object
          properties:
            spec:
              type: object
              properties:
                ruleEndpointType:
                  type: rest/eventbus/servicebus
              required:
                - ruleEndpointType
  scope: Namespaced
  names:
    plural: ruleendpoints
    singular: ruleendpoint
    kind: RuleEndpoint
    shortNames:
      - re
```

Rule定义：

```yaml
apiVersion: apiextensions.k8s.io/v1
kind: CustomResourceDefinition
metadata:
  name: rules.rules.kubeedge.io
spec:
  group: rules.kubeedge.io
  versions:
    - name: v1
      served: true
      storage: true
      schema:
        openAPIV3Schema:
          type: object
          properties:
            spec:
              type: object
              properties:
                source:
                  type: string
                  value: my-rest
                sourceResource:
                  description: {"path":"/a/b"} 或 {"topic":"<user define string>","node_name":"edge-node"}
                  type: object
                  additionalProperties:
                    type: string
                target:
                  type: string
                  value: my-eventbus
                targetResource:
                  description: {"topic":"/test"}
                  type: object
                  additionalProperties:
                    type: string
              required:
                - source
                - sourceResource
                - target
                - targetResource
            status:
              type: object
              properties:
                successMessages:
                  type: integer
                failMessages:
                  type: integer
                errors:
                  items:
                    type: string
                  type: array
  scope: Namespaced
  names:
    plural: rules
    singular: rule
    kind: Rule
```



### [发消息的步骤](https://docs.kubeedge.io/en/docs/developer/custom_message_deliver/)

以rest->eventbus 模式举例

1. 云端配置 router模块使能

2. 创建ruleEndpoint

3. 创建rule

   ```yaml
   apiVersion: rules.kubeedge.io/v1
   kind: Rule
   metadata:
     name: my-rule
     labels:
       description: test
   spec:
     source: "my-rest"
     sourceResource: {"path":"/test"}
     target: "my-eventbus"
     targetResource: {"topic":"test"}
   ```

4. 云端调用api接口发送消息

   ```shell
   POST
   http://{cloudcore_ip}:9443/{node_name}/{namespace}/{path}
   ```

   

5. 边缘订阅

   ```shell
   mosquitto_sub -t 'test' -d
   ```



MORE：

人工智能、数字孪生、边缘计算、量子计算、沉浸式技术、区块链、智能空间、5G