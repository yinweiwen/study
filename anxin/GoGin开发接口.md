本文介绍通过Go实现传统WEB-API开发，尽量兼容公司原Nodejs接口规范和开发公约，Gin类似原nodejs中koa组件，gorm类似原sequelize组件。

## Gin

Quick Start

```golang
r := gin.New()
// 日志系统
r.Use(gin.Logger())
// CORS
r.Use(CORS())
// Recovery middleware recovers from any panics and writes a 500 if there was one.
r.Use(gin.CustomRecovery(func(c *gin.Context, recovered interface{}) {
    if err, ok := recovered.(string); ok {
        c.String(http.StatusInternalServerError, fmt.Sprintf("error: %s", err))
    }
    c.AbortWithStatus(http.StatusInternalServerError)
}))
// k8s嗅探接口
root.GET("/ping", controller.PingEndpoint)
apiUrl, _ := props.GetOrEnv("api.url", ":4100")
_ = r.Run(apiUrl)
```



所有路由的EndPoint按如下格式：

```golang
func PingEndpoint(c *gin.Context) {
	c.JSON(http.StatusOK, gin.H{
		"message": "pong",
	})
}
```



### Middlewares

#### 1.鉴权中间件

通过token方式进行鉴权。每次请求上下文中获取token，以其为key在redis中的查找对应授权信息。验证成功后将user信息放入Gin上下文中。

```go
func Auth(c *gin.Context) {
	path := c.Request.URL.Path

	// Auth only when path is not being skipped
	if _, ok := skipPaths[path]; !ok {
		token := c.Query("token")
		if token != "" && TokenReg.MatchString(token) {
			var user model.User
			if u, ok := PersistentUsers[token]; ok {
				user = u
			} else {
				var tk string
				err := global.GCache.Get(token, &tk)
				if err != nil || tk == "" {
					ginutil.GinErr(c, ginutil.ERR_UNAUTHORIZED)
					c.Abort()
					return
				}
				if err = json.Unmarshal([]byte(tk), &user); err != nil {
					log.Info("[auth] json unmarshal failed, %s", tk)
					ginutil.GinErr(c, ginutil.ERR_UNAUTHORIZED)
					c.Abort()
					return
				}
			}
			// 鉴权通过
			c.Set("user", &user)
			c.Set("token", token)
		} else {
			ginutil.GinErr(c, ginutil.ERR_UNAUTHORIZED)
			c.Abort()
			return
		}
	}

	// Process request
	c.Next()
}
```



#### 2.权限中间件

必须符合RESTApi路由规范。如下/edge/:edgeId/info , edgeId标识EDGE的资源ID，该中间件中获取到该资源标志后查找当前用户是否对该资源有操作权限。

```go
func PermissionCheck(c *gin.Context) {
	u := c.MustGet("user").(*model.User)
	for _, param := range c.Params {
		switch param.Key {
		case "edgeId":
			if flag := permissionEdge(c, u, param.Value); !flag {
				c.Abort()
				return
			}
		}
	}
	c.Next()
}
```



#### 3.日志中间件

默认使用Gin内置的logger

```go
// 日志系统
r.Use(gin.Logger())
```

可以自定义日志处理，例如如下将日志存储到influxdb：

```go
func Logger() gin.HandlerFunc {
	return func(c *gin.Context) {
		// Start timer
		start := time.Now()
		path := c.Request.URL.Path
		raw := c.Request.URL.RawQuery

		c.Next()

		if c.Request.Method == http.MethodPost ||
			c.Request.Method == http.MethodPut ||
			c.Request.Method == http.MethodPatch ||
			c.Request.Method == http.MethodDelete {
			var userId uint
			var rootUser uint
			var thingId uint
			if obj, ok := c.Get("user"); ok {
				u := obj.(model.User)
				userId = u.ID
				rootUser = u.RootId()
			}
			if obj, ok := c.Get("fox"); ok {
				fox := obj.(*model.FoxContext)
				thingId = fox.Thing.ID
			}
			log := utils.FoxLog{
				User:     userId,
				RootUser: rootUser,
				Thing:    thingId,
				Method:   c.Request.Method,
			}
			log.Time = time.Now()
			log.Took = int64(log.Time.Sub(start) / time.Millisecond)
			log.IP = c.ClientIP()
			if raw != "" {
				path = path + "?" + raw
			}

			log.Path = path
			bts, _ := ioutil.ReadAll(c.Request.Body)
			if bts != nil {
				log.Params = string(bts)
			}
			log.Status = c.Writer.Status()
			log.ResponseCode = c.GetInt("ret_code")
			log.ResponseMsg = c.GetString("ret_msg")
			go utils.GInflux.WriteLog(log)
		}
		return
	}
}
```



#### 4.跨域访问

为支持跨域访问当前api地址，需要将响应访问头中加入关键标志：

```go
func CORS() gin.HandlerFunc {
	// TO allow CORS
	return func(c *gin.Context) {
		c.Writer.Header().Set("Access-Control-Allow-Origin", "*")
		c.Writer.Header().Set("Access-Control-Allow-Headers", "Content-Type, Content-Length, Accept-Encoding, X-CSRF-Token, Authorization, accept, origin, Cache-Control, X-Requested-With")
		c.Writer.Header().Set("Access-Control-Allow-Methods", "POST, OPTIONS, GET, PUT, DELETE")
		if c.Request.Method == "OPTIONS" {
			c.AbortWithStatus(204)
			return
		}
		c.Next()
	}
}
```



## Gorm

Gorm是一个golang的[ORM](https://gorm.io/zh_CN/docs/index.html)框架

- 全功能 ORM
- 关联 (Has One，Has Many，Belongs To，Many To Many，多态，单表继承)
- Create，Save，Update，Delete，Find 中钩子方法
- 支持 `Preload`、`Joins` 的预加载
- 事务，嵌套事务，Save Point，Rollback To Saved Point
- Context、预编译模式、DryRun 模式
- 批量插入，FindInBatches，Find/Create with Map，使用 SQL 表达式、Context Valuer 进行 CRUD
- SQL 构建器，Upsert，数据库锁，Optimizer/Index/Comment Hint，命名参数，子查询
- 复合主键，索引，约束
- Auto Migration
- 自定义 Logger
- 灵活的可扩展插件 API：Database Resolver（多数据库，读写分离）、Prometheus…
- 每个特性都经过了测试的重重考验
- 开发者友好

示例：

1. 定义连接

   ```golang
   HRM, err = gorm.Open(postgres.Open(utils.GIniParser.GetString("db", "dsn_hrm")), &gorm.Config{
   		NamingStrategy: NewHrmNamingStrategy(schema.NamingStrategy{
   			//TablePrefix: "t_",   // table name prefix, table for `User` would be `t_users`
   			SingularTable: true,  // use singular table name, table for `User` would be `user` with this option enabled
   			NoLowerCase:   false, // skip the snake_casing of names
   			//NameReplacer: strings.NewReplacer("CID", "Cid"),
   		}),
   	})
   ```

   如上例中，我们数据库中表结构的列名称是全部小写，故重新定义了一种命名策略 HrmNamingStrategy:

   ```go
   // 继承自 schema.NamingStrategy
   type HrmNamingStrategy struct {
   	schema.NamingStrategy
   }
   
   func NewHrmNamingStrategy(ns schema.NamingStrategy) HrmNamingStrategy {
   	return HrmNamingStrategy{
   		NamingStrategy: ns,
   	}
   }
   // 列命名规则
   func (h HrmNamingStrategy) ColumnName(table, column string) string {
   	return strings.ToLower(column)
   }
   ```

   

2. 定义模型

   定义一个golang的struct, 通过gorm标签定义ORM的相关属性

   ```go
   // 个人培训表
   type PersonalTraining struct {
      Id              int       `gorm:"primaryKey"`
      PersonalName    string    `json:"personalName"`    // 姓名
      DepartmentName  string    `json:"departmentName"`  // 部门名称
      TrainingType    string    `json:"trainingType"`    // 培训类型
      Topic           string    `json:"topic"`           //课程主题
      Trainer         string    `json:"trainer"`         //培训讲师
      TrainDate       time.Time `json:"trainDate"`       //培训时间
      TrainTime       string    `json:"trainTime"`       //培训时长
      TrainMethod     string    `json:"trainMethod"`     // 培训方式
      AttendanceScore string    `json:"attendanceScore"` // 考勤分数
      AppraisalMethod string    `json:"appraisalMethod"` // 考核形式
      AppraisalScore  string    `json:"appraisalScore"`  //考核分数
      TotalScore      string    `json:"totalScore"`      //总分
      Origin          string    `json:"origin"`          // 来源
   
      DepartmentId int   `json:"department" gorm:"-"`
      TrainerId    []int `json:"trainerId" gorm:"-"`
   }
   ```

   

3. 查询

   通过Find查找多条记录，通过First、Last、Take查找一条记录，Where子句包含多种写法，这里通过结构体加制定字段的方式，如下：

   ```go
   res := ds.HRM.Where(&train, "personalname", "departmentname", "topic", "traindate").Find(&olds)
   if res.Error != nil {
       log.Warn("[sync][%s] get old error %s. %s", config.Key, res.Error.Error(), string(bts))
   }
   ```

   

4. 插入

   通过create创建一条记录，字段Id定义为自增主键，不需要制定

   ```go
   ds.HRM.Create(&train)
   ```

   同时，save命令可以执行更新记录，默认根据主键：

   ```go
   train.Id = olds[0].Id
   res = ds.HRM.Save(&train)
   ```

   

5.  更多

   参考 【https://gorm.io/zh_CN/docs/index.html】







