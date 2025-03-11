\#数据库状态查看
pg_ctl status
\#数据库启动命令
/opt/pg12/bin/pg_ctl -D /opt/data5432 start &
\#查看数据库日志
cd /opt/data5432/pg_log
tail -n 10 postgres-xx.csv
\#服务启动失败的话，则可以修改数据库配置连接数2000可以改至5000
vi /opt/data5432/postgresql.conf
\#重新启动
/opt/pg12/bin/pg_ctl -D /opt/data5432/ start





ip addr |grep 10.8.40
效果：223有没有在n2主数据库上
  inet 10.8.40.112/24 brd 10.8.40.255 scope global eno4
  inet 10.8.40.223/24 scope global secondary eno4这样看下呢

![img](file:///C:/Users/yww08/Documents/WXWork/1688858167337977/Cache/Image/2024-11/企业微信截图_17326067508714.png)





 /opt/pgsql/bin/pg_ctl -D /mnt/data/data5432 start &

