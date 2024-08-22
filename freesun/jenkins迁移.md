#### Jenkins迁移

Jenkins 的配置和数据存储在 Jenkins 主服务器的文件系统中。了解这些文件夹和文件的作用可以帮助你在迁移 Jenkins 时做出正确的选择。以下是 Jenkins 主要文件和文件夹的解释，以及迁移时需要关注的部分：

+ Jenkins 目录结构
  JENKINS_HOME 目录：

  JENKINS_HOME 是 Jenkins 的根目录，存储所有配置、插件、构建历史等数据。默认情况下，这个目录位于 /var/lib/jenkins，但可以在启动时通过环境变量 JENKINS_HOME 进行更改。

+ config.xml：

  这个文件是 Jenkins 全局配置文件，包含系统设置、全局安全配置等信息。在迁移时一定要拷贝。

+ jobs/

  这个文件夹包含所有的 Jenkins 任务（即 job）。每个 job 有一个子文件夹，名称与 job 名称一致。每个子文件夹包含该 job 的配置和构建历史。
  迁移时要拷贝：整个 jobs/ 文件夹。

+ plugins/：

  这个文件夹包含所有已安装的插件，每个插件以 .hpi 或 .jpi 文件存储。
  迁移时要拷贝：整个 plugins/ 文件夹。

+ users/：

  这个文件夹包含 Jenkins 用户的配置文件，包括用户的个人设置和 API token 等。
  迁移时要拷贝：整个 users/ 文件夹。

+ secrets/：

  这个文件夹存储各种加密密钥和凭据。
  迁移时要拷贝：整个 secrets/ 文件夹。

+ nodes/：

  这个文件夹包含所有 Jenkins 节点（即 agent）的配置。
  迁移时要拷贝：整个 nodes/ 文件夹。



进入alpine中

```sh
sudo chown -R jenkins:jenkins /var/jenkins_home
sudo chmod -R 755 /var/jenkins_home

du -h --max-depth=1 /var/jenkins_home

chown -R jenkins:jenkins /data
chmod -R 777 /data

```



kubectl create serviceaccount jenkins.

迁移jenkins的所有任务：

```sh
rsync -av --exclude='builds' jobs jobs_bak

kubectl cp -n devops-tools ubuntu-799966c55-x8gcq:/app-pv/jobs_bak /home/cloud/jobs_bak

scp -r jobs_bak anxinyun@10.8.40.101:/home/anxinyun/jobs_bak

kubectl cp -njenkins jobs_bak/jobs alpine-6d974d6d97-72n76:/data/
```

拷贝最近5条build记录：

```sh
#!/bin/bash

# 源 Jenkins jobs 目录
SRC_JOBS_DIR="/var/jenkins_home/jobs"
# 目标 Jenkins jobs 目录
DST_JOBS_DIR="/var/jenkins_home/jobs_bak"

# 确保目标目录存在
mkdir -p "$DST_JOBS_DIR"

# 遍历每个 job 目录
for job in "$SRC_JOBS_DIR"/*; do
  if [ -d "$job" ]; then
    job_name=$(basename "$job")
    echo "Processing job: $job_name"

    # 创建目标 job 目录
    mkdir -p "$DST_JOBS_DIR/$job_name"
    
    # 拷贝 job 的配置文件和其他非 build 文件夹
    rsync -a --exclude='builds' "$job/" "$DST_JOBS_DIR/$job_name/"
    
    # 创建目标 builds 目录
    mkdir -p "$DST_JOBS_DIR/$job_name/builds"
    
    # 获取构建目录下的所有构建记录，并按名称排序（假设名称是数字）
    builds=($(ls -1 "$job/builds" | sort -n))
    
    # 计算需要保留的构建数量
    total_builds=${#builds[@]}
    if [ $total_builds -gt 5 ]; then
      start=$((total_builds - 5))
    else
      start=0
    fi
    
    # 拷贝最近的构建记录
    for (( i=$start; i<$total_builds; i++ )); do
      build=${builds[$i]}
      echo "Copying build: $build"
      cp -r "$job/builds/$build" "$DST_JOBS_DIR/$job_name/builds/"
    done
  fi
done

echo "Migration completed."

```
