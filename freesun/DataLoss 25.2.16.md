DataLoss 25.2.16

ET log: 

滑窗计算时读取缓存的窗口数据失败：疑似REDIS缓存丢失。

![image-20250216142548782](C:\Users\yww08\AppData\Roaming\Typora\typora-user-images\image-20250216142548782.png)

查看redis:

![image-20250216142921551](C:\Users\yww08\AppData\Roaming\Typora\typora-user-images\image-20250216142921551.png)

恢复：（因为ET重启了）

![image-20250216143025253](imgs/DataLoss 25.2.16/image-20250216143025253.png)