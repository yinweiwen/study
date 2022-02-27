## Scala语言入门

参考：

《[Scala语言规范](https://static.runoob.com/download/Scala%E8%AF%AD%E8%A8%80%E8%A7%84%E8%8C%83.pdf)》

《[Scala入门](https://docs.scala-lang.org/zh-cn/tour/tour-of-scala.html)》

[Scala](https://so.csdn.net/so/search?q=Scala&spm=1001.2101.3001.7020) 是一门多范式（multi-paradigm）的编程语言，设计初衷是要集成面向对象编程和函数式编程的各种特性。

Scala是一种针对JVM将函数和面向对象技术组合在一起的编程语言。



+ 面向对象：一切皆对象

+ 函数式编程：一切皆表达式，每条语句都可以看成一条表达式。函数式的特点是无副作用，不会对输入参数进行修改，scala设计的默认数据结构绝大部分是不可变的。并且在一个良好风格的scala程序中，只需要使用val不可变变量而无需使用var可变变量。

+ 简洁：强大的自动类型推断，隐含类型转换，匿名函数，case类，字符串插值器。

+ 表现力：集合的&和|运算,函数定义的=>符号,for循环<-的符号，Map的 ->符号，以及生成range的 1 to 100等表达。

### 类型

`AnyVal`代表值类型,`AnyRef`代表引用类型.

`Nothing`是所有类型的子类型，也称为底部类型。`Null`是所有引用类型的底部类型。

![Scala Type Hierarchy](imgs/scala-et/unified-types-diagram.svg)

### 声明与定义

```scala
// 定义变量
var index1 : Int= 1
// 定义常量
val index2 : Int= 1
```

### 类

```scala
// 类名，构造器和默认值
class Point(var x: Int = 0, var y: Int = 0)

// 特质（接口）
trait Iterator[A] {
  def hasNext: Boolean
  def next(): A
}

// 继承
class Cat(val name: String) extends Pet
```

### 方法与函数

```scala
class Test{
    // 定义方法
  def m(x: Int):Int = x + 3
   // 定义函数
  val f = (x: Int) => x + 3
}
```

> 方法可以返回Unit，类似java的void
>
> 方法的返回在最后，可以省略return关键字

### 元组

Tuple2~Tuple22

```scala
val tp= ("sugar",24,true):Tuple3[String,Int,Boolean]
println(tp._1)

// 元组在循环中的应用
val numPairs = List((2, 5), (3, -7), (20, 56))
for ((a, b) <- numPairs) {
  println(a * b)
}
```

### Object和Case Class

Case Class和class的区别：

+ 初始化的时候new字段不是必须
+ 默认实现toString/equals/hashCode/Serializable
+ 支持模式匹配

Object：

+ 通过object实现静态方法和字段
+ 通过与Class同名，即类与伴生对象。共享属性和方法、必须在同一源文件中。

### 模式匹配

```scala
// 类似switch功能
x match {
  case 0 => "zero"
  case 1 => "one"
  case 2 => "two"
  case _ => "other"
}

// case class
def showNotification(notification: Notification): String = {
  notification match {
      // 模式匹配和，模式守卫
    case Email(sender, title, _) if sender.startWith("A") =>
      s"You got an email from $sender with title: $title"
    case SMS(number, message) =>
      s"You got an SMS from $number! Message: $message"
    case VoiceRecording(name, link) =>
      s"you received a Voice Recording from $name! Click the link to hear it: $link"
  }
}
```



## ET Coding

部分代码及语法说明。



## ET 环境准备



## ET开发