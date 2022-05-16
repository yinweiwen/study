# Python

### Data Types
1. Numbers (int,long,float,complex)
2. String
3. List
4. Tuple
5. Dictionary

list=['a',1,2.2]
tuple=('a', 1, 0.2) -- 相当于只读列表
String、List列表、Tuple元组
[a:b] a~b索引的子集(包含a,不包含b)
[a:b:c] a~b step为c的子集
dict={}; dict[1]='s';dict['a']='b'
a**b == a的b次方

### 逻辑运算符
and/or/not   
bool值是True/False  非0和非空（null）值为true

### 成员运算符
in / not in

### 身份运算符
is / is not  
is 用于判断两个变量引用对象是否为同一个(同一块内存空间)， == 用于判断引用变量的值是否相等。

### 条件
if xxx: elif xxx:  else:

while: else: 

pass 不做任何事情，一般用做占位语句。

```python
for letter in 'python':
    if letter=='h':
        pass
    else: print(letter)
```

### 数学函数
```python
import math,cmath
print(math.sin(math.radians(30)))
print(math.cos(math.pi))
print(cmath.sqrt(-1))
# 随机数
import random
alist=[2,1,24,3,5]
print(random.random()) # 0~1
print(random.randrange(1,10,2))
print(random.choice(alist))
```

### 字符串
```python
# 原始字符串
print(r'\r123"bc')
print("pi=%3.2f"%3.14159)
print('''sql=
CREATE TABLE users (  
login VARCHAR(8), 
uid INTEGER,
prid INTEGER)
''')
print(u'Hello\u0020World !')
import string
print('3.23'.isalnum()) # False
print('aBc+'.isalpha()) # False
print('caption'.center(100))
print('-2.12e2'.isnumeric())
print('  name  '.strip())
```



[镜像库]
1. 
国内源：
清华：https://pypi.tuna.tsinghua.edu.cn/simple

阿里云：http://mirrors.aliyun.com/pypi/simple/

中国科技大学 https://pypi.mirrors.ustc.edu.cn/simple/

华中理工大学：http://pypi.hustunique.com/

山东理工大学：http://pypi.sdutlinux.org/ 

豆瓣：http://pypi.douban.com/simple/

note：新版ubuntu要求使用https源，要注意。

例如：pip3 install -i https://pypi.doubanio.com/simple/ 包名

2. 
[linux] ~/.pip/pip.conf
[windows] C:\Users\yww08\pip\pip.ini

```
[global]
index-url = https://pypi.tuna.tsinghua.edu.cn/simple
trusted-host = pypi.tuna.tsinghua.edu.cn
disable-pip-version-check = true
timeout = 120
[install]
trusted-host=mirrors.aliyun.com
```





## Flask程序构建发布

```python
 pip freeze > requirements.txt
```

start.sh

```sh
#!/bin/bash

source venv/bin/activate && flask run -h 0.0.0.0 -p 5000
```



Dockerfile

```dockerfile
FROM repository.anxinyun.cn/base-images/python:ubuntu-4.21-04-28

WORKDIR /app

COPY . .

RUN rm /bin/sh && ln -s /bin/bash /bin/sh

RUN source venv/bin/activate && pip install --default-timeout=100 -i https://mirrors.aliyun.com/pypi/simple/ -r requirements.txt

RUN chmod u+x ./start.sh

EXPOSE 5000

CMD ["bash","./start.sh" ]
```

