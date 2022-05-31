## 实战准备：如何搭建硬件开发环境

参考课程 https://time.geekbang.org/column/article/321652


连接NodeMCU：

![image-20220531202714794](imgs/IOT/image-20220531202714794.png)



下载驱动：
https://www.silabs.com/developers/usb-to-uart-bridge-vcp-drivers 

![image-20220531203407451](imgs/IOT/image-20220531203407451.png)



下载兼容ESP8266的MicroPython固件： https://micropython.org/download/esp8266/



安装python 2.7.15

```python
pip install esptool
```



```
.\esptool.py.exe --port COM3 erase_flash

.\esptool.py.exe --port COM3 --baud 460800 write_flash --flash_size=detect 0 esp8266-20220117-v1.18.bin

python pyboard.py --device COM3 -f cp main.py :

```

可以看到如下名称的wifi热点：

![image-20220531214056043](imgs/IOT/image-20220531214056043.png)

使用putty连接:

![image-20220531214231936](imgs/IOT/image-20220531214231936.png)

> 进入REPL后无法输入
>
> https://github.com/micropython/micropython/issues/587
>
> 使用SecureCRT连上之后也无法进行交互输入。通过 `flowcontrol = none`解决
>
> > I re uploaded the micropython firmware and, just for fun, i tried to open with the arduino serial monitor. And it worked.
> > Back to `minicom` and check settings... `Hardware Flow Control `was on... Once I turned it `off` it works!
> >
> > Sorry for the wasted time.
>
> still important in 2020 (putty flowcontrol = none)



编写main.py，复制到开发板上：

> 1. 使用`pyboard.py` 工具： https://docs.micropython.org/en/latest/reference/pyboard.py.html
> 2. 必须用python3执行
> 3. pip装包时报`SSL`错误需关掉梯子
> 4.  module 'serial' has no attribute '__version__'

```sh
set PYBOARD_DEVICE=COM3
python3 pyboard.py -f cp main.py :

# or
python3 pyboard.py --device COM3 -f cp main.py :

# 说明
 -f, --filesystem      perform a filesystem action: cp local :device | cp
                        :device local | cat path | ls [path] | rm path | mkdir
                        path | rmdir path
```

首先`pip install serial`

![image-20220601000559151](imgs/IOT/image-20220601000559151.png)



main.py

```python
import machine
import time

# 指明 GPIO2 管脚
pin = machine.Pin(2, machine.Pin.OUT)

# 循环执行
while True:
    time.sleep(2)   # 等待 2 秒
    pin.on()        # 控制 LED 状态
    time.sleep(2)   # 等待 2 秒
    pin.off()       # 切换 LED 状
```



[Micro.python ESP8266 API](https://docs.micropython.org/en/latest/esp8266/quickref.html)



## 附件

WoT.City 的 Web of Things Framework 就是依靠不同 IoT 类型

| IoT Node (Device Type)      | Solution Platform                            | IoT Diagram (Use Scenario)                           |
| :-------------------------- | :------------------------------------------- | :--------------------------------------------------- |
| Single Board Computer (SBC) | Intel Edison、Qualcomm Dragonboard 410c etc. | IoT Router etc.                                      |
| High Performance MCU        | ARM mbed OS、Neuclio                         | Sensor hub (Time-series Data Type) etc.              |
| WiFi MCU                    | ESP8266、NodeMCU、EMW3165 etc.               | Sensor hub (Interrupt Type)、Network controller etc. |

NodeMCU 开发板 = ESP8266 模组 + USB to serial 芯片 + NodeMCU firmware

![esp8266-nodemcu-pinout](imgs/IOT/5919b29836076.png)
