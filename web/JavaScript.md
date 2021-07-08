**提示：**把脚本置于 **<body>** 元素的底部，可改善显示速度，因为脚本编译会拖慢显示。

通过脚本文件引入

```html
<script src="myScript.js"></script>
```



## 输出

```js
document.getElementById(id).innerHTML='hello'

document.write()

windows.alert()

console.log()
```



## 类型

包含值的数据类型

+ string
+ number
+ boolean
+ object
+ function

对象类型

+ Object
+ Date
+ Array

不能包含值的数据类型

+ null
+ undefined

```js
typeof "ww"     // string  typeof运算符

"ww".constructor // "function String(){[native code]}"  返回变量的构造器函数

```

### 类型转换

```javascript
String(x)
x.toString()
num.toExponential/toFiexed/toPrecision

Number("") // NaN
Number(Date) // epoch milliseconds
parseFloat parseInt
```

日期

```js
date.getDate()/getDay/getMonth ...
```



## 正则

正则表达式是构成*搜索模式（search pattern）*的字符序列。

```js
var patt = /w3school/i;
// / pattern 
// i 大小写不敏感  另外，g 执行全局匹配  m 多行匹配

// 两个字符串方法
str.search(pattern) // return index
str.replace(pattern)

// RegExp对象
patt.test("blabla")  // bool
patt.exec("search text") // 执行搜索返回结果
```

## 异常

```js
try{
    adddlert("欢迎访问！");
    // throw 1
}catch(err){
    console.log(err) // stacktrace
    console.log(err.name)
    console.log(err.message)
}finally{
    
}
```

## 全局变量

在JavaScript中，函数外声名的变量是全局变量

全局变量的作用域是全局的，网页的所有脚本和函数都能访问它

自动全局：如果您为尚未声明的变量赋值，此变量会自动成为*全局*变量。

严格模式：严格模式下不会自动创建全局变量

```js
myFunction();
// 此处的代码能够使用 carName 变量

function myFunction() {
    carName = "porsche";
}
```



### Hoisting 声明提升

Hoisting是JavaScript中将所有声明提升到当前作用域顶部的默认行为。

```js
x=5;
console.log(x);
var x;
// 等同于：

var x;
x=5;
console.log(x);
```

let和const声明的变量不会被提升！

### “use strict”

严格模式：不允许

+ 不声明使用变量
+ 删除变量、对象、函数（delete）
+ 重复参数名
+ 不允许八进制数值 010
+ 不允许转义字符 \010

https://www.w3school.com.cn/js/js_this.asp