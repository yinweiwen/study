@@ 
# COPYRIGHT LITTER-DRAGON

# 环境准备
---
## 主机
可以用虚拟机（VirtualBox/Ubuntu 16.04），桥接网络，规划IP地址。例如：

|hostname|ip	|  |
| ------| ----- |---|
|iota-m1|10.8.25.100|kubernetes master|
|iota-n1|10.8.25.150|node|
| ... |    |   |

## 基础准备
规划好节点名称和ip，并修改所有机器的/etc/hosts文件，添加必要的记录
例如：

```

10.8.25.100 iota-m1
10.8.25.150 iota-n1

```
---
# 安装
---

> 在所有机器上，以root用户执行以下命令

## apt支持HTTPS源

``` bash
apt-get update && apt-get install -y apt-transport-https
```

## 安装curl工具

``` bash
apt-get install curl
```
## 增加google的安装源 key
``` bash
curl -s https://packages.cloud.google.com/apt/doc/apt-key.gpg | apt-key add -
```
> 如果此方式失败，请手动获取 https://packages.cloud.google.com/apt/doc/apt-key.gpg 文件，存储在某目录，例如 /home/iota/k8s下，然后执行
> `apt-key add /home/iota/k8s/apt-key.gpg`

## 添加kubernetes的安装源镜像
> 使用的是ustc的镜像

``` bash
cat <<EOF >/etc/apt/sources.list.d/kubernetes.list
deb http://mirrors.ustc.edu.cn/kubernetes/apt kubernetes-xenial main
EOF
```
## 安装 kubelet kubeadm kubectl

``` bash
apt-get update
kubernetes-cni=0.5.1-00 kubelet=1.8.2-00 kubeadm=1.8.2-00 kubectl=1.8.2-00
```

## 获取kubernetes环境所需要的基础镜像
> 需要拉取可用的镜像，并打标签。利用的是蜂巢资源

    1 拉取所需镜像

``` bash
    docker pull hub.c.163.com/ap6108/kubernetes-dashboard-init-amd64:v1.0.1
    docker pull hub.c.163.com/juranzhijia/kubernetes-dashboard-amd64:v1.7.1
    docker pull hub.c.163.com/k8s163/etcd-amd64:3.0.17
    docker pull hub.c.163.com/zhijiansd/k8s-dns-kube-dns-amd64:1.14.5
    docker pull hub.c.163.com/zhijiansd/k8s-dns-sidecar-amd64:1.14.5
    docker pull hub.c.163.com/zhijiansd/k8s-dns-dnsmasq-nanny-amd64:1.14.5
    docker pull hub.c.163.com/conformance/kube-apiserver-amd64:v1.8.2
    docker pull hub.c.163.com/conformance/kube-controller-manager-amd64:v1.8.2
    docker pull hub.c.163.com/conformance/kube-scheduler-amd64:v1.8.2
    docker pull hub.c.163.com/conformance/kube-proxy-amd64:v1.8.2
    docker pull hub.c.163.com/conformance/pause-amd64:3.0
	
```
    2 更改标签，以便kubeadm可以拉取到
``` bash
docker tag hub.c.163.com/k8s163/etcd-amd64:3.0.17 gcr.io/google_containers/etcd-amd64:3.0.17
docker tag hub.c.163.com/conformance/kube-apiserver-amd64:v1.8.2 gcr.io/google_containers/kube-apiserver-amd64:v1.8.2
docker tag hub.c.163.com/conformance/kube-controller-manager-amd64:v1.8.2 gcr.io/google_containers/kube-controller-manager-amd64:v1.8.2
docker tag hub.c.163.com/conformance/kube-scheduler-amd64:v1.8.2 gcr.io/google_containers/kube-scheduler-amd64:v1.8.2
docker tag hub.c.163.com/conformance/pause-amd64:3.0 gcr.io/google_containers/pause-amd64:3.0
docker tag hub.c.163.com/conformance/kube-proxy-amd64:v1.8.2  gcr.io/google_containers/kube-proxy-amd64:v1.8.2
docker tag hub.c.163.com/zhijiansd/k8s-dns-kube-dns-amd64:1.14.5 gcr.io/google_containers/k8s-dns-kube-dns-amd64:1.14.5
docker tag hub.c.163.com/zhijiansd/k8s-dns-sidecar-amd64:1.14.5 gcr.io/google_containers/k8s-dns-sidecar-amd64:1.14.5
docker tag hub.c.163.com/zhijiansd/k8s-dns-dnsmasq-nanny-amd64:1.14.5 gcr.io/google_containers/k8s-dns-dnsmasq-nanny-amd64:1.14.5
docker tag hub.c.163.com/ap6108/kubernetes-dashboard-init-amd64:v1.0.1 gcr.io/google_containers/kubernetes-dashboard-init-amd64:v1.0.1
docker tag hub.c.163.com/juranzhijia/kubernetes-dashboard-amd64:v1.7.1 gcr.io/google_containers/kubernetes-dashboard-amd64:v1.7.1 
docker tag hub.c.163.com/k8s163/etcd-amd64:3.0.17  gcr.io/google_containers/etcd-amd64:3.0.17 
```

## 更改kubelet的启动参数

> (或者跳过此节，关闭交互分区，见下一节)

kubernetes的标准配置是禁用交换分区的，但是考虑到实际情况，暂时先通过配置项跳过。

    1. 修改配置
``` bash
vi /etc/systemd/system/kubelet.service.d/10-kubeadm.conf 

# 添加：
Environment="KUBELET_EXTRA_ARGS=--fail-swap-on=false"
```
    2. 重启服务
```
systemctl daemon-reload
systemctl restart kubelet
```

## 关闭交互分区

``` bash
swapoff -a  

# 删除 /etc/fstab 表中的交换分区记录
```
---
> 此处开始master和node分开执行

# Master 

## 初始化Master

> 其中：
> --pod-network-cidr=10.244.0.0/16 为后续使用flannel准备 \
> --skip-preflight-checks 跳过swap分区检查 \
> --kubernetes-version=1.8.2 指定版本，否则又要去google拉取文件 

``` bash
kubeadm init --pod-network-cidr=10.244.0.0/16 --skip-preflight-checks --kubernetes-version=1.8.2
```

``` bash
[kubeadm] WARNING: kubeadm is in beta, please do not use it for production clusters.
[init] Using Kubernetes version: v1.8.2
[init] Using Authorization modes: [Node RBAC]
[preflight] Skipping pre-flight checks
[kubeadm] WARNING: starting in 1.8, tokens expire after 24 hours by default (if you require a non-expiring token use --token-ttl 0)
[certificates] Generated ca certificate and key.
[certificates] Generated apiserver certificate and key.
[certificates] apiserver serving cert is signed for DNS names [iota-k1 kubernetes kubernetes.default kubernetes.default.svc kubernetes.default.svc.cluster.local] and IPs [10.96.0.1 10.8.25.100]
[certificates] Generated apiserver-kubelet-client certificate and key.
[certificates] Generated sa key and public key.
[certificates] Generated front-proxy-ca certificate and key.
[certificates] Generated front-proxy-client certificate and key.
[certificates] Valid certificates and keys now exist in "/etc/kubernetes/pki"
[kubeconfig] Wrote KubeConfig file to disk: "admin.conf"
[kubeconfig] Wrote KubeConfig file to disk: "kubelet.conf"
[kubeconfig] Wrote KubeConfig file to disk: "controller-manager.conf"
[kubeconfig] Wrote KubeConfig file to disk: "scheduler.conf"
[controlplane] Wrote Static Pod manifest for component kube-apiserver to "/etc/kubernetes/manifests/kube-apiserver.yaml"
[controlplane] Wrote Static Pod manifest for component kube-controller-manager to "/etc/kubernetes/manifests/kube-controller-manager.yaml"
[controlplane] Wrote Static Pod manifest for component kube-scheduler to "/etc/kubernetes/manifests/kube-scheduler.yaml"
[etcd] Wrote Static Pod manifest for a local etcd instance to "/etc/kubernetes/manifests/etcd.yaml"
[init] Waiting for the kubelet to boot up the control plane as Static Pods from directory "/etc/kubernetes/manifests"
[init] This often takes around a minute; or longer if the control plane images have to be pulled.
[apiclient] All control plane components are healthy after 49.502140 seconds
[uploadconfig] Storing the configuration used in ConfigMap "kubeadm-config" in the "kube-system" Namespace
[markmaster] Will mark node iota-k1 as master by adding a label and a taint
[markmaster] Master iota-k1 tainted and labelled with key/value: node-role.kubernetes.io/master=""
[bootstraptoken] Using token: 9418ef.829f26b57dd86cc7
[bootstraptoken] Configured RBAC rules to allow Node Bootstrap tokens to post CSRs in order for nodes to get long term certificate credentials
[bootstraptoken] Configured RBAC rules to allow the csrapprover controller automatically approve CSRs from a Node Bootstrap Token
[bootstraptoken] Configured RBAC rules to allow certificate rotation for all node client certificates in the cluster
[bootstraptoken] Creating the "cluster-info" ConfigMap in the "kube-public" namespace
[addons] Applied essential addon: kube-dns
[addons] Applied essential addon: kube-proxy

Your Kubernetes master has initialized successfully!

To start using your cluster, you need to run (as a regular user):

  mkdir -p $HOME/.kube
  sudo cp -i /etc/kubernetes/admin.conf $HOME/.kube/config
  sudo chown $(id -u):$(id -g) $HOME/.kube/config

You should now deploy a pod network to the cluster.
Run "kubectl apply -f [podnetwork].yaml" with one of the options listed at:
  http://kubernetes.io/docs/admin/addons/

You can now join any number of machines by running the following on each node
as root:

  kubeadm join --token 9418ef.829f26b57dd86cc7 10.8.25.100:6443 --discovery-token-ca-cert-hash sha256:1d8b9549ef43bcaee0053258ca64b00abdf2670d20e85302130e6051dd3a5827
```

> 记录并保存最后一行，用于节点加入：

## 使得Master上的普通用户可以访问kubernetes

``` bash
mkdir -p $HOME/.kube
sudo cp -i /etc/kubernetes/admin.conf $HOME/.kube/config
sudo chown $(id -u):$(id -g) $HOME/.kube/config
```

## 创建pod network
```
kubectl apply -f https://raw.githubusercontent.com/coreos/flannel/v0.9.1/Documentation/kube-flannel.yml

```
> 网络这里比较复杂，为了简化安装过程，两台设备的配置最好一样，多网卡的情况，需要规划好使用相关的设备口，如果不是默认设备网卡，需要修改kube-flannel.yml，保证使用的网口互通

---

# Node

## 加入
> root 用户 执行
``` bash
kubeadm join --token 9418ef.829f26b57dd86cc7 10.8.25.100:6443 --discovery-token-ca-cert-hash sha256:1d8b9549ef43bcaee0053258ca64b00abdf2670d20e85302130e6051dd3a5827 --skip-preflight-checks
```

至此，kubernetes环境搭建完成，在Master上查看节点状况：

``` bash
iota@iota-m1:~/k8s$ kubectl get nodes
NAME      STATUS    ROLES     AGE       VERSION
iota-m1   Ready     master    1h        v1.8.4
iota-n1   Ready     <none>    1h        v1.8.4
```


# 基本操作

> yaml 文件的编写

### 常用命令

``` bash
kubectl get --help
kubectl get all --all-namespaces
            pods
            replicationcontrollers
            replicasets
            namespaces
            services
            deployments
```

``` bash
# 查看k8s节点
kubectl get node

# 创建和删除pod
kubectl create -f {podname}-deploy.yml
kubectl delete -f {podname}-deploy.yml
kubectl delete pod {podname} -n anxinyun

# 查看 namespace
kubectl get namespace

# 查看 pods
kubectl get pod --all-namespaces
kubectl get pods -n {namespace} -o wide

kubectl get service -n {namespace}

# 查看pod 最后1000条日志
kubectl logs -n anxinyun {podname} --tail 1000

kubectl describe pod XXX --namespace=XXX

# 进入容器
kubectl exec -it pod --namespace {namespace} /bin/bash

# 拷贝文件
kubectl cp {namespace}/{podname}:/path/dir /local/path

# 查看 node labels
kubectl get nodes --show-labels

# 标记 节点
kubectl label nodes {node} {label.key}={label.value}

```
---
----

# 辅助

### dashboard
> 目前kubernetes的dashboard权限限制较多，尽可能使用kubectl操作。

### API访问集群
- 使用独立的命名空间
- 提供一个用于调用Kubernetes API的 SeviceAccount / token。
- 授予该ServiceAccount集群管理权限（ClusterRoleBinding），参考：[RBAC](https://kubernetes.io/docs/reference/access-authn-authz/rbac/)
``` bash
kubectl create namespace iota
kubectl create serviceaccount iota -n iota
```
授权：
``` bash
#file: cluster-admin.yaml
apiVersion: rbac.authorization.k8s.io/v1
kind: ClusterRoleBinding
metadata:
  name: iota-cluster-admin-rolebinding
subjects:
- kind: ServiceAccount
  name: iota
  namespace: iota
roleRef:
  kind: ClusterRole
  name: cluster-admin
  apiGroup: rbac.authorization.k8s.io
```
``` bash  
kubectl create -f cluster-admin.yaml
```

---
---
---
# 重装k8s

``` bash
kubeadm reset 
systemctl stop kubelet
rm -rf /var/lib/cni/
rm -rf /var/lib/kubelet/*
rm -rf /run/flannel
rm -rf /etc/cni/
ifconfig cni0 down
ifconfig flannel.1 down
ip link delete cni0
ip link delete flannel.1
apt purge -y kubelet kubeadm kubectl kubernetes-cni 
systemctl daemon-reload

```
> 然后重新执行上述步骤



# 扩展节点

```bash
# 执行下面命令，重新生成token newtoken
kubeadm token create 
# 查看token
kubeadm token list
# 打印 ca证书sha256编码hash值 sha256_hash
openssl x509 -pubkey -in /etc/kubernetes/pki/ca.crt | openssl rsa -pubin -outform der 2>/dev/null | openssl dgst -sha256 -hex | sed 's/^.* //'
# 在节点执行
kubeadm join masterip:6443 --token ${newtoken} --discovery-token-ca-cert-hash sha256:${sha256_hash} --skip-preflight-checks
# 在master查看节点状态
kubectl get nodes
```

# 注意

> 有时会出现节点一直无法通信

1. 检查dns

   ```bash
   cat /etc/resolv.conf
   ```

2. 网关

   ```bash
   route add default gw 10.8.30.1
   ```

   

3. 防火墙

   ```
   
   ```

   

# 安装 1.14.3

### 升级kubeadm/kubectl/kubelet版本

```bash
apt-get install -y kubelet=1.14.3-00 kubeadm=1.14.3-00 kubectl=1.14.3-00 kubernetes-cni=0.7.5-00
```

### 拉取镜像

```bash
docker pull repository.anxinyun.cn/k8s/kube-apiserver:v1.14.3
docker pull repository.anxinyun.cn/k8s/kube-controller-manager:v1.14.3
docker pull repository.anxinyun.cn/k8s/kube-scheduler:v1.14.3
docker pull repository.anxinyun.cn/k8s/kube-proxy:v1.14.3
docker pull repository.anxinyun.cn/k8s/pause:3.1
docker pull repository.anxinyun.cn/k8s/etcd:3.3.10
docker pull repository.anxinyun.cn/k8s/coredns:1.3.1
```

> 重新打tag

```bash
docker tag repository.anxinyun.cn/k8s/kube-apiserver:v1.14.3 k8s.gcr.io/kube-apiserver:v1.14.3
docker tag repository.anxinyun.cn/k8s/kube-controller-manager:v1.14.3 k8s.gcr.io/kube-controller-manager:v1.14.3
docker tag repository.anxinyun.cn/k8s/kube-scheduler:v1.14.3 k8s.gcr.io/kube-scheduler:v1.14.3
docker tag repository.anxinyun.cn/k8s/kube-proxy:v1.14.3 k8s.gcr.io/kube-proxy:v1.14.3
docker tag repository.anxinyun.cn/k8s/pause:3.1 k8s.gcr.io/pause:3.1
docker tag repository.anxinyun.cn/k8s/etcd:3.3.10 k8s.gcr.io/etcd:3.3.10
docker tag repository.anxinyun.cn/k8s/coredns:1.3.1 k8s.gcr.io/coredns:1.3.1

```

> 以下命令在master节点执行

```bash
kubeadm init --kubernetes-version=1.14.3 --apiserver-advertise-address=192.168.1.212 --pod-network-cidr=10.244.0.0/16
```

> kubectl version 查看版本
