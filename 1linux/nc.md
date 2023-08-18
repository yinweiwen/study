在 Ubuntu（以及其他类Unix系统）中，`nc` 命令是网络工具中的一个，通常用于创建基本的网络连接、监听端口、传输数据等。它也被称为 "netcat"。

以下是一些常见的用法示例：

1. **创建简单的网络连接：**

   通过指定目标主机和端口，你可以建立到目标的简单连接。

   ```
   shCopy code
   nc example.com 80
   ```

2. **监听端口：**

   你可以使用 `nc` 命令来监听一个特定的端口，等待其他计算机连接进来。

   ```
   shCopy code
   nc -l -p 12345
   ```

3. **传输文件：**

   `nc` 命令可以用于在网络上传输文件。

   在接收端：

   ```
   shCopy code
   nc -l -p 12345 > received_file.txt
   ```

   在发送端：

   ```
   shCopy code
   cat file_to_send.txt | nc target_ip 12345
   ```

4. **测试端口连通性：**

   你可以使用 `nc` 命令来测试特定主机和端口是否可达。

   ```
   shCopy code
   nc -zv example.com 80
   ```

这只是 `nc` 命令的一些基本用法示例。你可以使用 `man nc` 命令来查看完整的 `nc` 命令手册，以了解更多的选项和用法。在某些系统中，你可能需要安装 `netcat` 包，才能使用 `nc` 命令。在 Ubuntu 上，你可以使用以下命令安装：

```
shCopy code
sudo apt-get install netcat
```