## 扩展Linux磁盘

    fdisk -l
    查看挂载磁盘信息
    fdisk /dev/sda 
    对磁盘进行分区
    np
    n-新建分区
    p-主分区
    默认...
    w - 保存
    mkfs -t ext3 -c /dev/sdb1
    格式化磁盘
    
    磁盘 /dev/sda：85.9 GB, 85899345920 字节，167772160 个扇区
    Units = 扇区 of 1 * 512 = 512 bytes
    扇区大小(逻辑/物理)：512 字节 / 512 字节
    I/O 大小(最小/最佳)：512 字节 / 512 字节
    磁盘标签类型：dos
    磁盘标识符：0x000ed50d
    
    设备 Boot      Start         End      Blocks   Id  System
    /dev/sda1   *        2048     2099199     1048576   83  Linux
    /dev/sda2         2099200    41943039    19921920   8e  Linux LVM
    /dev/sda3        41943040   167772159    62914560   83  Linux
    
    磁盘 /dev/mapper/cl-root：18.2 GB, 18249416704 字节，35643392 个扇区
    Units = 扇区 of 1 * 512 = 512 bytes
    扇区大小(逻辑/物理)：512 字节 / 512 字节
    I/O 大小(最小/最佳)：512 字节 / 512 字节


    磁盘 /dev/mapper/cl-swap：2147 MB, 2147483648 字节，4194304 个扇区
    Units = 扇区 of 1 * 512 = 512 bytes
    扇区大小(逻辑/物理)：512 字节 / 512 字节
    I/O 大小(最小/最佳)：512 字节 / 512 字节

   

## 服务器空间不足

查看磁盘使用

```sh
du -h -x --max-depth=1

发现 /var/cache目录较大

执行
yum clean all

```

```
Directory /home/fs/data/hadoop-root/dfs/name is in an inconsistent state: storage directory does not exist or is not accessible

ssh localhost无法通过，Permission denied
修改过/etc/ssh/sshd_config的PermitRootLogin：yes
```





## 查看服务器型号

https://blog.csdn.net/zhangliao613/article/details/79021606