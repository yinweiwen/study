# [CSS](https://www.w3school.com.cn/)

## 语法

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

## Color

<h5 style="background-color:tomato;">tomato</h5>
<h5 style="background-color:rgb(255, 99, 71);">rgb(255, 99, 71)</h5>
<h5 style="background-color:#ff6347;">#ff6347</h5>
<h5 style="background-color:hsl(9, 100%, 64%);">hsl(9, 100%, 64%)</h5>



## 背景

+ background-color
+ background-image
+ background-repeat    # no-repeat/repeat-x/repeat-y
+ background-attachment
+ background-position  # right top

简写：

```css
body {
    background: #ffffff url("tree.png") no-repeat right top
}
```



## 边框

+ border-style  

  <p style="border-style:dotted;">dotted</p>

  <p style="border-style:solid;">solid</p>

  ... dashed double groove ridge inset outset none hidden

+ border-width

  `px/pt/cm/em`  thin/medium/thick

+ border-color

+ border-radius

  <p style="border-style:solid;border-color:red;border-radius:10px">Border Radius</p>

简写

```csss
p{
 border: 5px solid red;
}
```

## 外边框

<p style="border:2px solid red; margin-top:20px">top margin 20px outside</p>

+ margin: 0px上 右 下 左

外边距合并： 当两个**垂直**外边距相遇时，取最大的保留。（垂直margin作用： 段落间留白足够）

## 内边距

<p style="border:2px solid red; padding:20px">padding 20px inside</p>

> width+padding是实际容器宽度。使用box-sizing可以限定总宽度



## 高度/宽度

height 和 width 属性可设置如下值：

- auto - 默认。浏览器计算高度和宽度。
- *length* - 以 px、cm 等定义高度/宽度。
- % - 以包含块的百分比定义高度/宽度。
- initial - 将高度/宽度设置为默认值。
- inherit - 从其父值继承高度/宽度。

<p style="width:500px; background:tomato;">width 500px</p>

<p style="max-width:500px; background:tomato;">max-width  500px</p>

## 框模型

![image-20210619170019791](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\image-20210619170019791.png)

## 轮廓

<p style="width:200px; border:2px solid black; outline:5px solid blue">黑色边框 蓝色轮廓</p>

轮廓不计入元素尺寸，轮廓可以重叠



## 文本

<h4 style="color:GREEN;text-align:center;">文本格式化</h4>

+ text-align 对齐方式
+ vertival-align 垂直对齐
+ text-decoration: none,overline,line-through,underline
+ text-transform: uppercase/lowercase/capitalize
+ text-indent 首行缩进
+ letter-spacing 字符间间距
+ line-height 行间距
+ text-shadow 阴影效果 :2px(水平阴影) 2px(垂直阴影) 5px(模糊效果) red(阴影颜色)



Font

+ font-family: "Times New Roman", Times, serif; 备选
+ font-style: normal;italic;oblique
+ font-weight: normal;bold;
+ font-size: 40px;2.5em;  1em=16px

## 链接

```css
a:link{
    color:red;
}
a:visited / hover / active
```

## 列表

```css
ul {
    list-style-type:circle; square; upper-roman;lower-alpha;
}
```

