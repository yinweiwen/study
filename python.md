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