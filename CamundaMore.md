## READ MORE

### Listener定义

可以直接定义java类

 ![image-20210630144938550](H:/coding/study/imgs/Camunda/image-20210630144938550.png)

或者通过注解attach到对应的任务实例：

```java
@Component
public class CCListener {
    private Logger logger= LoggerFactory.getLogger(CCListener.class);

    @EventListener(condition = "#delegateTask.eventName=='create' && #delegateTask.name=='CCTask'")
    public void Notify(){

    }
}
```



### Process Engine API

在spring-boot程序中使用

```java
@Autowired
private RepositoryService repositoryService;
```

+ 

### Process Engine Concepts

##### Process Definitions 过程定义

```java
List<ProcessDefinition> processDefinitions = repositoryService.createProcessDefinitionQuery()
    .processDefinitionKey("invoice") //key
    .orderByProcessDefinitionVersion()
    .asc()
    .list();
```

##### Process Instance 过程实例

启动一个实例

```java
ProcessInstance instance = runtimeService.startProcessInstanceByKey("invoice");
```

通过fluent builder创建任意Activities集合的过程实例

```java
ProcessInstance instance = runtimeService.createProcessInstanceByKey("invoice")
  .startBeforeActivity("SendInvoiceReceiptTask")
  .setVariable("creditor", "Nice Pizza Inc.")
  .startBeforeActivity("DeliverPizzaSubProcess")
  .setVariableLocal("destination", "12 High Street")
  .execute();
```

##### Job&Job Definitions

Camunda进程引擎包含一个名为Job Executor的组件。作业执行器是一个调度组件，负责执行异步后台工作 (例如定时器)

查询：

```java
managementService.createJobDefinitionQuery()
  .processDefinitionKey("orderProcess")
  .list()
```

