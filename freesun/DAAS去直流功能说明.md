## DAAS去直流功能说明

支持软件采集过程中对振动数据进行去直流的功能。具体设置在"采样示波参数设置"中，如下：

![image-20240716142852428](C:\Users\yww08\AppData\Roaming\Typora\typora-user-images\image-20240716142852428.png)

+ 自动校准开关
  + 关闭：不进行自动校准
  + 启动后仅执行一次：仅在启动的时候加载“校准时长”秒的数据计算均值，作为后续去直流的基准。
  + 启动后每半小时执行一次：启动时执行一次，之后每半小时更新一次去直流基准值。
  + 总是执行：连续收集校准时长段的数据，不断更新去直流基准。
+ 校准时长
  + 取多久的数据做平均，作为去直流基准值。



数据流程：



振动采集软件，采集到每一包数据后。