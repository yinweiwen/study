---

# 什么是WSL
适用于 Linux 的 Windows 子系统可让开发人员按原样运行 GNU/Linux 环境 - 包括大多数命令行工具、实用工具和应用程序 - 且不会产生传统虚拟机或双启动设置开销。<br />你可以

- 在windows 下 使用你喜欢的linux 发行版
- 在 linux 运行常用的命令行软件，（例如 `grep`、`sed`、`awk`）或其他 ELF-64 二进制文件
- 运行 Bash shell 脚本和 GNU/Linux 命令行应用程序，包括
   - 工具：vim、emacs、tmux
   - 语言：NodeJS、Javascript、Python、C#、Rust、Go 等。
   - 服务：SSHD、Nginx、MySQL、Apache、lighttpd、MongoDB、PostgreSQL
- 使用自己的 GNU/Linux 分发包管理器安装其他软件。
- 使用类似于 Unix 的命令行 shell 调用 Windows 应用程序。
- 在 Windows 上调用 GNU/Linux 应用程序
## 为啥使用 WLS 2 ？
![image.png](https://cdn.nlark.com/yuque/0/2021/png/668312/1621773949639-ee89734e-1559-4aaa-a85e-1d834b67424a.png#align=left&display=inline&height=391&margin=%5Bobject%20Object%5D&name=image.png&originHeight=522&originWidth=987&size=32808&status=done&style=none&width=740)<br />从上述比较表中可以看出，除了跨操作系统文件系统的性能外，WSL 2 都比 WSL 1 更具优势。<br />WSL 2 使用最新、最强大的虚拟化技术在轻量级实用工具虚拟机 (VM) 中运行 Linux 内核。 但是，WSL 2 不是传统的 VM 体验。<br />WSL 2 使用全新的体系结构，该体系结构受益于运行真正的 Linux 内核
## 升级WSL


> 检查你的系统版本号
> 设置 > 更新和安全 > OS 内部版本信息


<br />![](https://cdn.nlark.com/yuque/0/2021/png/668312/1621772801926-74ee6a22-981d-4835-abda-9b15453ccd63.png#align=left&display=inline&height=449&margin=%5Bobject%20Object%5D&originHeight=449&originWidth=1187&size=0&status=done&style=none&width=1187)<br />![image.png](https://cdn.nlark.com/yuque/0/2021/png/668312/1621773225117-acfb0797-f17b-45cf-a100-4258d511c90b.png#align=left&display=inline&height=262&margin=%5Bobject%20Object%5D&name=image.png&originHeight=262&originWidth=550&size=12246&status=done&style=none&width=550)<br />如果你的版本号低于** 18362**，请使用 [Windows 更新助手](https://www.microsoft.com/zh-cn/software-download/windows10) 更新你的 Windows 10 。<br />
<br />

# 安装WSL 2


1. 启用“ _虚拟机平台(Virtual Machine Platform)_”可选功能


```powershell
dism.exe /online /enable-feature /featurename:VirtualMachinePlatform /all /norestart
```

2. 下载并安装更新组件：[https://aka.ms/wsl2kernel](https://aka.ms/wsl2kernel)
2. <br />



```powershell
# 将 WSL 2 设置为默认版本
wsl --set-default-version 2

# 查看有已安装的子系统
wsl -l -v
  NAME            STATE           VERSION
* Ubuntu-18.04    Stopped         2
  kali-linux      Running         2

# 迁移到version2
wsl --set-version Ubuntu-18.04 2
```

<br />后面没有安装子系统的可以 打开 win10 的 Microsoft Store 选择 你喜欢的linux 发行版。<br />![](https://cdn.nlark.com/yuque/0/2021/png/668312/1621773443828-3dc4dfc9-61a3-4e02-80d8-4a88893c4be1.png#align=left&display=inline&height=795&margin=%5Bobject%20Object%5D&originHeight=1060&originWidth=1593&size=0&status=done&style=none&width=1195)<br />在分发版的页面中，选择“获取”。<br />首次启动新安装的 Linux 分发版时，将打开一个控制台窗口，系统会要求你等待一分钟或两分钟，以便文件解压缩并存储到电脑上。 之后的启动时间应不到一秒<br />然后，需要为新的 Linux 分发版创建用户帐户和密码：<br />![](https://cdn.nlark.com/yuque/0/2021/png/668312/1621773649289-a75b0370-c147-41bb-92ce-1a00e19a8f3f.png#align=left&display=inline&height=575&margin=%5Bobject%20Object%5D&originHeight=766&originWidth=1348&size=0&status=done&style=none&width=1011)
# 
# 使用


## [简单配置优化](http://blog.lingwenlong.com/2020/04/22/ubuntu-tips/)


### 使用xshell 连接


> 子系统自己的终端不好用，配置下使用xhell 来连接使用


<br />修改 ssh 配置<br />

```bash
> sudo vi /etc/ssh/sshd_config
Port 1022
PasswordAuthentication yes

> sudo service ssh restart
# 这里可能会报错 error: Could not load host key: /etc/ssh/ssh_host_rsa_key ...

ssh-keygen -t rsa -f /etc/ssh/ssh_host_rsa_key 
ssh-keygen -t dsa -f /etc/ssh/ssh_host_dsa_key
```


## [安装 docker](http://blog.lingwenlong.com/2020/03/28/docker-install/)


### 补充


> 由于子系统中不支持自启程序



> 在 ubuntu 中 编写初始化脚本



```bash
> sudo vi /etc/init.sh

#!/bin/bash

service ssh start && service docker start

> chmod +x /etc/init.sh
```


> 在 windows 下创建启动脚本 start-ubuntu.bat(start-ubuntu.cmd)



```cmd
@echo off

wsl --shutdown
wsl -d Ubuntu-20.04 -u root /etc/init.sh
```


> 后面每次启动后执行下这个脚本，就可以了



> 想设置开机自启：WIN + R 输入 `shell:startup` , 把脚本放进去，就可以了

