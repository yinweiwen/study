### 通过Helm快速安装Datahub.



github：https://github.com/acryldata/datahub-helm

添加datahub的helm仓库

```sh
# snap install helm --classic
helm repo add datahub https://helm.datahubproject.io/

helm repo update
helm search repo datahub

NAME                         	CHART VERSION	APP VERSION	DESCRIPTION                                      
datahub/datahub              	0.2.175      	0.10.4     	A Helm chart for LinkedIn DataHub                
datahub/datahub-prerequisites	0.0.18       	           	A Helm chart for packages that Datahub depends on

kubectl create secret generic mysql-secrets --from-literal=mysql-root-password=datahub --namespace datahub
kubectl create secret generic neo4j-secrets --from-literal=neo4j-password=datahub --namespace datahub
kubectl create secret generic postgresql-secrets --from-literal=postgres-password=postgres --namespace datahub

```

安装前置依赖 （##这里使用现有环境中的kafka和es和postgres，故跳过该步骤）

```
helm install prerequisites ./datahub-prerequisites-0.0.18.tgz --namespace datahub --create-namespace
```

安装Datahub

```sh
#helm install --timeout 300s datahub datahub/datahub --values values.yaml
helm install datahub ./datahub-0.2.175.tgz --namespace datahub
# Error: INSTALLATION FAILED: Get "https://github.com/acryldata/datahub-helm/releases/download/datahub-0.2.175/datahub-0.2.175.tgz": unexpected EOF
# 科学上网下载后上传到服务器，然后执行：
helm install --timeout 300s datahub ./datahub-0.2.175.tgz --values values.yaml

# 将helm生成kubernetes的yaml文件
helm template datahub ./datahub-0.2.175.tgz --values values.yaml --namespace datahub --output-dir a
```



查看发布版本

```sh
root@fastest:/home/fastest# helm list
NAME   	NAMESPACE	REVISION	UPDATED                                	STATUS  	CHART          	APP VERSION
datahub	default  	1       	2023-06-25 18:28:59.01655685 +0800 CST 	failed  	datahub-0.2.175	0.10.4     
kruise 	default  	1       	2023-01-06 15:12:52.093350102 +0800 CST	deployed	kruise-1.3.0   	1.3.0 
```



#### Helm 是 Kubernetes 的包管理器

Helm中的概念；

> *Chart* 代表着 Helm 包。它包含在 Kubernetes 集群内部运行应用程序，工具或服务所需的所有资源定义。你可以把它看作是 Homebrew formula，Apt dpkg，或 Yum RPM 在Kubernetes 中的等价物。
>
> *Repository（仓库）* 是用来存放和共享 charts 的地方。它就像 Perl 的 [CPAN 档案库网络](https://www.cpan.org/) 或是 Fedora 的 [软件包仓库](https://src.fedoraproject.org/)，只不过它是供 Kubernetes 包所使用的。
>
> *Release* 是运行在 Kubernetes 集群中的 chart 的实例。一个 chart 通常可以在同一个集群中安装多次。每一次安装都会创建一个新的 *release*。以 MySQL chart为例，如果你想在你的集群中运行两个数据库，你可以安装该chart两次。每一个数据库都会拥有它自己的 *release* 和 *release name*。



查找包和仓库

```sh
root@fastest:/home/fastest# helm search hub datahub
URL                                               	CHART VERSION	APP VERSION	DESCRIPTION                                       
https://artifacthub.io/packages/helm/datahub/da...	0.2.175      	0.10.4     	A Helm chart for LinkedIn DataHub                 
https://artifacthub.io/packages/helm/wikimedia/...	0.0.32       	0.10.0     	A Helm chart for DataHub at the Wikimedia Found...
https://artifacthub.io/packages/helm/datahub-ko...	0.2.2        	0.3.1      	A Helm chart for LinkedIn DataHub's datahub-gms...
...
root@fastest:/home/fastest# helm search repo datahub
NAME                         	CHART VERSION	APP VERSION	DESCRIPTION                                      
datahub/datahub              	0.2.175      	0.10.4     	A Helm chart for LinkedIn DataHub                
datahub/datahub-prerequisites	0.0.18       	           	A Helm chart for packages that Datahub depends on
```



安装包

```sh
# helm install RELEASE-NAME CHART-NAME
$ helm install happy-panda bitnami/wordpress

# helm status RELEASE-NAME
$ helm status happy-panda

# 显示chart中可配置的参数
$ helm show values bitnami/wordpress
# 查看Release的values
$ helm get values happy-panda
```

安装过程中有两种方式传递配置数据：

- `--values` (或 `-f`)：使用 YAML 文件覆盖配置。可以指定多次，优先使用最右边的文件。
- `--set`：通过命令行的方式对指定项进行覆盖。



升级和回滚

```sh
$ helm upgrade -f panda.yaml happy-panda bitnami/wordpress
$ helm rollback happy-panda 1
helm history [RELEASE]
```



卸载

```sh
# 卸载一个Release
$ helm uninstall happy-panda

# 保留一条删除记录
# 使用 helm list --uninstalled 只会展示使用了 --keep-history 删除的 release。
# helm list --all 会展示 Helm 保留的所有 release 记录，包括失败或删除的条目（指定了 --keep-history）：
$ helm uninstall --keep-history
```



仓库使用

```sh
$ helm repo list
$ helm repo add dev https://example.com/dev-charts

```



创建自己的chart

```sh
$ helm create deis-workflow
# 现在，./deis-workflow 目录下已经有一个 chart 了。你可以编辑它并创建你自己的模版。
# 打包发布
$ helm package deis-workflow
# 用本地宝安装
$ helm install deis-workflow ./deis-workflow-0.1.0.tgz
```

