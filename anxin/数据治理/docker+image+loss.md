## 问题：

pause-amd64镜像丢失，导致重新启动无法从google下载，需要重新tag本地镜像

K8S的垃圾回收机制参考这篇文章：https://kubernetes.io/docs/concepts/architecture/garbage-collection/#containers-images



以下是docker的回收机制分析试验

```sh
# 查看镜像、容器、数据卷所占用的空间 (可回收空间均为0)
root@iota-n3:/var/lib/docker# docker system df
TYPE                TOTAL               ACTIVE              SIZE                RECLAIMABLE
Images              5                   5                   466.3MB             0B (0%)
Containers          57                  52                  100.2kB             0B (0%)
Local Volumes       0                   0                   0B                  0B
# 查看详细
docker system df -v

```



通过crontab -l 查看定时任务：无



未找到原因 @TODO