### MyBatis

MyBatis 是一款优秀的持久层框架，它支持自定义 SQL、存储过程以及高级映射。MyBatis 免除了几乎所有的 JDBC 代码以及设置参数和获取结果集的工作。MyBatis 可以通过简单的 XML 或注解来配置和映射原始类型、接口和 Java POJO（Plain Old Java Objects，普通老式 Java 对象）为数据库中的记录。



在spintboot中引入MyBatis

```xml
<dependency>
   <groupId>org.mybatis.spring.boot</groupId>
   <artifactId>mybatis-spring-boot-starter</artifactId>
   <version>2.2.2</version>
   <exclusions>
      <exclusion>
         <groupId>com.zaxxer</groupId>
         <artifactId>HikariCP</artifactId>
      </exclusion>
   </exclusions>
</dependency>

<!--Mybatis分页插件 -->
<dependency>
    <groupId>com.github.pagehelper</groupId>
    <artifactId>pagehelper-spring-boot-starter</artifactId>
    <version>1.4.6</version>
</dependency>
```



在application.xml中添加如下配置

```xml
# 修改为数据库字段下划线分隔直接对应java驼峰命名
mybatis:
    configuration:
        map-underscore-to-camel-case: true
```



Spring Boot中使用MyBatis非常简便。下面是使用Spring Boot和MyBatis的基本步骤：

1. 添加依赖：在你的Spring Boot项目的`pom.xml`文件中，添加MyBatis和相应的数据库驱动依赖。例如，对于MySQL数据库，可以添加以下依赖：

```xml
xmlCopy code<dependencies>
    <!-- Spring Boot Starter for MyBatis -->
    <dependency>
        <groupId>org.mybatis.spring.boot</groupId>
        <artifactId>mybatis-spring-boot-starter</artifactId>
        <version>2.2.0</version>
    </dependency>
    
    <!-- MySQL Connector -->
    <dependency>
        <groupId>mysql</groupId>
        <artifactId>mysql-connector-java</artifactId>
        <version>8.0.26</version>
    </dependency>
</dependencies>
```

1. 配置数据源：在`application.properties`（或`application.yml`）配置文件中，配置数据库连接信息。例如，对于MySQL数据库，可以添加以下配置：

```properties
propertiesCopy codespring.datasource.url=jdbc:mysql://localhost:3306/mydatabase
spring.datasource.username=username
spring.datasource.password=password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
```

1. 创建实体类：在Java代码中，创建与数据库表对应的实体类。使用`@Entity`和`@Table`注解指定实体类与数据库表的映射关系。

```java
javaCopy code@Entity
@Table(name = "users")
public class User {
    @Id
    private Long id;
    
    private String name;
    private Integer age;
    // Getters and setters...
}
```

1. 创建Mapper接口：创建与实体类对应的Mapper接口，用于定义数据库操作的方法。在接口方法上使用`@Select`、`@Insert`、`@Update`、`@Delete`等注解指定对应的SQL语句。

```java
javaCopy code@Mapper
public interface UserMapper {
    @Select("SELECT * FROM users")
    List<User> getAllUsers();
    
    @Insert("INSERT INTO users (id, name, age) VALUES (#{id}, #{name}, #{age})")
    void insertUser(User user);
    
    // Other methods...
}
```

1. 配置MyBatis：创建一个配置类，并使用`@MapperScan`注解指定Mapper接口所在的包。

```java
javaCopy code@Configuration
@MapperScan("com.example.myapp.mapper")
public class MyBatisConfig {
}
```

1. 使用Mapper接口：在你的服务类中，通过注入Mapper接口的实例，使用MyBatis执行数据库操作。

```java
javaCopy code@Service
public class UserService {
    private final UserMapper userMapper;
    
    public UserService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }
    
    public List<User> getAllUsers() {
        return userMapper.getAllUsers();
    }
    
    public void addUser(User user) {
        userMapper.insertUser(user);
    }
    
    // Other methods...
}
```

以上就是在Spring Boot中使用MyBatis的基本步骤。通过配置数据源、创建实体类和Mapper接口，以及使用MyBatis注解和注入Mapper接口实例，你可以方便地进行数据库操作。



遇到问题：

报错如下，提示 ested exception is org.apache.ibatis.binding.BindingException: Parameter 'addChannels' not found. 在对应方法前加上 @Param即可

```

org.mybatis.spring.MyBatisSystemException: nested exception is org.apache.ibatis.binding.BindingException: Parameter 'addChannels' not found. Available parameters are [arg0, collection, list]
	at org.mybatis.spring.MyBatisExceptionTranslator.translateExceptionIfPossible(MyBatisExceptionTranslator.java:96)
	at org.mybatis.spring.SqlSessionTemplate$SqlSessionInterceptor.invoke(SqlSessionTemplate.java:441)
	at com.sun.proxy.$Proxy83.insert(Unknown Source)
	at org.mybatis.spring.SqlSessionTemplate.insert(SqlSessionTemplate.java:272)
	at org.apache.ibatis.binding.MapperMethod.execute(MapperMethod.java:62)
	at org.apache.ibatis.binding.MapperProxy$PlainMethodInvoker.invoke(MapperProxy.java:145)
	at org.apache.ibatis.binding.MapperProxy.invoke(MapperProxy.java:86)
	at com.sun.proxy.$Proxy96.batchAdd(Unknown Source)
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.lang.reflect.Method.invoke(Method.java:498)
	at org.springframework.aop.support.AopUtils.invokeJoinpointUsingReflection(AopUtils.java:344)
	at org.springframework.aop.framework.ReflectiveMethodInvocation.invokeJoinpoint(ReflectiveMethodInvocation.java:198)
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:163)
	at org.springframework.dao.support.PersistenceExceptionTranslationInterceptor.invoke(PersistenceExceptionTranslationInterceptor.java:137)
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:186)
	at org.springframework.aop.framework.JdkDynamicAopProxy.invoke(JdkDynamicAopProxy.java:215)
	at com.sun.proxy.$Proxy97.batchAdd(Unknown Source)
	at com.genersoft.iot.vmp.storager.impl.VideoManagerStorageImpl.resetChannels(VideoManagerStorageImpl.java:198)
	at com.genersoft.iot.vmp.storager.impl.VideoManagerStorageImpl$$FastClassBySpringCGLIB$$b5684f89.invoke(<generated>)
	at org.springframework.cglib.proxy.MethodProxy.invoke(MethodProxy.java:218)
	at org.springframework.aop.framework.CglibAopProxy.invokeMethod(CglibAopProxy.java:386)
	at org.springframework.aop.framework.CglibAopProxy.access$000(CglibAopProxy.java:85)
	at org.springframework.aop.framework.CglibAopProxy$DynamicAdvisedInterceptor.intercept(CglibAopProxy.java:704)
	at com.genersoft.iot.vmp.storager.impl.VideoManagerStorageImpl$$EnhancerBySpringCGLIB$$92c530b2.resetChannels(<generated>)
	at com.genersoft.iot.vmp.gb28181.session.CatalogDataCatch.timerTask(CatalogDataCatch.java:116)
	at sun.reflect.GeneratedMethodAccessor100.invoke(Unknown Source)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.lang.reflect.Method.invoke(Method.java:498)
	at org.springframework.scheduling.support.ScheduledMethodRunnable.run(ScheduledMethodRunnable.java:84)
	at org.springframework.scheduling.support.DelegatingErrorHandlingRunnable.run(DelegatingErrorHandlingRunnable.java:54)
	at java.util.concurrent.Executors$RunnableAdapter.call(Executors.java:511)
	at java.util.concurrent.FutureTask.runAndReset(FutureTask.java:308)
	at java.util.concurrent.ScheduledThreadPoolExecutor$ScheduledFutureTask.access$301(ScheduledThreadPoolExecutor.java:180)
	at java.util.concurrent.ScheduledThreadPoolExecutor$ScheduledFutureTask.run(ScheduledThreadPoolExecutor.java:294)
	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1142)
	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:617)
	at java.lang.Thread.run(Thread.java:745)
Caused by: org.apache.ibatis.binding.BindingException: Parameter 'addChannels' not found. Available parameters are [arg0, collection, list]
	at org.apache.ibatis.binding.MapperMethod$ParamMap.get(MapperMethod.java:212)
	at org.apache.ibatis.scripting.xmltags.DynamicContext$ContextAccessor.getProperty(DynamicContext.java:120)
	at org.apache.ibatis.ognl.OgnlRuntime.getProperty(OgnlRuntime.java:3356)
	at org.apache.ibatis.ognl.ASTProperty.getValueBody(ASTProperty.java:121)
	at org.apache.ibatis.ognl.SimpleNode.evaluateGetValueBody(SimpleNode.java:212)
	at org.apache.ibatis.ognl.SimpleNode.getValue(SimpleNode.java:258)
	at org.apache.ibatis.ognl.Ognl.getValue(Ognl.java:586)
	at org.apache.ibatis.ognl.Ognl.getValue(Ognl.java:550)
	at org.apache.ibatis.scripting.xmltags.OgnlCache.getValue(OgnlCache.java:46)
	at org.apache.ibatis.scripting.xmltags.ExpressionEvaluator.evaluateIterable(ExpressionEvaluator.java:54)
	at org.apache.ibatis.scripting.xmltags.ForEachSqlNode.apply(ForEachSqlNode.java:68)
	at org.apache.ibatis.scripting.xmltags.MixedSqlNode.lambda$apply$0(MixedSqlNode.java:32)
	at java.util.ArrayList.forEach(ArrayList.java:1249)
	at org.apache.ibatis.scripting.xmltags.MixedSqlNode.apply(MixedSqlNode.java:32)
	at org.apache.ibatis.scripting.xmltags.DynamicSqlSource.getBoundSql(DynamicSqlSource.java:39)
	at org.apache.ibatis.mapping.MappedStatement.getBoundSql(MappedStatement.java:305)
	at org.apache.ibatis.executor.statement.BaseStatementHandler.<init>(BaseStatementHandler.java:64)
	at org.apache.ibatis.executor.statement.PreparedStatementHandler.<init>(PreparedStatementHandler.java:41)
	at org.apache.ibatis.executor.statement.RoutingStatementHandler.<init>(RoutingStatementHandler.java:46)
	at org.apache.ibatis.session.Configuration.newStatementHandler(Configuration.java:681)
	at org.apache.ibatis.executor.SimpleExecutor.doUpdate(SimpleExecutor.java:48)
	at org.apache.ibatis.executor.BaseExecutor.update(BaseExecutor.java:117)
	at org.apache.ibatis.executor.CachingExecutor.update(CachingExecutor.java:76)
	at sun.reflect.GeneratedMethodAccessor109.invoke(Unknown Source)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.lang.reflect.Method.invoke(Method.java:498)
	at org.apache.ibatis.plugin.Plugin.invoke(Plugin.java:64)
	at com.sun.proxy.$Proxy167.update(Unknown Source)
	at org.apache.ibatis.session.defaults.DefaultSqlSession.update(DefaultSqlSession.java:194)
	at org.apache.ibatis.session.defaults.DefaultSqlSession.insert(DefaultSqlSession.java:181)
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.lang.reflect.Method.invoke(Method.java:498)
	at org.mybatis.spring.SqlSessionTemplate$SqlSessionInterceptor.invoke(SqlSessionTemplate.java:427)
	... 37 common frames omitted
```





