### Streamlit







附录：

在k8s容器中执行报错信息

```
2024-09-19T09:41:58.748241013+08:00     raise OSError(errno.EMFILE, "inotify instance limit reached")
```

执行（在宿主机node-06）:

```sh
root@node-06:~# cat /proc/sys/fs/inotify/max_user_instances
128
root@node-06:~# sudo sysctl fs.inotify.max_user_instances=8192
fs.inotify.max_user_instances = 8192

```

