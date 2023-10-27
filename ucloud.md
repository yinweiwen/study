## 2023-10-26

服务器地址：165.154.182.118 (洛杉矶)

用户密码：ubuntu/yww***



在 Ubuntu 20.04 上安装 Docker、Docker Compose、PostgreSQL 和 Nginx 需要执行一系列命令。以下是安装这些软件的步骤：

1. 安装 Docker：

   ```
   bash复制代码# 更新软件包列表
   sudo apt update
   
   # 安装依赖工具
   sudo apt install -y apt-transport-https ca-certificates curl software-properties-common
   
   # 添加 Docker 官方 GPG 密钥
   curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /usr/share/keyrings/docker-archive-keyring.gpg
   
   # 添加 Docker APT 存储库
   echo "deb [arch=amd64 signed-by=/usr/share/keyrings/docker-archive-keyring.gpg] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
   
   # 安装 Docker
   sudo apt update
   sudo apt install -y docker-ce
   
   # 启动 Docker 服务并设置开机自启
   sudo systemctl start docker
   sudo systemctl enable docker
   
   # 验证安装
   sudo docker --version
   ```

2. 安装 Docker Compose：

   ```
   bash复制代码# 下载 Docker Compose 到 /usr/local/bin 目录
   sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
   
   # 添加执行权限
   sudo chmod +x /usr/local/bin/docker-compose
   
   # 验证安装
   docker-compose --version
   ```

3. 安装 PostgreSQL：

   ```
   bash复制代码# 安装 PostgreSQL 和相关的库
   sudo apt install -y postgresql postgresql-contrib
   
   # 启动 PostgreSQL 服务并设置开机自启
   sudo systemctl start postgresql
   sudo systemctl enable postgresql
   
   # 验证安装
   psql --version
   ```

4. 安装 Nginx：

   ```
   bash复制代码# 安装 Nginx
   sudo apt install -y nginx
   
   # 启动 Nginx 服务并设置开机自启
   sudo systemctl start nginx
   sudo systemctl enable nginx
   
   # 验证安装
   nginx -v
   ```