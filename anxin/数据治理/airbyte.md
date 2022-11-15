## Airbyte

是一款开源的数据EL工具

![3.048-Kilometer view](imgs/airbyte/understanding_airbyte_high_level_architecture-ac5aa905ca8916521b41cdaecec4cd08.png)

组件

- `UI`：一个易于使用的图形界面，用于与 Airbyte API 进行交互。
- `WebApp Server`: 处理 UI 和 API 之间的连接。
- `Config Store`：存储所有连接信息（凭据、频率... ）。
- `Scheduler Store`：存储调度程序簿记的状态和作业信息。
- `Config API`: Airbyte的主控平面。Airbyte 中的所有操作，例如创建源、目标、连接、管理配置等，都是通过 API 配置和调用的。
- `Scheduler`：调度程序从 API 获取工作请求并将它们发送到 Temporal 服务以并行化。它负责跟踪成功/失败并根据配置的频率触发同步。
- `Temporal Service`：管理调度程序的任务队列和工作流。
- `Worker`：工作人员连接到源连接器，拉取数据并将其写入目标。
- `Temporary Storage`：工作人员在需要将数据溢出到磁盘上时可以使用的存储。



有四种worker：

- `MAX_SPEC_WORKERS`：允许并行运行的***Spec* worker**的最大数量。
- `MAX_CHECK_WORKERS`：允许并行运行的***检查连接***工作人员的最大数量。
- `MAX_DISCOVERY_WORKERS`：允许并行运行的***Discovery* worker**的最大数量。
- `MAX_SYNC_WORKERS`：允许并行运行的最大***同步工作者**数。*



使用[Temporal](https://temporal.io/)进行任务调度

![image-20221115104947590](imgs/airbyte/image-20221115104947590.png)

基于Temporal SDK，用户开发Temporal APP:（一个可重入的进程）。

Temporal SDK中提供Client接口，支持其与[Temporal Cluster![Link preview icon](imgs/airbyte/link-preview-icon.svg)](https://docs.temporal.io/clusters)之间的通信



## References:

[official] https://airbytehq.github.io/understanding-airbyte/high-level-view



2022 Q4 Roadmap

MESSY DAYS:

+ AI
  + Smart fire/helmet detection :star::computer:
  + BP on platform :snake:
  + Video
    + Performance :star2:
    + SRS
    + CORS
+ EDGE
  + Authorization :soccer:
  + WEB App on REACT :sos:
  + interact with AI
  + interact with **BigData** :star2::star2::star2:
+ Big Data
  + Learn more @Clickhouse
  + Learn more @Flink
  + Learn more @airbyte
+ DevOps
  + Services stability
  + Device/Protocol management
  + Data Simulator
  + Data Mover