## CEPH ODS 无法启动

>  参考 https://access.redhat.com/solutions/6999849
>
>  https://github.com/rook/rook/issues/9885

省流：最终解决办法是将Ceph中副本数由3改为2，释放了一部分空间，暂时恢复了。



```sh
debug     -4> 2024-07-08T07:59:07.904+0000 7f80ef3c93c0  1 bluefs _allocate unable to allocate 0x10000 on bdev 1, allocator name block, allocator type hybrid, capacity 0x11774196000, block size 0x1000, alloc size 0x10000, free 0xdedc09000, fragmentation 0.5334715, allocated 0x0
debug     -3> 2024-07-08T07:59:07.904+0000 7f80ef3c93c0 -1 bluefs _allocate allocation failed, needed 0x9399
debug     -2> 2024-07-08T07:59:07.904+0000 7f80ef3c93c0 -1 bluefs _flush_range_F allocated: 0x0 offset: 0x0 length: 0x9399
debug     -1> 2024-07-08T07:59:07.912+0000 7f80ef3c93c0 -1 /home/jenkins-build/build/workspace/ceph-build/ARCH/x86_64/AVAILABLE_ARCH/x86_64/AVAILABLE_DIST/centos8/DIST/centos8/MACHINE_SIZE/gigantic/release/17.2.5/rpm/el8/BUILD/ceph-17.2.5/src/os/bluestore/BlueFS.cc: In function 'int BlueFS::_flush_range_F(BlueFS::FileWriter*, uint64_t, uint64_t)' thread 7f80ef3c93c0 time 2024-07-08T07:59:07.907432+0000
/home/jenkins-build/build/workspace/ceph-build/ARCH/x86_64/AVAILABLE_ARCH/x86_64/AVAILABLE_DIST/centos8/DIST/centos8/MACHINE_SIZE/gigantic/release/17.2.5/rpm/el8/BUILD/ceph-17.2.5/src/os/bluestore/BlueFS.cc: 3137: ceph_abort_msg("bluefs enospc")

 ceph version 17.2.5 (98318ae89f1a893a6ded3a640405cdbb33e08757) quincy (stable)
```

这个错误消息表明 `Ceph` 的 `BlueStore` 文件系统在尝试进行空间分配时遇到了“没有剩余空间”（ENOSPC）的问题。即使文件系统报告有可用空间（`free 0xdedc09000`），但由于碎片化或其他原因，它无法满足分配请求。



```sh
sh-4.4$ ceph -s
  cluster:
    id:     a62a9110-a804-4dca-af81-fa8d80031926
    health: HEALTH_WARN
            4 backfillfull osd(s)
            Low space hindering backfill (add storage if this doesn't resolve itself): 89 pgs backfill_toofull
            Degraded data redundancy: 7796200/38415381 objects degraded (20.294%), 87 pgs degraded, 87 pgs undersized
            4 pool(s) backfillfull
            1130 daemons have recently crashed
            1 mgr modules have recently crashed

  services:
    mon: 3 daemons, quorum b,d,e (age 3d)
    mgr: b(active, since 2d), standbys: a
    mds: 1/1 daemons up, 1 hot standby
    osd: 5 osds: 4 up (since 3m), 4 in (since 7m); 89 remapped pgs

  data:
    volumes: 1/1 healthy
    pools:   4 pools, 177 pgs
    objects: 12.81M objects, 1.6 TiB
    usage:   4.0 TiB used, 351 GiB / 4.4 TiB avail
    pgs:     7796200/38415381 objects degraded (20.294%)
             296861/38415381 objects misplaced (0.773%)
             87 active+clean
             86 active+undersized+degraded+remapped+backfill_toofull
             2  active+remapped+backfill_toofull
             1  active+undersized+degraded+remapped+backfill_wait+backfill_toofull
             1  active+clean+scrubbing+deep

  io:
    client:   108 KiB/s rd, 35 KiB/s wr, 74 op/s rd, 11 op/s wr
```



到`ceph-tool`容器中执行：

![image-20240708174103962](C:\Users\yww08\AppData\Roaming\Typora\typora-user-images\image-20240708174103962.png)





## 横向节点扩容

拟通过增加1台服务器作为Ceph节点加入，提高整体存储容量。

修改`cluster.yaml`:

```yaml
 nodes:
      - name: "node-01"
        devices:
          - name: "sda"
      ...
      - name: "ck-master"
        devices:
          - name: "sda"
```



在修改`Cluster.yaml`后增加节点

```
CephBlockPool/rook-ceph/ceph-blockpool dry-run failed, reason: InternalError, error: Internal error occurred: failed calling webhook "cephblockpool-wh-rook-ceph-admission-controller-rook-ceph.rook.io": failed to call webhook: Post "https://rook-ceph-admission-controller.rook-ceph.svc:443/validate-ceph-rook-io-v1-cephblockpool?timeout=5s": x509: certificate signed by unknown authority (possibly because of "x509: invalid signature: parent certificate cannot sign this kind of certificate" while trying to verify candidate authority certificate "serial:336976302276413441245920813253687075705")
```

这个错误解决办法：https://github.com/rook/rook/issues/10719

```sh
 kubectl delete secret -n rook-ceph rook-ceph-admission-controller

```

等待operator自动重启后正常了。



进入cep-tool使用查看:

```sh
sh-4.4$ ceph osd df
ID  CLASS  WEIGHT   REWEIGHT  SIZE     RAW USE   DATA     OMAP     META     AVAIL    %USE   VAR   PGS  STATUS
 0    hdd  1.09160   1.00000  1.1 TiB   1.0 TiB  1.0 TiB  3.8 GiB  9.8 GiB   73 GiB  93.51  1.01  105      up
 2    hdd  1.09160   1.00000  1.1 TiB   1.0 TiB  1.0 TiB  6.2 GiB  8.9 GiB   65 GiB  94.21  1.02   17      up
 1    hdd  1.09160   1.00000  1.1 TiB   1.0 TiB  1.0 TiB  4.0 GiB  9.4 GiB   56 GiB  94.97  1.03  103      up
 4    hdd  1.09160   1.00000  1.1 TiB  1007 GiB  992 GiB  5.5 GiB  9.3 GiB  111 GiB  90.04  0.97  106      up
 3    hdd  1.09160   1.00000  1.1 TiB  1007 GiB  992 GiB  6.1 GiB  9.4 GiB  110 GiB  90.12  0.97  113      up
                       TOTAL  5.5 TiB   5.1 TiB  5.0 TiB   26 GiB   47 GiB  415 GiB  92.57
MIN/MAX VAR: 0.97/1.03  STDDEV: 2.08
sh-4.4$ ceph osd tree
ID   CLASS  WEIGHT   TYPE NAME         STATUS  REWEIGHT  PRI-AFF
 -1         5.45799  root default
 -3         1.09160      host node-01
  0    hdd  1.09160          osd.0         up   1.00000  1.00000
 -9         1.09160      host node-02
  2    hdd  1.09160          osd.2         up   1.00000  1.00000
 -5         1.09160      host node-03
  1    hdd  1.09160          osd.1         up   1.00000  1.00000
-11         1.09160      host node-04
  4    hdd  1.09160          osd.4         up   1.00000  1.00000
 -7         1.09160      host node-05
  3    hdd  1.09160          osd.3         up   1.00000  1.00000
```



**列出所有 Pod 并查找引用 PVC 的 Pod**:

```
sh
复制代码
kubectl get pods --all-namespaces -o jsonpath="{range .items[*]}{.metadata.name}{'\t'}{.metadata.namespace}{'\t'}{.spec.volumes[*].persistentVolumeClaim.claimName}{'\n'}{end}" | grep <pvc-name>
```

**注意**: 将 `<pvc-name>` 替换为你的 PVC 名称。

**找到管理 Pod 的控制器**：

1. 列出所有 Pod：

   ```sh
   kubectl get pods --all-namespaces
   ```
   
2. 查看 Pod 的详细信息以找到控制器：

   ```sh
   kubectl describe pod <pod-name> -n <namespace>
   ```
   
   在输出的详细信息中，查找 `Controlled By` 字段。例如：

   ```tex
Name:         my-pod
   Namespace:    default
   ...
   Controlled By:  ReplicaSet/my-replicaset
   ```
   
   **删除管理 Pod 的控制器**： 根据找到的控制器类型（Deployment、`ReplicaSet`、`StatefulSet` 等），删除相应的控制器。

   **删除 Deployment**:

   ```sh
kubectl delete deployment <deployment-name> -n <namespace>
   ```
   
   **删除 ReplicaSet**:
   
   ```sh
kubectl delete replicaset <replicaset-name> -n <namespace>
   ```

   **删除 StatefulSet**:
   
   ```sh
   kubectl delete statefulset <statefulset-name> -n <namespace>
   ```

   **删除 DaemonSet**:

   ```sh
   kubectl delete daemonset <daemonset-name> -n <namespace>
   ```
   
   使用 Rook Ceph 工具查看 Ceph OSD 的状态，以确保存储空间已被回收。

   ```sh
kubectl -n rook-ceph exec -it $(kubectl -n rook-ceph get pod -l app=rook-ceph-tools -o jsonpath='{.items[0].metadata.name}') -- ceph status
   
   kubectl -n rook-ceph exec -it $(kubectl -n rook-ceph get pod -l app=rook-ceph-tools -o jsonpath='{.items[0].metadata.name}') -- ceph df
   
   
   root@jumper-02:/home/cloud# kubectl -n rook-ceph exec -it $(kubectl -n rook-ceph get pod -l app=rook-ceph-tools -o jsonpath='{.items[0].metadata.name}') -- ceph osd status

   ID  HOST      USED  AVAIL  WR OPS  WR DATA  RD OPS  RD DATA  STATE
 0  node-01  1045G  72.7G      0        0       1       90   backfillfull,exists,up
    1  node-03  1061G  55.8G      0        0       1     1531   exists,full,up
    2  node-02  1053G  64.4G      0        0       0        0   backfillfull,exists,up
    3  node-05  1007G   110G      0        0       0        0   backfillfull,exists,up
    4  node-04  1006G   111G      0        0       0        3   backfillfull,exists,up
   root@jumper-02:/home/cloud# kubectl -n rook-ceph exec -it $(kubectl -n rook-ceph get pod -l app=rook-ceph-tools -o jsonpath='{.items[0].metadata.name}') -- ceph osd tree
   ID   CLASS  WEIGHT   TYPE NAME         STATUS  REWEIGHT  PRI-AFF
    -1         5.45799  root default
    -3         1.09160      host node-01
     0    hdd  1.09160          osd.0         up   1.00000  1.00000
    -9         1.09160      host node-02
     2    hdd  1.09160          osd.2         up   1.00000  1.00000
    -5         1.09160      host node-03
     1    hdd  1.09160          osd.1         up   1.00000  1.00000
   -11         1.09160      host node-04
     4    hdd  1.09160          osd.4         up   1.00000  1.00000
    -7         1.09160      host node-05
     3    hdd  1.09160          osd.3         up   1.00000  1.00000
   root@jumper-02:/home/cloud#  kubectl get pod -nrook-ceph -o wide
   NAME                                                READY   STATUS      RESTARTS        AGE     IP                NODE        NOMINATED NODE   READINESS GATES
   csi-cephfsplugin-542zm                              2/2     Running     0               18m     10.8.40.141       ck-master   <none>           <none>
   csi-cephfsplugin-5wq75                              2/2     Running     8 (48d ago)     320d    10.8.40.131       node-06     <none>           <none>
   csi-cephfsplugin-5xgzv                              2/2     Running     4 (229d ago)    320d    10.8.40.130       node-05     <none>           <none>
   csi-cephfsplugin-8dn2p                              2/2     Running     6 (53d ago)     320d    10.8.40.133       node-02     <none>           <none>
   csi-cephfsplugin-c8mxx                              2/2     Running     8 (53d ago)     320d    10.8.40.128       node-03     <none>           <none>
   csi-cephfsplugin-provisioner-7cc6b9c99-sdx9s        5/5     Running     5 (229d ago)    285d    100.160.173.195   node-05     <none>           <none>
   csi-cephfsplugin-provisioner-7cc6b9c99-tg8jv        5/5     Running     0               48d     100.160.184.5     node-02     <none>           <none>
   csi-cephfsplugin-th8wc                              2/2     Running     6 (229d ago)    320d    10.8.40.127       node-04     <none>           <none>
   csi-cephfsplugin-w9tht                              2/2     Running     6 (53d ago)     320d    10.8.40.134       node-01     <none>           <none>
   csi-rbdplugin-bws67                                 2/2     Running     14 (53d ago)    545d    10.8.40.134       node-01     <none>           <none>
   csi-rbdplugin-cjm4m                                 2/2     Running     16 (53d ago)    545d    10.8.40.128       node-03     <none>           <none>
   csi-rbdplugin-f7fbw                                 2/2     Running     16 (48d ago)    545d    10.8.40.131       node-06     <none>           <none>
   csi-rbdplugin-g2tnl                                 2/2     Running     14 (229d ago)   545d    10.8.40.127       node-04     <none>           <none>
   csi-rbdplugin-kpnfb                                 2/2     Running     0               8m40s   10.8.40.141       ck-master   <none>           <none>
   csi-rbdplugin-p5j7p                                 2/2     Running     14 (229d ago)   545d    10.8.40.130       node-05     <none>           <none>
   csi-rbdplugin-provisioner-5486664c96-7llvl          5/5     Running     4 (41d ago)     48d     100.160.227.9     node-04     <none>           <none>
   csi-rbdplugin-provisioner-5486664c96-lmhwr          5/5     Running     0               48d     100.160.184.181   node-02     <none>           <none>
   csi-rbdplugin-zxcq4                                 2/2     Running     14 (53d ago)    545d    10.8.40.133       node-02     <none>           <none>
   rook-ceph-crashcollector-node-01-55bfc58d54-4j92t   1/1     Running     0               43d     100.160.190.1     node-01     <none>           <none>
   rook-ceph-crashcollector-node-02-c74cd7787-wqsjx    1/1     Running     0               48d     100.160.184.43    node-02     <none>           <none>
   rook-ceph-crashcollector-node-03-7c656f8b64-hmh2x   1/1     Running     3 (53d ago)     319d    100.160.254.83    node-03     <none>           <none>
   rook-ceph-crashcollector-node-04-566d45795f-p4tr7   1/1     Running     1 (229d ago)    281d    100.160.227.4     node-04     <none>           <none>
   rook-ceph-crashcollector-node-05-7d78cf748-rzzll    1/1     Running     0               202d    100.160.173.216   node-05     <none>           <none>
   rook-ceph-mds-myfs-a-597b48fb8c-jshr5               2/2     Running     3 (62d ago)     202d    100.160.174.26    node-05     <none>           <none>
   rook-ceph-mds-myfs-b-d49f7945d-892s2                2/2     Running     1 (4d2h ago)    48d     100.160.184.7     node-02     <none>           <none>
   rook-ceph-mgr-a-7fc4bcc88b-rbr2d                    3/3     Running     0               5d20h   100.160.190.22    node-01     <none>           <none>
   rook-ceph-mgr-b-68c794b8bb-4rndl                    3/3     Running     1 (2d20h ago)   49d     100.160.184.9     node-02     <none>           <none>
   rook-ceph-mon-b-68d55cc69-trxjk                     2/2     Running     16 (53d ago)    545d    100.160.254.84    node-03     <none>           <none>
   rook-ceph-mon-d-7c6f976fcf-4l56m                    2/2     Running     0               43d     100.160.190.42    node-01     <none>           <none>
   rook-ceph-mon-e-7577548647-9tbjb                    2/2     Running     0               48d     100.160.184.22    node-02     <none>           <none>
   rook-ceph-operator-f45cb444c-lz7v9                  1/1     Running     5 (21h ago)     48d     100.160.174.60    node-05     <none>           <none>
   rook-ceph-osd-0-65f679cbf-bdt8r                     2/2     Running     0               43d     100.160.190.17    node-01     <none>           <none>
   rook-ceph-osd-1-f9fb9b548-d5clr                     2/2     Running     0               20h     100.160.254.156   node-03     <none>           <none>
   rook-ceph-osd-2-686785cdd-l48wp                     2/2     Running     2 (20h ago)     20h     100.160.184.38    node-02     <none>           <none>
   rook-ceph-osd-3-5569bcb6c4-c6h9g                    2/2     Running     6 (229d ago)    360d    100.160.173.231   node-05     <none>           <none>
   rook-ceph-osd-4-58cc5f5b99-wvx8c                    2/2     Running     5 (41d ago)     319d    100.160.227.56    node-04     <none>           <none>
   rook-ceph-osd-prepare-node-01-s8p74                 0/1     Completed   0               21h     100.160.190.43    node-01     <none>           <none>
   rook-ceph-osd-prepare-node-02-hrf4z                 0/1     Completed   0               21h     100.160.184.15    node-02     <none>           <none>
   rook-ceph-osd-prepare-node-03-5qnds                 0/1     Completed   0               21h     100.160.254.184   node-03     <none>           <none>
   rook-ceph-osd-prepare-node-04-psn6x                 0/1     Completed   0               21h     100.160.227.8     node-04     <none>           <none>
   rook-ceph-osd-prepare-node-05-7ldhr                 0/1     Completed   0               21h     100.160.173.207   node-05     <none>           <none>
   rook-ceph-tools-54bdbfc7b7-c5hfk                    1/1     Running     0               53d     100.160.174.24    node-05     <none>           <none>
   
   ```
   
   

## 单机磁盘扩容：

https://blog.51cto.com/u_14458428/9017267

排查过程：

```sh
root@jumper-02:/home/cloud# kubectl -n rook-ceph exec -it $(kubectl -n rook-ceph get pod -l app=rook-ceph-tools -o jsonpath='{.items[0].metadata.name}') -- ceph status
  cluster:
    id:     a62a9110-a804-4dca-af81-fa8d80031926
    health: HEALTH_ERR
            4 backfillfull osd(s)
            1 full osd(s)
            Low space hindering backfill (add storage if this doesn't resolve itself): 95 pgs backfill_toofull
            Degraded data redundancy: 7798716/38428311 objects degraded (20.294%), 87 pgs degraded, 87 pgs undersized
            8 pgs not deep-scrubbed in time
            4 pool(s) full
            1620 daemons have recently crashed
            1 mgr modules have recently crashed

  services:
    mon: 3 daemons, quorum b,d,e (age 5d)
    mgr: b(active, since 3d), standbys: a
    mds: 1/1 daemons up, 1 hot standby
    osd: 5 osds: 5 up (since 43h), 5 in (since 43h); 95 remapped pgs

  data:
    volumes: 1/1 healthy
    pools:   4 pools, 177 pgs
    objects: 12.81M objects, 1.6 TiB
    usage:   5.1 TiB used, 415 GiB / 5.5 TiB avail
    pgs:     7798716/38428311 objects degraded (20.294%)
             541352/38428311 objects misplaced (1.409%)
             87 active+undersized+degraded+remapped+backfill_toofull
             82 active+clean
             8  active+remapped+backfill_toofull

  io:
    client:   6.6 KiB/s rd, 3.3 KiB/s wr, 2 op/s rd, 0 op/s wr

root@jumper-02:/home/cloud# kubectl -n rook-ceph exec -it $(kubectl -n rook-ceph get pod -l app=rook-ceph-tools -o jsonpath='{.items[0].metadata.name}') -- ceph df
--- RAW STORAGE ---
CLASS     SIZE    AVAIL     USED  RAW USED  %RAW USED
hdd    5.5 TiB  415 GiB  5.1 TiB   5.1 TiB      92.58
TOTAL  5.5 TiB  415 GiB  5.1 TiB   5.1 TiB      92.58

--- POOLS ---
POOL             ID  PGS   STORED  OBJECTS     USED   %USED  MAX AVAIL
.mgr              1    1   10 MiB        3   31 MiB  100.00        0 B
replicapool       2   32     19 B        1   12 KiB  100.00        0 B
myfs-metadata     3   16   11 GiB  150.53k   27 GiB  100.00        0 B
myfs-replicated   4  128  2.0 TiB   12.66M  5.0 TiB  100.00        0 B

root@jumper-02:/home/cloud# kubectl -n rook-ceph exec -it $(kubectl -n rook-ceph get pod -l app=rook-ceph-tools -o jsonpath='{.items[0].metadata.name}') -- ceph osd df
ID  CLASS  WEIGHT   REWEIGHT  SIZE     RAW USE   DATA     OMAP     META     AVAIL    %USE   VAR   PGS  STATUS
 0    hdd  1.09160   1.00000  1.1 TiB   1.0 TiB  1.0 TiB  3.8 GiB  9.6 GiB   73 GiB  93.50  1.01  105      up
 2    hdd  1.09160   1.00000  1.1 TiB   1.0 TiB  1.0 TiB  6.2 GiB  9.3 GiB   64 GiB  94.24  1.02   17      up
 1    hdd  1.09160   1.00000  1.1 TiB   1.0 TiB  1.0 TiB  4.3 GiB  9.7 GiB   56 GiB  95.03  1.03  103      up
 4    hdd  1.09160   1.00000  1.1 TiB  1007 GiB  992 GiB  5.5 GiB  9.5 GiB  111 GiB  90.05  0.97  106      up
 3    hdd  1.09160   1.00000  1.1 TiB  1007 GiB  992 GiB  6.1 GiB  9.0 GiB  111 GiB  90.08  0.97  113      up
                       TOTAL  5.5 TiB   5.1 TiB  5.0 TiB   26 GiB   47 GiB  415 GiB  92.58
MIN/MAX VAR: 0.97/1.03  STDDEV: 2.11

root@jumper-02:/home/cloud# kubectl -n rook-ceph exec -it $(kubectl -n rook-ceph get pod -l app=rook-ceph-tools -o jsonpath='{.items[0].metadata.name}') -- ceph osd status
ID  HOST      USED  AVAIL  WR OPS  WR DATA  RD OPS  RD DATA  STATE
 0  node-01  1045G  72.7G      0        0       1       90   backfillfull,exists,up
 1  node-03  1062G  55.5G      0        0       0        0   exists,full,up
 2  node-02  1053G  64.3G      0        0       0        0   backfillfull,exists,up
 3  node-05  1006G   110G      0        0       1       16   backfillfull,exists,up
 4  node-04  1006G   111G      0        0       0        0   backfillfull,exists,up

```

![image-20240710131301753](imgs/CEPH 无法启动/image-20240710131301753.png)



```sh
docker pull docker.io/calico/pod2daemon-flexvol:v3.24.5
docker pull openkruise/kruise-manager:v1.4.0
```



Jenkins容器中无法正确访问Nexus

![image-20240710202551873](imgs/CEPH 无法启动/image-20240710202551873.png)



查看该容器下的/etc/hosts 看到nexus指向两个ip地址

修改coredns

```sh
 kubectl edit configmap coredns -n kube-system
```



Nexus报错:

```
2024-07-10T21:03:08.745775026+08:00 2024-07-10 13:03:08,744+0000 ERROR [qtp1100486876-1340] *UNKNOWN org.sonatype.nexus.blobstore.file.FileBlobStore - Unable to load BlobAttributes for blob id: 60011fdd-252b-4e0c-a45f-3b614c6863eb, path: /nexus-data/blobs/default/content/vol-07/chap-25/60011fdd-252b-4e0c-a45f-3b614c6863eb.properties, exception: null

2024-07-10T21:03:08.747025111+08:00 2024-07-10 13:03:08,746+0000 WARN  [qtp1100486876-1340] *UNKNOWN org.sonatype.nexus.repository.httpbridge.internal.ViewServlet - Failure servicing: GET /repository/fs-npm/string_decoder/-/string_decoder-1.3.0.tgz

2024-07-10T21:03:08.747036024+08:00 org.sonatype.nexus.repository.MissingBlobException: Blob default@5278F4C3-BCFBB109-411FE1EC-FB20F1AF-A94AFA88:60011fdd-252b-4e0c-a45f-3b614c6863eb exists in metadata, but is missing from the blobstore
```



在nexus界面中执行如下：

```sh
更新元数据：

使用 Nexus 的修复工具（Repair - Reconcile component database from blob store）来修复元数据和 blob 存储的一致性。
在 Nexus 管理界面，导航到 Admin > System > Tasks，创建一个新的任务选择 Repair - Reconcile component database from blob store，然后运行该任务。
```

还是没有解决问题

修改nexus中`npm` group只代理`npm-old`后 ，能解决部分构建，

![img](imgs/CEPH 无法启动/企业微信截图_17206795963863.png)