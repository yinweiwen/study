## 记一次ES性能问题

（2024.11）

ES 查询慢。部分索引呈现YELLOW情况。

临时解决方法：重启n3上的ES节点、

```json
PUT _cluster/settings
{
 "persistent": {
 "cluster.routing.allocation.enable": "none",
 "cluster.routing.rebalance.enable": "none",
 "cluster.routing.allocation.allow_rebalance": "indices_primaries_active"
 }
}

POST _flush/synced

# sudo systemctl stop elasticsearch.service
# sudo systemctl start elasticsearch.service

PUT _cluster/settings
{
 "persistent": {
 "cluster.routing.allocation.enable": "primaries",
 "cluster.routing.rebalance.enable": "primaries",
 "cluster.routing.allocation.allow_rebalance": "indices_primaries_active"
 }
}


PUT _cluster/settings
{
 "persistent": {
 "cluster.routing.rebalance.enable": "all",
 "cluster.routing.allocation.enable": "all",
 "cluster.routing.allocation.allow_rebalance": "indices_all_active"
 }
}
```



![img](imgs/记一次ES性能问题/企业微信截图_17331061397887.png)

排查发现，出现部分副本分片unsigned的情况。

> 在 Elasticsearch 中，分片的状态会被标记为黄色（yellow）或红色（red）。当分片的状态是 黄色 时，意味着某些分片未能在所有的节点上复制，而是只有主分片在当前节点上存在，副本分片丢失。这通常不会影响读取操作，但会降低数据的容错性。
>
> 当分片的状态是 红色 时，表示某些分片完全不可用，可能是因为主分片和副本分片都不可用，导致无法访问该索引的任何数据。
>
> 发现分片不可用的原因：
> N3节点的分片不可用：如果所有的分片在 es-n3 上都不可用，可能是以下原因之一：
>
> es-n3 节点本身离线或者未加入集群。
> 存储设备问题，导致无法加载或恢复分片。
> 由于磁盘空间不足，导致 Elasticsearch 无法分配或复制分片。
> 网络问题，导致 es-n3 无法与其他节点正常通信。
> 副本分片问题：如果 es-n3 节点上原本应该有副本的分片未能成功分配，可能会导致分片处于黄色或红色状态。

执行手动分配：

```json

POST /_cluster/reroute
{
  "commands": [
    {
      "allocate_replica": {
        "index": "savoir_themes",
        "shard": 2,
        "node": "es-n3"
      }
    }
  ]
}
```

出现报错：

```json
{
  "error": {
    "root_cause": [
      {
        "type": "remote_transport_exception",
        "reason": "[es-n5][10.8.40.14:9300][cluster:admin/reroute]"
      }
    ],
    "type": "illegal_argument_exception",
    "reason": "[allocate_replica] allocation of [savoir_themes][2] on node {es-n1}{cqYS88zIS02N4GXRYxpbbg}{hSt14YlESeSv8gX3yiQ4Sg}{10.8.40.10}{10.8.40.10:9300}{xpack.installed=true} is not allowed, reason: [YES(shard has no previous failures)][YES(primary shard for this replica is already active)][YES(explicitly ignoring any disabling of allocation due to manual allocation commands via the reroute API)][NO(cannot allocate replica shard to a node with version [6.8.2] since this is older than the primary version [6.8.23])][YES(the shard is not being snapshotted)][YES(ignored as shard is not being recovered from a snapshot)][YES(node passes include/exclude/require filters)][YES(the shard does not exist on the same host)][YES(enough disk for shard on node, free: [8tb], shard size: [0b], free after allocating shard: [8tb])][YES(below shard recovery limit of outgoing: [0 < 100] incoming: [0 < 100])][YES(the shard count [362] for this node is under the index limit [4] and cluster level node limit [-1])][YES(allocation awareness is not enabled, set cluster setting [cluster.routing.allocation.awareness.attributes] to enable it)]"
  },
  "status": 400
}
```

具体原因是，您尝试将副本分配到的节点 `es-n1` 的版本（`6.8.2`）较旧，无法与主分片的版本（`6.8.23`）兼容。

原来是上次ES-n6加入后出现的**版本不兼容**问题。



ES-N6加入后部分数据的副本分片跑到N6上。 重启n3节点后，N6上部分分片变成主分片。然后n3再启动后，数据从N6->N3,高版本到低版本分片副本，这是禁止的。所以出现这个问题。



解决办法：

升级所有节点 从 6.8.2 -=> 6.8.23



```sh
es-n1~es-n5  cloud/Fas_123
es-n6 root/root
程序路径：

[root@cloud-es-n3 cloud]# sudo systemctl stop elasticsearch.service
[root@cloud-es-n3 cloud]# mv /usr/share/elasticsearch /usr/share/elasticsearch_bk
[root@cloud-es-n3 cloud]# scp -r root@es-n6:/usr/share/elasticsearch /usr/share/elasticsearch
[root@cloud-es-n3 cloud]# sudo systemctl start elasticsearch.service

```



> 如果重启后出现副本没有自动迁移上去，使用reroute命令手动触发。