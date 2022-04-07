## Ubuntu18 ON VirtualBox

### Basic

```sh
apt-get update
apt-get upgrade
apt-get install openssh-server
apt-get install openjdk-8-jdk

# install docker
sudo apt-get install \
    apt-transport-https \
    ca-certificates \
    curl \
    gnupg-agent \
    software-properties-common
    
 curl -fsSL https://mirrors.ustc.edu.cn/docker-ce/linux/ubuntu/gpg | sudo apt-key add -
 
 sudo add-apt-repository \
   "deb [arch=amd64] https://mirrors.ustc.edu.cn/docker-ce/linux/ubuntu/ \
  $(lsb_release -cs) \
  stable"
  
  sudo apt-get update
  
  sudo apt-get install docker-ce docker-ce-cli containerd.io
  
  sudo docker run hello-world
  
# 国内镜像
vi  /etc/docker/daemon.json 
# {"registry-mirrors":["https://reg-mirror.qiniu.com/"]}
sudo systemctl daemon-reload
sudo systemctl restart docker

#docker-compose
 sudo curl -L "https://github.com/docker/compose/releases/download/v2.2.2/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
 sudo chmod +x /usr/local/bin/docker-compose
```



### MicroK8S

```shell
snap install microk8s --classic --channel=1.13/stable
```

fetch-images.sh

```
#!/bin/bash
images=(
k8s.gcr.io/pause:3.1=mirrorgooglecontainers/pause-amd64:3.1
gcr.io/google_containers/defaultbackend-amd64:1.4=mirrorgooglecontainers/defaultbackend-amd64:1.4
k8s.gcr.io/kubernetes-dashboard-amd64:v1.10.1=registry.cn-hangzhou.aliyuncs.com/google_containers/kubernetes-dashboard-amd64:v1.10.1
k8s.gcr.io/heapster-influxdb-amd64:v1.3.3=registry.cn-hangzhou.aliyuncs.com/google_containers/heapster-influxdb-amd64:v1.3.3
k8s.gcr.io/heapster-amd64:v1.5.2=registry.cn-hangzhou.aliyuncs.com/google_containers/heapster-amd64:v1.5.2
k8s.gcr.io/heapster-grafana-amd64:v4.4.3=registry.cn-hangzhou.aliyuncs.com/google_containers/heapster-grafana-amd64:v4.4.3
k8s.gcr.io/metrics-server-amd64:v0.3.6=registry.cn-hangzhou.aliyuncs.com/google_containers/metrics-server-amd64:v0.3.6
)

OIFS=$IFS; # ±£´æ¾ÉÖµ

for image in ${images[@]};do
    IFS='='
    set $image
    docker pull $2
    docker tag  $2 $1
    docker rmi  $2
    docker save $1 > 1.tar && microk8s.docker --namespace k8s.io image import 1.tar && rm 1.tar
    IFS=$OIFS; # »¹Ô­¾ÉÖµ
done
```



### Golang

```sh
# downloading 1.18

# /etc/profile
export GO111MODULE=auto
export GOPROXY=https://goproxy.cn,direct
export GOROOT=/home/yww/go
export PATH=$PATH:/home/yww/go/bin

# 安装GCC
sudo apt install gcc

root@yww-VirtualBox:/home/yww/pholcus-master# go env -w GOSUMDB=off
root@yww-VirtualBox:/home/yww/pholcus-master# go env -w GOPROXY=https://goproxy.cn
root@yww-VirtualBox:/home/yww/pholcus-master# go build example_main.go 
```





## Flink On Yarn

部署单机hadoop，参考：

https://phoenixnap.com/kb/install-hadoop-ubuntu

使root用户有操作hdfs /目录权限。

```sh
hdfs dfs -chmod -R 777 hdfs://127.0.0.1:9000/

#将dlink plugin目录下的jar包提交到lib目录
ll | grep jar |  awk '{print $9}' | xargs -n1 -I {} hdfs dfs -put {} /lib
```

yarn

```sh
yarn application -list
```



dlink

```
java -Dloader.path=./lib,./plugins -Ddruid.mysql.usePingMethod=false -jar -Xms512M -Xmx2048M -XX:PermSize=512M -XX:MaxPermSize=1024M ./dlink-admin-0.6.0.jar
```



mysql 开启binlog

```
 command: mysqld --character-set-server=utf8mb4 --collation-server=utf8mb4_unicode_ci --log-bin=/var/log/mysql/mysql-bin.log --server-id=1
 
 #sql
 show variables like '%log_bin%'
```

