ambari安装

计划：

> 192.168.1.100 ck-n1  MASTER*
>
> 192.168.1.101 ck-n2  SLAVER1
>
> 192.168.1.102 ck-n3   SLAVER2
>
> 192.168.1.103 ck-n4   SLAVER3



准备：

配置免密登录





在原ambari服务器中将相关资源拷贝过来

```sh
scp -r services/ cloud@10.8.40.141:/home/cloud/ambari/
# 141
scp -r services/ diantong@ck-n1:/home/diantong/
```

