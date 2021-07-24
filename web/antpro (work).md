*AntPro文档的工作副本*

前端基础学习：[MDN](https://developer.mozilla.org/zh-CN/docs/Web/Guide)

UMI API学习[UMI](https://umijs.org/docs/routing)

Antd Pro Design组件学习 [Pro Components](https://procomponents.ant.design/components/)

## 使用步骤

安装tyarn (官方推荐)

```
npm install yarn tyarn -g
```

初始化

```sh
tyarn create umi myapp
```

安装依赖

```sh
cd myapp && tyarn
```

开启 Umi UI（可选）：

```sh
tyarn add @umijs/preset-ui -D
```



使用cnpm

```shell
npm install -g cnpm --registry=https://registry.npm.taobao.org

```





## Koa

```shell
npm i koa
```

执行listen

```js
app.listen(3000)
==
http.createServer(app.callback()).listen(3000);
```

剥洋葱的例子：

```js
const Koa = require('koa');
const app = new Koa();

console.log("hello");
// logger

app.use(async (ctx, next) => {
  await next();
  console.log('logger');
  const rt = ctx.response.get('X-Response-Time');
  console.log(`${ctx.method} ${ctx.url} - ${rt}`);
});

// x-response-time

app.use(async (ctx, next) => {
  const start = Date.now();
  await next();
  console.log('x-response-time');
  const ms = Date.now() - start;
  ctx.set('X-Response-Time', `${ms}ms`);
});

// response

app.use(async ctx => {
  ctx.body = 'Hello World';
  console.log('response');
});

app.listen(3000);
```



设置：

- `app.env` 默认是 **NODE_ENV** 或 "development"
- `app.keys` 签名的 cookie 密钥数组
- `app.proxy` 当真正的代理头字段将被信任时
- 忽略 `.subdomains` 的 `app.subdomainOffset` 偏移量，默认为 2
- `app.proxyIpHeader` 代理 ip 消息头, 默认为 `X-Forwarded-For`
- `app.maxIpsCount` 从代理 ip 消息头读取的最大 ips, 默认为 0 (代表无限)



`app.use` 使用扩展的中间件 (支持链式表达式)

```js
app.use(someMiddleware)
```



通过 `app.context` 将内容添加到整应用程序中 `ctx`

```js
app.context.db=db();

app.use(async ctx => {
  console.log(ctx.db);
});
```



定义自己的错误处理：

```js
app.on('error', (err, ctx) => {
  log.error('server error', err, ctx)
});
```



上下文Context中API

```js
ctx.req // Node 的 request 对象
ctx.res // Node 的 response对象
ctx.request  // Koa 的Request 对象
ctx.response // Koa 的Response对象
ctx.app // 应用程序实例引用
ctx.app.emit // 扩展了内部EventEmitter 通过listeners订阅消息
// cookies
ctx.cookies.get(name,[signed])
ctx.cookies.set(name,value,[options])
ctx.throw(status,msg,properties)
// ctx.throw(400,'msg') 等同于
        const err = new Error('name required');
        err.status = 400;
        err.expose = true;
        throw err;
ctx.assert(bool,status,msg,properties)
```



## fs-scaffold

```js
/**
 * Created by rain on 2015/11/6.
 */

'use strict';
/*jslint node:true*/
//from koa
const http = require('http');
const Koa = require('koa');
const convert = require('koa-convert'); // 跨域资源共享
const logger = require('koa-logger');
const session = require('koa-generic-session');
const bodyParser = require('koa-bodyparser');
const csrf = require('koa-csrf');
const cors = require('koa-cors');
const statics = require('koa-static');
const Router = require('koa-66'); // Koa 路由管理
const io = require('socket.io');

//util
const co = require('co'); // 异步

//from fs
const fsDc = require('fs-dc'); // 数据库操作 封装sequelize
const fsLogger = require('fs-logger'); // 

function scaffold(config) {
    const defaultConfig = require('./config.js');
    const app = new Koa();
    csrf(app);
    const router = new Router();
    config = config || defaultConfig;
    app.keys = ["it is a secret"];
    app.use(convert(cors({
        credentials: true
    })));
    app.use(co.wrap(errorHandler()));
    if (config.staticDirs && Array.isArray(config.staticDirs)) {
        config.staticDirs.forEach(function (s) {
            app.use(convert(statics(s)));
        });
    }
    app.use(convert(logger()));
    app.use(convert(session({ key: 'fs-sid', cookie: { maxAge: null } }, app)));
    app.use(convert(bodyParser({
        'formLimit': '80mb',
        'jsonLimit': '80mb',
        'textLimit': '80mb',
    })));
    //init fs.logger and inject it into app(app.fs.logger) and runtime ctx(ctx.fs.logger)
    // 注入fs日志模块 到 应用程序实例app和运行时上下文ctx
    app.use(co.wrap(fsLogger(app, config.logger)));
    //init fs.dc and inject it into app(app.fs.dc) and runtime ctx(app.fs.dc)
    // 注入fs数据库模块 到 应用程序实例app和运行时上下文ctx
    if (config.dc) {
        app.use(co.wrap(fsDc(app, config.dc)));
    }

    const socketRegEvent = []
    app.socket = {
        on: function(evt, callback) {
            socketRegEvent.push([evt, callback]);
        }
    }

    config.mws.forEach(function (mv) {
        if (typeof mv.entry === 'function') {
            try {
                mv.entry(app, router, mv.opts);
            } catch (err) {
                app.fs.logger.log('error', '[app]', err)
            }
        }
    });

    app.use(router.routes());

    function errorHandler() {
        return function* (ctx, next) {
            try {
                yield next();
            } catch (err) {
                app.fs.logger.log("error", "[FS-ERRHD]", err);
                //simple process.
                //@Todo 500 page; 400...
                ctx.status = 500;
                ctx.body = 'internal server error';
            }
        };
    }

    const server = http.createServer(app.callback());
    const socket = io(server);

    app.socket = socket;

    socketRegEvent.forEach(function(reg) {
        socket.on(reg[0], reg[1]);
    });

    app.server = server.listen(config.port || 4000);
    // app.server = app.listen(config.port || 4000);
    //for test
    app.router = router;
    return app;
}

module.exports = scaffold;
```



### [Co](https://github.com/tj/co#readme)

Co模块--以同步的形式编写异步代码的 nodejs 模块

先了解ES6中的iterator/generator.

iterator：

```js
var lang={name:'js',age:18};
var it=Iterator(lang);
var pair = it.next();
console.log(pair); // ["name","js"]
pair = it.next();
console.log(pair); // ["age",18]
```

generator：

```js
function *gen(){
    yield 'hello';
    yield 'world';
    return true;
}

let iter=gen();
var a=iter.next();
console.log(a); // {value:'hello',done:false}
a=iter.next();
console.log(a); // {value:'world',done:false}
a=iter.next();
console.log(a); // {value:true,done:false}
```



co: 生成一个迭代器，再执行next

```js
var co = require('co');
co(function *(){
   yield syshello();
   yield sayworld();
   yield saybye();
});
```

