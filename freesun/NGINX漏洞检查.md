NGINX漏洞检查

https://github.com/stark0de/nginxpwner

Nginxpwner 是一个简单的工具,用于查找常见的 Nginx 配置错误和漏洞。它可以检测 Nginx 版本是否过时,并提供相关漏洞信息;扫描 Nginx 特定的漏洞,如 CRLF 注入、路径遍历、变量泄露等;还可以测试 PURGE HTTP 方法的可用性,以及 401/403 绕过等问题。该工具主要关注开发人员在 nginx.conf 中可能引入的配置错误。

### 主要功能点

- 获取 Nginx 版本并检查是否存在已知漏洞
- 使用 gobuster 扫描 Nginx 特定的漏洞
- 检查 CRLF 注入、路径遍历、变量泄露等常见配置错误
- 测试 PURGE HTTP 方法的可用性
- 检查 401/403 绕过漏洞
- 测试 Raw backend 读取响应的配置错误
- 检查 PHP 相关的 Nginx 配置问题
- 测试 CVE-2017-7529 整数溢出漏洞



使用：

```sh
sudo docker run -it nginxpwner:latest /bin/bash
```

这段代码的作用是使用Burp工具获取一个主机的所有URL，然后将这些URL提取出路径，并去重排序，最后使用nginxpwner.py脚本来对指定的网站进行路径爆破。具体步骤包括：

1. 在Burp中选择目标标签页，选择主机，右键点击，复制该主机的所有URL，并保存到一个文件中。
2. 使用命令`cat urllist | unfurl paths | cut -d"/" -f2-3 | sort -u > /tmp/pathlist`对保存的URL文件进行处理，提取出路径并去重排序，保存到`/tmp/pathlist`文件中。
3. 或者通过其他方式获取已发现的应用程序中的路径列表（这些路径不应以/开头）。
4. 最后，使用命令`python3 nginxpwner.py https://example.com /tmp/pathlist`来运行nginxpwner.py脚本，对指定网站进行路径爆破。



检查 https://project.zhiwucloud.com/signin 过程：

```sh
root@ck-n1:/home/diantong/nginxpwner-main# python nginxpwner.py https://project.zhiwucloud.com pathlist

 _   _  _____  _____  _   _ __   ________  _    _  _   _  _____ ______
| \ | ||  __ \|_   _|| \ | |\ \ / /| ___ \| |  | || \ | ||  ___|| ___ \
|  \| || |  \/  | |  |  \| | \ V / | |_/ /| |  | ||  \| || |__  | |_/ /
| . ` || | __   | |  | . ` | /   \ |  __/ | |/\| || . ` ||  __| |    /
| |\  || |_\ \ _| |_ | |\  |/ /^\ \| |    \  /\  /| |\  || |___ | |\ \
\_| \_/ \____/ \___/ \_| \_/\/   \/\_|     \/  \/ \_| \_/\____/ \_| \_|

            A common vulnerability scanner for Nginx
                      Author @stark0de1
[-] NGINX out of date, current version is: 1.12.1 and last version is: 1.27.0
[-] All possible exploits will be printed now:
sh: 1: searchsploit: not found
[-] For the complete list of vulnerabilities check out: https://cve.mitre.org/cgi-bin/cvekey.cgi?keyword=nginx
[?] If the tool reveals the nginx.conf file this is probably because there is no root directive in the nginx.conf file. Get the contents of the file and use https://github.com/yandex/gixy to find more misconfigurations



2024/07/22 14:57:25 [!] 2 errors occurred:
        * WordList (-w): Must be specified (use `-w -` for stdin)
        * Url/Domain (-u): Must be specified



[+] No CRLF via common misconfiguration found
[+] No signs of misconfigured PURGE HTTP method
[+] No variable leakage misconfiguration found
[-] Possible path traversal vulnerability found for insecure merge_slashes setting
[-] Try this to URIs manually: ///../../../../../etc/passwd and //////../../../../../../etc/passwd
[?] Testing hop-by-hop headers

No relevant results for 127.0.0.1 tests
No relevant results for localhost tests
No relevant results for 192.168.1.1 tests
No relevant results for 10.0.0.1 tests

[?] To test Raw backend reading responses, please make a request with the following contents to Nginx. In case the response is interesting: https://book.hacktricks.xyz/pentesting/pentesting-web/nginx#raw-backend-response-reading

GET /? XTTP/1.1
Host: 127.0.0.1
Connection: close

[+] The site uses PHP
[!] If the site uses PHP check for this misconfig: https://book.hacktricks.xyz/pentesting/pentesting-web/nginx#script_name and also check this: https://github.com/jas502n/CVE-2019-11043. A last advice, if you happen to have a restricted file upload and you can reach the file you uploaded try making a request to <filename>/whatever.php,and if it executes PHP code it is because the PHP-FastCGI directive is badly configured (this normally only works for older PHP versions)


[?] Executing Kyubi to check for path traversal vulnerabilities via misconfigured NGINX alias directive

URL                                                     Status
--------------------------------------------------------------------------------
https://project.zhiwucloud.com/signin../ [200]
https://project.zhiwucloud.com/signin../../ [200]
https://project.zhiwucloud.com/signin../../../../../../../../../../../ [200]

[+] No X-Accel-Redirect bypasses found using it as request header

[?] Testing all provided paths to check to CRLF injection. This is specially interesting if the site uses S3 buckets or GCP to host files

[?] Testing for common integer overflow vulnerability in nginx's range filter module
target: https://project.zhiwucloud.com
status: 200: Server: nginx/1.12.1
status: 200: Server: nginx/1.12.1

[+] Non vulnerable



[?] If the site uses Redis, please do check out: https://labs.detectify.com/2021/02/18/middleware-middleware-everywhere-and-lots-of-misconfigurations-to-fix/


[*] More things that you need to test by hand: CORS misconfiguration (ex: bad regex) with tools like Corsy, Host Header injection, Web cache poisoning & Deception in case NGINX is being for caching as well, HTTP request smuggling both normal request smuggling and https://bertjwregeer.keybase.pub/2019-12-10%20-%20error_page%20request%20smuggling.pdf. As well as the rest of typical web vulnerabilities
```

