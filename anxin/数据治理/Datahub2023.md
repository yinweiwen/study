基础概念看这篇就够了：

https://www.cnblogs.com/itxiaoshen/p/17392602.html



### Great Expectations

Great Expectations是最近出现的一款优秀的数据质量检查工具

- Data Batch: 数据包，对输入数据的封装
- Expectations: 检验规则，期望的数据情况，例如 某一个字段不应该有空值
- Expectation Suite:：检验规则的集合，包含多个Expectaton
- Metric:指标，执行检查规则时需要计算的指标，例如某列的均值
- Profiler: 对目标数据的画像，形成各种指标
- Validator: 规则校验器，执行检查规则Expectation，计算Metric的类
- Checkpoint: 数据检查点，调用validator去执行expectations，获得所有的验证结果，是将Data Batch和Expectation Suite整合调用的工具
- Data Docs: 可视化的质量检查结果文档
- Data Context: 管理数据配置的上下文容器，是通过yaml配置，包含其他组件所需的配置，是程序运行时第一个入口。
- Runtime_environment:运行时的配置，根据用户的输入，会替换事先配置的参数
- ExecutionEngine: 具体执行检查规则的引擎，比如sqlserver或者dataframe或者spark
- Data Assistant: 数据助手，其调用profiler检查多个数据情况，生成metric(可以配置exact或者flag_outliers模式，前者是没有去掉奇异点的情况)，从而形成对Expectation的建议，为了方便用户快速形成对检查规则的配置
- Action: 数据检查结果形成后的触发动作，例如发邮件、使用OpenLineage寻找错误原因、生成文档等
- Renderer 将验证规则和验证结果，转化为文档格式

![overview](imgs/Datahub2023/gx_oss_process-050a4264f415a1bff3ceea3ac6f9b3a0.png)



在jupyter中试验：

https://github.com/datarootsio/tutorial-great-expectations

```sh
docker pull dataroots/tutorial-great-expectations && docker run -it --rm -p 8888:8888 dataroots/tutorial-great-expectations
```

在浏览器中访问

http://10.8.30.71:8888/tree#notebooks



