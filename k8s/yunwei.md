```sh
#驱逐（默认也会执行cordon
kubectl drain node

#允许调度
kubectl uncordon node

#删除节点
kubectl delete no

#READD节点
 kubeadm reset && kubeadm join 
```

