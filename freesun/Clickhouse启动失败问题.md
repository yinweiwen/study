## Clickhouse启动失败问题

### 问题排查

启动时很多日志：

```...sh
2024.07.16 17:01:33.269576 [ 3636404 ] {} <Debug> alarm.alarms (e9508ff5-a59b-4cfa-a38a-a7cdd5f9735a): Loading mutation: mutation_59983739.txt entry, commands size: 1
...
```



太多的mutation操作文件，导致alarm库下的alarms表启动时间过长，加载超过服务启动超时时间

`vi /lib/systemd/system/clickhouse-server.service`

```sh
[Unit]
Description=ClickHouse Server (analytic DBMS for big data)
Requires=network-online.target
# NOTE: that After/Wants=time-sync.target is not enough, you need to ensure
# that the time was adjusted already, if you use systemd-timesyncd you are
# safe, but if you use ntp or some other daemon, you should configure it
# additionaly.
After=time-sync.target network-online.target
Wants=time-sync.target

[Service]
Type=notify

# NOTE: we leave clickhouse watchdog process enabled to be able to see OOM/SIGKILL traces in clickhouse-server.log files.
# If you wish to disable the watchdog and rely on systemd logs just add "Environment=CLICKHOUSE_WATCHDOG_ENABLE=0" line.
User=clickhouse
Group=clickhouse
Restart=always
RestartSec=30
TimeoutStartSec=12000
RuntimeDirectory=clickhouse-server
ExecStart=/usr/bin/clickhouse-server --config=/etc/clickhouse-server/config.xml --pid-file=/run/clickhouse-server/clickhouse-server.pid
# Minus means that this file is optional.
EnvironmentFile=-/etc/default/clickhouse
LimitCORE=infinity
LimitNOFILE=500000
CapabilityBoundingSet=CAP_NET_ADMIN CAP_IPC_LOCK CAP_SYS_NICE CAP_NET_BIND_SERVICE

[Install]
# ClickHouse should not start from the rescue shell (rescue.target).
WantedBy=multi-user.target
	
	
	
```





> ClickHouse 中的 mutation 操作(update/delete) 默认是异步执行的, 这会导致一种情况的出现: 删除的数据在一段时间内还能查询到. 在非事务性的使用场景中这个设置可以加快处理速度, 并且不会影响后来数据的添加, 但在要求事务性的使用场景中(比如新增数据依赖历史数据), 这个设置会导致后加的数据出现错误.
>
> 　　解决方法：在 ClickHouse 的配置文件中加入 mutation_sync 参数, 令其等于 1 或 2
>
> 　　mutation_sync 参数默认为 0, 也就是 mutation 操作异步执行, 设为 1 会在当前机器上阻塞直到操作完成, 设为 2 会一直阻塞直到操作在所有数据副本上执行完成, 主要用于集群。
>
> 　　修改 mutation_sync 这个参数的方法很简单, 如果只是为了临时修改一下, 在启动客户端命令时加入 -mutations_sync=1 就可以了.
>
> 
>
> 以数据删除为例：数据的删除过程，是以数据表的每个分区目录为单位，将所有目录重写为新的目录。数据在重写的过程中会将需要删除的数据移除掉。旧的数据目录并不会立即删除，而是会被标记为非激活状态（active为0）。当到MergeTree引擎下一次合并动作触发时，这些非激活目录才会被真正的物理删除。
>
> 因此，删除和更新操作，是**一个很重**的操作。不适合单条处理。



准备删除mutation_文件

```sh
 rm /home/clickhouse/data/alarm/alarms/mutation_*

bash: /usr/bin/rm: Argument list too long

```

文件太多删除不动

```sh
cd /home/clickhouse/data/alarm/alarms & find -name 'mutation_*' -type f -delete

```

还是删不动

```sh

root@node-13:/home/clickhouse/data/alarm/alarms# ls | wc -l
2345285
root@node-13:/home/clickhouse/data/alarm# rm -rf alarms/

```



### 修改进程去除update操作

1. 派单进程
2. 告警进程





Zookeeper挂掉

```
2024-08-08 16:22:36,643 [myid:] - ERROR [SyncThread:1:o.a.z.s.ZooKeeperCriticalThread@49] - Severe unrecoverable error, from thread : SyncThread:1
java.io.IOException: 设备上没有空间
        at java.base/java.io.FileOutputStream.writeBytes(Native Method)
        at java.base/java.io.FileOutputStream.write(FileOutputStream.java:349)
        at java.base/java.io.BufferedOutputStream.flushBuffer(BufferedOutputStream.java:81)
        at java.base/java.io.BufferedOutputStream.flush(BufferedOutputStream.java:142)
        at org.apache.zookeeper.server.persistence.FileTxnLog.commit(FileTxnLog.java:378)
        at org.apache.zookeeper.server.persistence.FileTxnSnapLog.commit(FileTxnSnapLog.java:598)
        at org.apache.zookeeper.server.ZKDatabase.commit(ZKDatabase.java:659)
        at org.apache.zookeeper.server.SyncRequestProcessor.flush(SyncRequestProcessor.java:235)
        at org.apache.zookeeper.server.SyncRequestProcessor.run(SyncRequestProcessor.java:169)
2024-08-08 16:22:36,660 [myid:] - ERROR [Snapshot Thread:o.a.z.s.ZooKeeperServer@552] - Severe unrecoverable error, exiting
java.io.IOException: 设备上没有空间
        at java.base/java.io.FileOutputStream.writeBytes(Native Method)
        at java.base/java.io.FileOutputStream.write(FileOutputStream.java:349)
        at java.base/java.io.BufferedOutputStream.flushBuffer(BufferedOutputStream.java:81)
        at java.base/java.io.BufferedOutputStream.write(BufferedOutputStream.java:127)
        at java.base/java.util.zip.CheckedOutputStream.write(CheckedOutputStream.java:73)
        at java.base/java.io.DataOutputStream.write(DataOutputStream.java:112)
        at org.apache.jute.BinaryOutputArchive.writeString(BinaryOutputArchive.java:112)
        at org.apache.zookeeper.server.DataTree.serializeNodeData(DataTree.java:1346)
        at org.apache.zookeeper.server.DataTree.serializeNode(DataTree.java:1331)
        at org.apache.zookeeper.server.DataTree.serializeNode(DataTree.java:1340)
        at org.apache.zookeeper.server.DataTree.serializeNode(DataTree.java:1340)
        at org.apache.zookeeper.server.DataTree.serializeNode(DataTree.java:1340)
        at org.apache.zookeeper.server.DataTree.serializeNode(DataTree.java:1340)
        at org.apache.zookeeper.server.DataTree.serializeNode(DataTree.java:1340)
        at org.apache.zookeeper.server.DataTree.serializeNode(DataTree.java:1340)
        at org.apache.zookeeper.server.DataTree.serializeNode(DataTree.java:1340)
        at org.apache.zookeeper.server.DataTree.serializeNode(DataTree.java:1340)
        at org.apache.zookeeper.server.DataTree.serializeNode(DataTree.java:1340)
        at org.apache.zookeeper.server.DataTree.serializeNodes(DataTree.java:1355)
        at org.apache.zookeeper.server.DataTree.serialize(DataTree.java:1365)
        at org.apache.zookeeper.server.util.SerializeUtils.serializeSnapshot(SerializeUtils.java:171)
        at org.apache.zookeeper.server.persistence.FileSnap.serialize(FileSnap.java:226)
        at org.apache.zookeeper.server.persistence.FileSnap.serialize(FileSnap.java:245)
        at org.apache.zookeeper.server.persistence.FileTxnSnapLog.save(FileTxnSnapLog.java:481)
        at org.apache.zookeeper.server.ZooKeeperServer.takeSnapshot(ZooKeeperServer.java:550)
        at org.apache.zookeeper.server.ZooKeeperServer.takeSnapshot(ZooKeeperServer.java:544)
        at org.apache.zookeeper.server.SyncRequestProcessor$1.run(SyncRequestProcessor.java:193)
2024-08-08 16:22:36,818 [myid:] - INFO  [SyncThread:1:o.a.z.s.ZooKeeperServerListenerImpl@43] - Thread SyncThread:1 exits, error code 1
2024-08-08 16:22:36,823 [myid:] - INFO  [SyncThread:1:o.a.z.s.SyncRequestProcessor@224] - SyncRequestProcessor exited!
2024-08-08 16:22:36,991 [myid:] - ERROR [Snapshot Thread:o.a.z.u.ServiceUtils@48] - Exiting JVM with code 10
2024-08-08 16:22:37,067 [myid:] - INFO  [QuorumPeer[myid=1](plain=[0:0:0:0:0:0:0:0]:2181)(secure=disabled):o.a.z.s.q.Follower@142] - Disconnected from leader (with address: zk-n2/192.168.1.106:2888). Was connected for 503770729ms. Sync state: true
2024-08-08 16:22:37,089 [myid:] - INFO  [QuorumPeer[myid=1](plain=[0:0:0:0:0:0:0:0]:2181)(secure=disabled):o.a.z.s.q.Follower@286] - shutdown Follower
2024-08-08 16:22:37,097 [myid:] - INFO  [QuorumPeer[myid=1](plain=[0:0:0:0:0:0:0:0]:2181)(secure=disabled):o.a.z.s.ZooKeeperServer@853] - shutting down
2024-08-08 16:22:37,100 [myid:] - INFO  [QuorumPeer[myid=1](plain=[0:0:0:0:0:0:0:0]:2181)(secure=disabled):o.a.z.s.RequestThrottler@256] - Shutting down
2024-08-08 16:22:37,108 [myid:] - INFO  [RequestThrottler:o.a.z.s.RequestThrottler@217] - Draining request throttler queue
2024-08-08 16:22:37,109 [myid:] - INFO  [RequestThrottler:o.a.z.s.RequestThrottler@195] - RequestThrottler shutdown. Dropped 0 requests
2024-08-08 16:22:37,109 [myid:] - INFO  [QuorumPeer[myid=1](plain=[0:0:0:0:0:0:0:0]:2181)(secure=disabled):o.a.z.s.q.FollowerRequestProcessor@172] - Shutting down
2024-08-08 16:22:37,109 [myid:] - INFO  [QuorumPeer[myid=1](plain=[0:0:0:0:0:0:0:0]:2181)(secure=disabled):o.a.z.s.q.CommitProcessor@631] - Shutting down
2024-08-08 16:22:37,109 [myid:] - INFO  [CommitProcessor:1:o.a.z.s.q.CommitProcessor@419] - CommitProcessor exited loop!
```



磁盘占用过高

设置zoo.conf重启zookeeper:

```sh
# 保留的快照数量
autopurge.snapRetainCount=3
# 设置自动清理日志的保留天数
autopurge.purgeInterval=1

```

