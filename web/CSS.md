## MD

```
选择器：{
    声明1；-》 属性:值；
    声明2；
    }
	
1，标签选择器， h1

2，类型选择器 .red{
	
}
<h1 class="red"></h1>

3、ID选择器
#green{
	color:red;
}

<p id="green"></p>

1. <style>{...}</style> 内部样式表

2. <p style="color:red;"/> 内联样式 （弊端：内容和样式没有分离）

3.1 <link rel="stylesheet" href="css/gp.css"> 外部样式表-链接式
3.2 外部样式表-导入式(CSS2.1&后导入import 推荐使用链接式)
    <style>
        @import "css/gp.css";
    </style>

超链接样式
标签：伪类
a:hover{
	color:red;
}
```
