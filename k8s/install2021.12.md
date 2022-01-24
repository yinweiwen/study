### 通过Kubeadm安装K8S集群

https://kubernetes.io/docs/setup/production-environment/tools/kubeadm/install-kubeadm/

准备工作：

1. Swap disabled

2. 网卡MAC地址和product_uuid唯一

3. Letting iptables see bridged traffic

   ```sh
   # 确保br_netfilter模块已加载
   lsmod | grep br_netfilter
   ```

4. 安装容器运行时，我们使用已经安装的docker

   ```sh
   支持以下:
   Runtime	Path to Unix domain socket
   Docker	/var/run/dockershim.sock
   containerd	/run/containerd/containerd.sock
   CRI-O	/var/run/crio/crio.sock
   ```

5. 其他

   ```sh
   #禁用SELINUX
   $ sudo setenforce 0                 #0代表permissive 1代表enforcing
   # 开启ipv4转发
    vi /etc/sysctl.conf
   net.ipv4.ip_forward = 1
   # 关闭防火墙
   $ sudo iptables -P FORWARD ACCEPT
   # 永久生效
   /usr/sbin/iptables -P FORWARD ACCEPT
   # 关闭docker 的iptable
   {
   	"iptables":false
   }
   # 禁用swap
   swapoff -a
   
   # 配置iptables参数，使得流经网桥的流量也经过iptables/netfilter防火墙
   $ sudo tee /etc/sysctl.d/k8s.conf <<-'EOF'
   net.bridge.bridge-nf-call-ip6tables = 1
   net.bridge.bridge-nf-call-iptables = 1
   EOF
   
   $ sudo sysctl --system
   ```

   

6. 安装工具

   + kubeadm 创建集群
   + kubelet 运行在所有节点的服务，控制容器启停
   + kubectl 集群命令行工具

   ```sh
   sudo apt-get update && sudo apt-get install -y apt-transport-https curl
   
   sudo curl -s https://mirrors.aliyun.com/kubernetes/apt/doc/apt-key.gpg | sudo apt-key add -
   
   sudo tee /etc/apt/sources.list.d/kubernetes.list <<-'EOF'
   deb https://mirrors.aliyun.com/kubernetes/apt kubernetes-xenial main
   EOF
   
   sudo apt-get update
   
   #删除之前的版本
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
   ipvsadm --clear
   rm -rf $HOME/.kube/config
   
   apt purge -y kubelet kubeadm kubectl kubernetes-cni 
   systemctl daemon-reload
   
   # 查看可用版本
   apt-cache madison kubeadm
   
   # 安装指定版本
   # 这里加上 --allow-downgrades --allow-change-held-packages 是因为之前尝试安装1.23后，需要进行降级处理
   sudo apt-get install -y --allow-downgrades --allow-change-held-packages kubelet=1.18.3-00 kubeadm=1.18.3-00 kubectl=1.18.3-00
   sudo apt-mark hold kubelet=1.18.3-00 kubeadm=1.18.3-00 kubectl=1.18.3-00
   
   # 随系统启动
   sudo systemctl enable kubelet && sudo systemctl start kubelet
   
   # 查看需要哪些镜像
   kubeadm config images list --kubernetes-version=v1.18.3
   
   #docker pull镜像拉取命令
   docker pull mirrorgcrio/kube-apiserver:v1.18.3
   docker pull mirrorgcrio/kube-controller-manager:v1.18.3
   docker pull mirrorgcrio/kube-scheduler:v1.18.3
   docker pull mirrorgcrio/kube-proxy:v1.18.3
   docker pull mirrorgcrio/pause:3.2
   docker pull mirrorgcrio/etcd:3.4.3-0
   docker pull mirrorgcrio/coredns:1.6.7
   #docker tag镜像重命名
   docker tag mirrorgcrio/kube-apiserver:v1.18.3 k8s.gcr.io/kube-apiserver:v1.18.3
   docker tag mirrorgcrio/kube-controller-manager:v1.18.3 k8s.gcr.io/kube-controller-manager:v1.18.3
   docker tag mirrorgcrio/kube-scheduler:v1.18.3 k8s.gcr.io/kube-scheduler:v1.18.3
   docker tag mirrorgcrio/kube-proxy:v1.18.3 k8s.gcr.io/kube-proxy:v1.18.3
   docker tag mirrorgcrio/pause:3.2 k8s.gcr.io/pause:3.2
   docker tag mirrorgcrio/etcd:3.4.3-0 k8s.gcr.io/etcd:3.4.3-0
   docker tag mirrorgcrio/coredns:1.6.7 k8s.gcr.io/coredns:1.6.7
   
   
   # master
   kubeadm init --pod-network-cidr=10.244.0.0/16 --kubernetes-version=1.18.3 --apiserver-advertise-address=10.8.30.38
   
   	mkdir -p $HOME/.kube
     sudo cp -i /etc/kubernetes/admin.conf $HOME/.kube/config
     sudo chown $(id -u):$(id -g) $HOME/.kube/config
     
   wget https://raw.githubusercontent.com/coreos/flannel/master/Documentation/kube-flannel.yml
   
   # slave
   kubeadm join 10.8.30.38:6443 --token wfwsdf.xja48e2nrxp0jj9t \
       --discovery-token-ca-cert-hash sha256:bde8eab3fd3b96d2fe98b27d2d0f5692042feace0f59944b4a6ba15cb7a2c9a9
       
       
   # 将本地hosts加入core-dns。
   kubectl get cm -n kube-system coredns -o yaml > /home/anxin/core-dns.yaml
   ```

   ```yaml
   apiVersion: v1
   data:
     Corefile: |
       .:53 {
           errors
           health {
              lameduck 5s
           }
           ready
           kubernetes cluster.local in-addr.arpa ip6.arpa {
              pods insecure
              fallthrough in-addr.arpa ip6.arpa
              ttl 30
           }
           hosts {
              10.8.30.35      node35
              10.8.30.36      node36
              10.8.30.37      node37
              10.8.30.38      node38
              10.8.30.39      node39
              10.8.30.40      node40
           }
           prometheus :9153
           forward . /etc/resolv.conf
           cache 30
           loop
           reload
           loadbalance
       }
   kind: ConfigMap
   ...
   ```

   ```sh
   # 重启core-dns pods
   
   ```

   



​    	[别人的安装指南](https://www.cnblogs.com/xiao987334176/p/12696740.html)

​		[通过阿里云获取gcr.io上的镜像](https://blog.csdn.net/yjf147369/article/details/80290881)





5. VMMEM 占用过高

   Hyper-V Manager中关闭