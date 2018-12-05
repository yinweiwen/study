# QT Creator
## Chapter 3
1. 无法调试：
	pro里面加配置
	`CONFIG     += debug`

2. qDebug 打印方法
```cpp
	qDebug("x:%d",x);
    qDebug() << "rect" <<endl<< rect;  // 需要#include <QDebug>
```
3. 对话框
```cpp
	QDialog dialog(this); 模态
    dialog.exec();

    QDialog *dialog2=new QDialog(this); 非模态
    dialog2->show();
```
4. 信号和槽

	Signal and Slots
	
	.h 文件中定义:
	```
	private slots:
		void on_pushButton_clicked();
	```
	
	Creator中 F4 拖动定义信号和槽的关联
	或 右键‘转到槽’ 自动跳转到槽位置
	
	界面添加槽的方式，省去connect函数，connect信息记录在ui文件中
5. 自动补齐的lineedit
	```
		QStringList wordList;
    wordList << "Qt" << "Qt Creator" << tr("你好");
    // 新建自动完成器
    QCompleter *completer = new QCompleter(wordList, this);
    // 设置大小写不敏感
    completer->setCaseSensitivity(Qt::CaseInsensitive);
    ui->lineEdit->setCompleter(completer);
	```
	需要引入 include "QCompleter"
	需要在 ui->setupUi(this);之后执行
	
## Chapter 4
1. 布局

Ctrl+L 水平布局
Ctrl+V 垂直布局
	
	伙伴(buddy) QLabel提供了一个有用的机制，那就是提供了助记符来设置键盘焦点到对应的部件上
	
## Chapter 5
	引入Printer时报错：
	E:\2Coding\qt\HelloWorld\mainwindow.cpp:125: error: undefined reference to `_imp___ZN8QPrinterD1Ev'
	
	需要引入模块 QT += printsupport
