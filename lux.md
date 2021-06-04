## netstat

-a all
-t tcp
-u udp
-l listen
-p pid list

## ps

-A ：所有的进程均显示出来，与 -e 具有同样的效用；
-a ： 显示现行终端机下的所有进程，包括其他用户的进程；
-u ：以用户为主的进程状态 ；
x ：通常与 a 这个参数一起使用，可列出较完整信息。
输出格式规划：
l ：较长、较详细的将该 PID 的的信息列出；
j ：工作的格式 (jobs format)
-f ：做一个更为完整的输出。

## nc 网络调试

```shell
test tcp
# nc -z -v [hostname/IP address] [port number]
test udp
# nc -z -v -u [hostname/IP address] [port number]

```

## step by step 安装ubuntu

1. rufus-2.15.exe 制作 ubuntu-16.04
2. sudo apt-get install openssh-server
3. sudo apt-get install openjdk-8-jdk

挂载网络磁盘(Samba服务)
mkdir -p localdir
apt install cifs-utils
mount -t cifs //10.8.30.163/versions /var/jenkins-version -o rw

>> tail:
tail -f 循环读取
tail -n 行数

>> 列出所有版本信息
[root@localhost ~]# lsb_release -a

>> Systemctl  (https://linux.cn/article-5926-1.html)
Systemctl是一个systemd工具，主要负责控制systemd系统和服务管理器。
分析启动耗时
systemd-analyze [blame]/各进程  [critical-chain]/关键链
systemctl list-unit-files 列出所有可用单元
systemctl list-units 列出所有运行中单元
systemctl --failed  列出所有失败单元
systemctl is-enabled crond.service 检查某个单元（如 cron.service）是否启用
systemctl status firewalld.service 检查某个单元或服务是否运行 
systemctl start httpd.service

systemctl restart httpd.service

systemctl stop httpd.service

systemctl reload httpd.service

systemctl status httpd.service


>> hdfs
查看hdfs存储空间：
hdfs dfs -du -h / 
hdfs dfs -rmr  hdfs://node35:9000/user/anxin/check-point
docker logs xxx container_id

>> hadoop 集群部署时确保所有机器上用户必须一样
	如
	useradd hadoop
	passwords hadoop
	// 加入sudoers
	su
	chmod 777 /etc/sudoers
	vi /etc/sudoers
		-- hadoop    ALL=(ALL)       ALL
	chmod 440 /etc/sudoers

	su hadoop
	sudo mkdir /home/hadoop
	sudo chown /home/hadoop

## 内存问题排查

	free [-m -g...]
	 total        used        free      shared  buff/cache   available
Mem:       12219288     7485148      271236       27104     4462904     4311184
Swap:             0           0           0
	mem:物理内存 swap硬盘上交换分区的使用情况。只有mem被当前进程实际占用完,即没有了buffers和cache时，才会使用到swap
	MEM -- total: 总物理内存大小  used: 总计分配给缓存使用的数量(包含buff/cache)  free: 未被分配的内存  shared: 共享内存(一般系统不会用到)
			buff/cache： 分配但未被使用的cache和buffer数量
	<https://blog.csdn.net/tianlesoftware/article/details/5463790>
	

	dmidecode -t processor memory [bios, system, baseboard, chassis, processor, memory, cache, connector, slot]
	
	top -d 1(1秒更新一次)  -c（显示完整的路径名称）  -i(不现实任何Idle和zombie进程)  -m(更新次数，完成后退出)
	【】【】     【优先级】				  
	PID USER      PR  NI【优先级别数值】    VIRT【进程占用的虚拟内存值】    RES【进程占用的物理内存值】
	SHR【进程使用的共享内存值】 S【进程的状态，其中S表示休眠，R表示正在运行，Z表示僵死状态，N表示该进程优先值是负数】
	%CPU %MEM     TIME+ COMMAND                                                                                                                       
 3581 root      20   0  415048 266628  21956 S   6.3  2.2 512:58.32 kube-apiserver                                                                                                                
  730 root      20   0  708712  61548  16404 S   4.7  0.5 333:30.48 kubelet                                                                                                                       
 3241 root      20   0  139472  57008  15800 S   4.0  0.5 353:34.31 kube-controller  
	

	top下支持的操作：
	 《空格》：立刻刷新。
	
	P：根据CPU使用大小进行排序。
	
	T：根据时间、累计时间排序。
	
	q：退出top命令。
	
	m：切换显示内存信息。
	
	t：切换显示进程和CPU状态信息。
	
	c：切换显示命令名称和完整命令行。
	
	M：根据使用内存大小进行排序。
	
	W：将当前设置写入~/.toprc文件中
	
	SHIFT+M 固定按内存排序


?	
	uptime 显示系统运行时长 最近1分钟/5分钟/15分钟的系统负载
	17:38:07 up 6 days,  1:13,  4 users,  load average: 0.32, 0.36, 0.45
	
	vmstat显示
	procs -----------memory---------- ---swap-- -----io---- -system-- ------cpu-----
	 r  b   swpd   free   buff  cache   si   so    bi    bo   in   cs us sy id wa st
	 0  0      0 251324 477184 3994764    0    0     8    48   10   12  3  2 94  1  0
	 其中：
	 io:
		bi - 从磁盘每秒读取的块数（blocks/s）
		bo - 每秒写到磁盘的块数（blocks/s）
	 cpu:
		us - 用户进程占cpu比例
		sy - 系统占用比例
		id - 空闲时间比例
		wa - cpu等待未决的磁盘IO时间比例
		
	iostat 用于统计CPU的使用情况及tty设备、硬盘和CD-ROM的I/0量

## GREP查询多个条件

kubectl logs spark-et-driver-savoir | grep 'a\|b'
kubectl logs -n savoir savoir-recv2-d55c84956-lwt47 --tail 100000 | egrep 'savoir_data.*d931eb76-7703-4d8c-a240-8158a02ceeae.*"result":{"code":3002'
netstat -an | grep -E "ESTABLISHED|WAIT"

使用方式：egrep [OPTIONS] PATTERN [FILE...] 

　　grep -E [OPTIONS] PATTERN [FILE...]

　　-i：忽略字符的大小写
　　-o：仅显示匹配到的字符串本身
　　-v：显示不被模式匹配到的行
　　-q：静默模式，即不输出任何信息
　　-A #：显示被模式匹配的行及其后#行
　　-B #：显示被模式匹配的行及其前#行
　　-C #：显示被模式匹配的行及其前后各#行
　　-G:支持基本正则表达式


## chmod 用法
	u -- User 文件或目录的拥有者
	g -- Group 所属群组
	o -- Other
	a -- 全部用户
	r -- 读取权限，数字代号4
	w -- 写入权限，数字代号2
	x -- 执行或切换权限，数字代号1
	- -- 不具任何权限，数字代号0
	s -- 特殊功能说明：变更文件或目录的权限
	-R 递归处理
	chmod u+x f01 // 设置拥有者的可执行属性
	chmod u=rwx,g=rw f01
	chmod 764 f01
	
	- rw- r-- r-- 
	第一个字符"-"表示普通文件 "d"表示目录
	后面三组分别为 u/g/o 用户的权限


## 查找/删除 指定日期的文件
	find .  -type f  -name *.*  -mtime +10  -exec rm {} \;
	. 当前目录
	-type f 查找文件
	-name *.log 查找名称后缀log的文件
	-mtime +180  查询180天以前的文件
	find . -type f -mtime -180 -exec ls -l {} \; | more
	find . -newermt '2016-11-03' ! -newermt '2016-11-04' -exec ls -l {} \;
	
	find . -type d -ctime +10 | xargs rm -rf;
	删除修改时间超过10天的所有文件夹 （等价于 find /path/to/base/dir/* -type d -ctime +10 -exec rm -rf {} \;）
	
	// 到version目录执行
	find . -type d -mindepth 1 -maxdepth 2  -ctime +10 | xargs rm -rf
	...TO LERN MORE
	
	>>
	-amin <分钟> 查找在指定时间曾被存取过的文件或目录
	-anewer <参考文件或目录> 查找其存取时间交指定文件或目录的存取时间更新的
	-atime<24小时数>：查找在指定时间曾被存取过的文件或目录，单位以24小时计算；
	-depth 目录层次
	-daystart 从本日开始计算时间
	-empty 寻找空文件或文件夹
	-name "*.txt" 查找文件名
	-iname "*.txt" 同上，忽略大小写
	find . -type f -size +10k 搜索大于10KB的文件
	find . -maxdepth 3 -type f 向下最大深度限制为3
	find . -mindepth 2 -type f 搜索出深度距离当前目录至少2个子目录的所有文件
	find /usr/ -path "*local*" 路径匹配
	find . -regex ".*\(\.txt\|\.pdf\)$" 基于正则表达式匹配文件路径

## 磁盘空间 和 查找大文件
```bash
find / -type f -size +200M 2>/dev/null|xargs du -shm|sort -nr|awk '{print $2}'|ls -l

ls -ll
du -h –max-depth=1 *  可以查看当前目录下各文件、文件夹的大小
du -sh 查询当前目录总大小

```


?	
## 后台启动进程
  *nohup 如果宿主ssh进程关闭，后台nohup进程也会被关闭
  建议使用 screen
  apt-get install screen
  > screen 按Enter，进入新的screen
	> 在新的screen里面使用nohup（或者直接启动）启动我们的进程
	> Ctrl A + D 退出
	> 显示 [detached from 8377.pts-12.node35]
  > 列出当前用户所有screen -ls
  > 进入已有screen -r 8377.pts-12.node35

## 关闭交换分区
https://serverfault.com/questions/684771/best-way-to-disable-swap-in-linux
	1. Identify configured swap devices and files with cat /proc/swaps.
	2. Turn off all swap devices and files with swapoff -a.
	3. Remove any matching reference found in /etc/fstab.
	4. Optional: Destroy any swap devices or files found in step 1 to prevent their reuse. Due to your concerns about leaking sensitive information, you may wish to consider performing some sort of secure wipe.
	
	1. run swapoff -a: this will immediately disable swap
	2. remove any swap entry from /etc/fstab
	3. reboot the system. If the swap is gone, good. If, for some reason, it is still here, you had to remove the swap partition. Repeat steps 1 and 2 and, after that, use fdisk or parted to remove the (now unused) swap partition. Use great care here: removing the wrong partition will have disastrous effects!
	4. reboot

## xshell使用
	属性里面可以设置默认语言为UTF-8
	属性里可以设置外观颜色(标识出重要的连接)
	sudo apt install lrzsz安装后可以实现文件传输（拖拽上传/rz 上传/sz 下载）

## git Permission denied (github)
	ssh-keygen
	copy C:\Users\yww08/.ssh/id_rsa.pub 内容
	github.com >personal settings>SSH > New SSH Key
	[ref](https://stackoverflow.com/questions/2643502/how-to-solve-permission-denied-publickey-error-when-using-git)


?	
## 别名 alias

	alias ka='kubectl get pods -n anxinyun'
	alias
	
	在~/.bashrc文件中添加
	alias ka='kubectl get pods -n anxinyun -o wide'
	alias kla='kubectl logs -n anxinyun '
	alias ksa='kubectl get services -n anxinyun -o wide'
	alias klat='kubectl logs -n anxinyun --tail 1000'
	alias kda='kubectl delete pod -n anxinyun'
	alias ks='kubectl get pods -n savoir -o wide'
	alias kss='kubectl get services -n savoir -o wide'
	alias kls='kubectl logs -n savoir '
	alias klst='kubectl logs -n savoir --tail 1000'

## Linux <> Windows 文件互传
	rz 
	sz file

## ElasticSearch数据导出
	https://www.npmjs.com/package/elasticdump
	
	npm install elasticdump -g
	
	elasticdump \
  --input=http://iota-m2:9200/aqi_division_hour \
  --output=/home/anxin/tools/aqi_division_hour_mapping.json \
  --type=mapping

  elasticdump \
  --input=http://production.es.com:9200/my_index \
  --output=http://staging.es.com:9200/my_index \
  --type=analyzer
elasticdump \
  --input=http://production.es.com:9200/my_index \
  --output=http://staging.es.com:9200/my_index \
  --type=mapping

  curl -X PUT 'http://172.16.3.5:9200/shining_index' -d@/data/shining_index_mapping.json

  

 ## Ubuntu 16.04 安装jenkins
 https://www.jianshu.com/p/845f267aec52

首先照着输入
systemctl status jenkins.service
看到
Failed to start LSB: Start Jenkins at boot time

>> 查看是否安装jdk
>> 是否端口号重读，修改jenkins端口 https://blog.csdn.net/csfreebird/article/details/9033443

jenkins上持续集成go项目：
https://blog.csdn.net/aixiaoyang168/article/details/82965854

## HDFS
Missing Blocks
1 hdfs fsck -list-corruptfileblocks
1 hdfs fsck / | egrep -v '^\.+$' | grep -v eplica
查看上面某一个文件的情况
1 hdfs fsck /path/to/corrupt/file -locations -blocks -files

解决方法
如果文件不重要，可以直接删除此文件(hdfs fsck -delete)；或删除后重新复制一份到集群中
如果不能删除，需要从上面命令中找到发生在哪台机器上，然后到此机器上查看日志。


hadoop fs  -stat %r /anxinyun


163DORCKER
sudo nsenter --target `sudo docker inspect -f {{.State.Pid}} a699a640d325` --mount --uts --ipc --net --pid 
chown 1000:1000 /var/run/docker.sock

## k8s批量删除pod
kubectl get pods -n savoir| grep savoir-jupyter | awk '{print $1}' | xargs kubectl delete pod -n savoir

## mosquitto管理
	ps -ef | grep mosq
	
	/etc/mosquitto/mosquitto.conf

## ambari 节点上的服务 显示心跳丢失

重启ambari代理
解决方法： service ambari-agent restart

