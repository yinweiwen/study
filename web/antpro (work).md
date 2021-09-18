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



## Redux

[阮一峰](https://www.ruanyifeng.com/blog/2016/09/redux_tutorial_part_one_basic_usages.html)

> "如果你不知道是否需要 Redux，那就是不需要它。"

设计思想

>（1）Web 应用是一个状态机，视图与状态是一一对应的。
>
>（2）所有的状态，保存在一个对象里面。



Store容器，整个应用只有一个store。

```js
import {createStore} from 'redux';
const store=createStore(fn);

const state=store.getState(); // state是store的一个快照。一个state对应一个view
```

Action是改变State的唯一方式（View -> Action -> State）

```js
const action={
    type:'ADD',
    payload:'good'
}
```

Action Creator函数生成Action

```js
export const RESIZE='RESIZE';
export function resize(hight,width){
    return {
        type:RESIZE,
        payload:{
            hight,
            width
        }
    }
}
```



store.dispatch() 是View发出Action的唯一方法

```js
store.dispath(resize(100,200));
```



Reducer:

Store收到Action后，调用reducer生产一个**新**的State。(新的state对象 Object.assign 或 {...state,...newState} 或 [...state,newItem] )

```js
const reducer=function(state=defaultState,action){
    switch(action.type){
        case 'ADD':
            return Object.assign(state,{a:"1"});
        default:
            return state;
    }
};
```

reducer是通过store.dispatch发布Action后自动调用的，所以Store需要知道Reducer的位置，这是在创建store的时候传递：

```js
import {createState} from 'redux';
const store=createStore(reducer);
```

各个React组件中写各自的reducer，通过`combineReducers`方法合并

```js
```

![img](imgs/antpro (work)/bg2016091802.jpg)



中间件：Middleware

是在`dispatch`过程中，添加其他处理步骤

例如添加logger中间件（redux-logger）

```js
import {applyMiddleware,createStore} from 'redux';
import createLogger from 'redux-logger';
const logger=createLogger();

const store = createStore(
	reducer,
    {}, // initial state
    applyMiddleware(thunk,promise,logger)
)
```



Redux-Thunk

```JS
const fetchPosts = postTitle=> (dispatch,getState) => {
    dispatch(requestPosts(postTitle));
    return fetch(`/home/API/${postTitle}.json`)
    	.then(response => response.json())
    	.then(json => dispatch(receivePosts(postTitle,json)));
};

// use
store.dispatch(fetchPosts('test'));
```

这里的Action Creator `fetchPosts`返回的是一个**函数**，参数是Redux的方法（dispatch和getstate）。store.dispatch只能发送对象，想用其发送这个函数，需要使用 Redux-thunk中间件



Redux-promise

同上，构造返回Promise对象的Action Creator。用此中间件可以适配。

```js
const fetchPosts=
      (dispatch,postTitle) => new Promise(function(resolve,reject){
          dispatch(requestPosts(postTitle));
          return fetch(`/home/API/${postTitle}.json`)
          	.then(response=>{
              type: 'FETCH_POSTS',
              payload: response.json()
          });
      });
```

或者通过createAction方法写

```js
const {dispatch,selectedPost} =this.props;
dispatch(requestPosts(selectedPost));
dispatch(createAction( // import {createAction} from 'redux-actions'
    'FETCH_POSTS', // TYPE
    fetch(`/home/API/${selectedPost}.json`)
    	.then(response=>response.json()) // Promise
));
```



React + Redux

UI组件和容器组件：

UI组件——只负责UI呈现，不带任何业务逻辑；没有状态，所有数据通过this.props提供。

容器组件——负责管理数据和业务逻辑，不负责UI呈现。带有内部状态state，使用Redux API

Redux的connect方法，负责将UI组件（用户的）自动生成容器组件： 使用两个函数

mapStateToProps —— 将状态（数据）变成用户组件的属性

mapDispatchToProps——用来建立UI组件的参数到dispatch方法的映射

```js
import {connect} from 'react-redux';

function mapStateToProps(state) {
    const {auth} = state;
    return {
        user: auth.user,
        error: auth.error,
        isRequesting: auth.isRequesting
    }
}

function mapDispatchToProps =(dispatch,ownProps)=>{
    return {
        onClick: ()=>{
            dispatch({
                type:'SET_VISIBLE',
                filter: ownProps.filter
            });
        }
    };
}

const VisibleTodoList = connect(
    mapStateToProps,
    mapDispatchToProps
)(TodoList);
```



Provider

通过provider组件向容器组件中传递 state。

```jsx
render(
    <Provider store={store}>
        <App/>
    </Provider>,
    document.getElementById('app')
)
```





## fs-scaffold

Free-sun脚手架介绍和使用记录

```js
.vscode：vscode配置文件(可选)
client：客户端代码根目录
    assets
    src
        components(通用化或定制化组件)
        layout(布局容器，组装sections)
        sections(app的各个模块)
            actions:redux中的action及actionCreator
            containers:redux中的容器组件
            component:redux中的呈现组件
            reducers:redux中的reducer
            index.js:入口，约定了section的接口
            routes:路由配置，兼容react-router
        utils(工具类)
        app.js(设置并组装layout和sections)
        index.js(入口)
    build
    index.html(单页应用html页面)
    index.js(用于node容器启动的静态页面入口)
middlewares：服务端koa中间件
routes：服务端路由
typings：typing智能感知(可选)
.babelrc：babel配置文件
config.js：服务端配置文件
config.js.tmpl：服务端配置文件小护士模板
jsconfig.json：vscode js配置文件(可选)
package.json：npm包配置文件
server.js：服务端启动脚本
webpack.config.js：webpack测试配置文件
webpack.config.prod.js：webpack发布配置文件
```



scaffold.js

```js
'use strict';
/*jslint node:true*/
//from koa
const http = require('http');
const Koa = require('koa');
const convert = require('koa-convert'); // 基于generator写法的中间件转为基于promise写法
const logger = require('koa-logger'); // koa日志
const session = require('koa-generic-session'); //  管理cookie and session
const bodyParser = require('koa-bodyparser'); // http body 解析
const csrf = require('koa-csrf'); // 防范跨站请求伪造攻击
const cors = require('koa-cors'); // 跨域资源共享
const statics = require('koa-static'); // 处理静态资源
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
    // 静态文件目录
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


配置文件 config.js

```js
/**
 * Created by rain on 2016/1/25.
 */

'use strict';
/*jslint node:true*/
const path = require('path');
/*这种以CommonJS的同步形式去引入其它模块的方式代码更加简洁:获取组件*/
const os = require('os');
const args = require('commander');

const dev = process.env.NODE_ENV == 'development';
// 启动参数
args.option('-p, --port <value>', 'server port')
    .option('-u, --api-url <value>', 'webapi service', 'http://localhost:8080')
    .parse(process.argv);

const P_APP_WEBAPI = process.env.SMART_SEAL_API || args.apiUrl;

if (!P_APP_WEBAPI) {
    console.log('缺少启动参数，异常退出');
    process.exit(-1);
}
global.apiUrl = P_APP_WEBAPI;
const product = {
    // 应用端口
    port: args.port || 8080,
    // 静态文件目录
    staticDirs: [path.join(__dirname, './client')],
    mws: [
    {
        // 代理中间件 
        entry: require('./middlewares/proxy').entry,
        opts: {
            host: P_APP_WEBAPI,
            match: /^\/_api\//,
        }
    },
     {
        // 路由管理
        entry: require('./routes').entry,
        opts: {
            apiUrl: P_APP_WEBAPI,
            staticRoot: './client',
            toposDir: '/assets/topos',
        }
    }, {
        entry: require('./client').entry, // 静态信息
        opts: {}
    }],
    // 日志配置
    logger: {
        level: 'debug',
        json: false,
        filename: path.join(__dirname, 'log', 'runtime.txt'),
        colorize: true,
        maxsize: 1024 * 1024 * 5,
        rotationFormat: false,
        zippedArchive: true,
        maxFiles: 10,
        prettyPrint: true,
        label: '',
        timestamp: true,
        eol: os.EOL,
        tailable: true,
        depth: null,
        showLevel: true,
        maxRetries: 1
    }
};

let config;
if (dev) {
    config = {
        port: product.port,
        staticDirs: product.staticDirs,
        mws: product.mws.concat([
            {
                entry: require('./middlewares/webpack-dev').entry,
                opts: {}
            }
        ]),
        logger: product.logger,
    }
    config.logger.filename = path.join(__dirname, 'log', 'development.txt');
} else {
    config = product;
}

module.exports = config; // 区分开发和发布
```

 代码结构

![image-20210724111633750](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\image-20210724111633750.png)

+ components 组件

+ layout 布局

+ sections 页面

+ utils 工具类，如

  + print log

  ```js
  let log = function(){
  /*
      ██████
     ███░░███
    ░███ ░░░  ████████   ██████   ██████              █████  █████ ████ ████████
   ███████   ░░███░░███ ███░░███ ███░░███ ██████████ ███░░  ░░███ ░███ ░░███░░███
  ░░░███░     ░███ ░░░ ░███████ ░███████ ░░░░░░░░░░ ░░█████  ░███ ░███  ░███ ░███
    ░███      ░███     ░███░░░  ░███░░░              ░░░░███ ░███ ░███  ░███ ░███
    █████     █████    ░░██████ ░░██████             ██████  ░░████████ ████ █████
   ░░░░░     ░░░░░      ░░░░░░   ░░░░░░             ░░░░░░    ░░░░░░░░ ░░░░ ░░░░░
  
  
  */  
  }
  
  let print_log = ()=>{
    let lines = new String(log); // 获取注释文字🤞
    lines = lines.substring(lines.indexOf("/*") + 3, lines.lastIndexOf("*/"));
    let co = `\n ©2010-`+new Date().getFullYear()+ ` 飞尚科技`;
  
    console.log(lines + co)
  
  }
  
  export default print_log;
  ```

  + regexp

    ```js
    /* eslint-disable import/no-mutable-exports */
    // 常见的 正则表达式 校验
    // QQ号、手机号、Email、是否是数字、去掉前后空格、是否存在中文、邮编、身份证、URL、日期格式、IP
    export const myRegExp = {
        // 检查字符串是否为合法QQ号码
        isQQ: function (str) {
            // 1 首位不能是0 ^[1-9]
            // 2 必须是 [5, 11] 位的数字 \d{4, 9}
            let reg = /^[1-9][0-9]{4,9}$/gim;
            if (reg.test(str)) {
                console.log('QQ号码格式输入正确');
                return true;
            } else {
                console.log('请输入正确格式的QQ号码');
                return false;
            }
        },
        // 检查字符串是否为合法手机号码
        isPhone: function (str) {
            let reg = /^(0|86|17951)?(13[0-9]|15[012356789]|18[0-9]|14[57]|17[678])[0-9]{8}$/;
            if (reg.test(str)) {
                console.log('手机号码格式输入正确');
                return true;
            } else {
                console.log('请输入正确格式的手机号码');
                return false;
            }
        },
        // 检查字符串是否为合法Email地址
        isEmail: function (str) {
            let reg = /^\w+((-\w+)|(\.\w+))*\@[A-Za-z0-9]+((\.|-)[A-Za-z0-9]+)*\.[A-Za-z0-9]+$/;
            if (reg.test(str)) {
                console.log('Email格式输入正确');
                return true;
            } else {
                console.log('请输入正确格式的Email');
                return false;
            }
        },
        // 检查字符串是否是数字
        isNumber: function (str) {
            let reg = /^\d+$/;
            if (reg.test(str)) {
                console.log(str + '是数字');
                return true;
            } else {
                console.log(str + '不是数字');
                return false;
            }
        },
        // 去掉前后空格
        trim: function (str) {
            let reg = /^\s+|\s+$/g;
            return str.replace(reg, '');
        },
        // 检查字符串是否存在中文
        isChinese: function (str) {
            let reg = /[\u4e00-\u9fa5]/gm;
            if (reg.test(str)) {
                console.log(str + ' 中存在中文');
                return true;
            } else {
                console.log(str + ' 中不存在中文');
                return false;
            }
        },
        // 检查字符串是否为合法邮政编码
        isPostcode: function (str) {
            // 起始数字不能为0，然后是5个数字 [1-9]\d{5}
            let reg = /^[1-9]\d{5}$/g;
            if (reg.test(str)) {
                console.log(str + ' 是合法的邮编格式');
                return true;
            } else {
                console.log(str + ' 是不合法的邮编格式');
                return false;
            }
        },
        // 检查字符串是否为合法身份证号码
        isIDcard: function (str) {
            let reg = /^(^[1-9]\d{7}((0\d)|(1[0-2]))(([0|1|2]\d)|3[0-1])\d{3}$)|(^[1-9]\d{5}[1-9]\d{3}((0\d)|(1[0-2]))(([0|1|2]\d)|3[0-1])((\d{4})|\d{3}[Xx])$)$/;
            if (reg.test(str)) {
                console.log(str + ' 是合法的身份证号码');
                return true;
            } else {
                console.log(str + ' 是不合法的身份证号码');
                return false;
            }
        },
        // 检查字符串是否为合法URL
        isURL: function (str) {
            let reg = /^https?:\/\/(([a-zA-Z0-9_-])+(\.)?)*(:\d+)?(\/((\.)?(\?)?=?&?[a-zA-Z0-9_-](\?)?)*)*$/i;
            if (reg.test(str)) {
                console.log(str + ' 是合法的URL');
                return true;
            } else {
                console.log(str + ' 是不合法的URL');
                return false;
            }
        },
        // 检查字符串是否为合法日期格式 yyyy-mm-dd
        isDate: function (str) {
            let reg = /^[1-2][0-9][0-9][0-9]-[0-1]{0,1}[0-9]-[0-3]{0,1}[0-9]$/;
            if (reg.test(str)) {
                console.log(str + ' 是合法的日期格式');
                return true;
            } else {
                console.log(str + ' 是不合法的日期格式，yyyy-mm-dd');
                return false;
            }
        },
        // 检查字符串是否为合法IP地址
        isIP: function (str) {
            // 1.1.1.1 四段 [0 , 255]
            // 第一段不能为0
            // 每个段不能以0开头
            //
            // 本机IP: 58.50.120.18 
            let reg = /^([1-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])(\.([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])){3}$/gi;
            if (reg.test(str)) {
                console.log(str + ' 是合法的IP地址');
                return true;
            } else {
                console.log(str + ' 是不合法的IP地址');
                return false;
            }
        }
    };
    ```

  + webapi helper

    ```js
    // sessionStorage中取出user的token，拼接到请求url后
    export const buildUrl = (url)=>{
        const apiurl=`/${rootUrl}/${url}`;
        const user = JSON.parse(sessionStorage.getItem('user'));
        if(user == null){
            return apiurl;
        }
        let connector=url.indexOf('?') ===-1?'?':'&';
        return `${apiurl}${connector}token=${user.token}`;
    }
    
    // 路由请求失败处理，跳转到登录
    const resultHandler = (resolve,reject) => (err,res)=>{
        if(err){
            if(err.status==401){
                // unauth
                const user = JSON.parse(sessionStorage.getItem('user'));
                sessionStorage.clear();
                windows.document.location.replace('/login');
                reject('unauth');
            }else{
                reject({
                    status: err.status || 0,
                    body: err.response ? err.response.body:err.message
                });
            }
            reject({
                status: err.status || 0,
                body: err.response ? err.response.body:err.message
            });
        }else{
            resolve(res.body);
        }
    }
    
    export const ApiTable = {
        /* 登录 */
        login: 'login',
        logout: 'logout',
        getVerifyCodeUrl: 'verification-code',
        /* 首页 */
        
    }
    
    export class Request{
        static get = (url,query) =>
        	new Promise((resolve,reject)=>{
            request.get(buildUrl(url)).query(query).end(resultHandler(resolve,reject));
        })
    }
    
    ```

    


### Layout

app.js

```js
return (
            <Layout title={this.props.projectName} sections={sections}/>
        );
```



layout.jsx

```jsx
'use strict';
import React from 'react';
import Immutable from 'immutable';
import moment from 'moment';
import 'moment/locale/zh-cn';
moment.locale('zh-cn');
import { render } from 'react-dom';
import { Provider } from 'react-redux';
import { Router, browserHistory } from 'react-router';
import { syncHistoryWithStore } from 'react-router-redux';
import { Layout, NoMatch } from './containers';
import configStore from './store';
import { initLayout } from './actions/global';

class Root extends React.Component {
    constructor(props) {
        super(props);
    }

    render() {
        /**
            export default {
            key: 'auth',  //    主键    
            reducers: reducers,  // function auth(state=initState,action) -- state通过action转换的纯函数
            routes: routes, // 路由 -- {type: 'outer', route: {path:"signin", component: Login }} -- Login是一个React组件
            actions: actions, // 一组动作，例如API请求，最终触发dispatch（{type: LOGIN_SUCCESS, payload: {user: user}});）
            getNavItem: getNavItem // ??
        };
        */
        let routes = this.props.sections // routes集合
            .reduce((p, c) => {
                return p.concat(c.routes);
            }, []);

        let innerRoutes = routes // inner routes集合
            .filter(route => {
                return route.type === 'inner';
            })
            .map(r => {
                return r.route;
            });

        let homeRoutes = routes // home routes集合
            .filter(r=> {
                return r.type === 'home';
            }).map(r => {
                return r.route;
            });

         // inner+home routes集合
        let combinedInnerRoutes = innerRoutes.concat(homeRoutes);

        let outerRoutes = routes  // outer routes集合
            .filter(s=> {
                return s.type === 'outer';
            }).map(r => {
                return r.route;
            });

        let homePage = homeRoutes[0].component; // 首页组件

        // 根路由
        let rootRoute = {
            component: 'div',
            childRoutes: [
                {
                    path: '/',
                    component: Layout, // container中的layout组件
                    indexRoute: {component: homePage}, // 首页
                    childRoutes: combinedInnerRoutes // 内部组件？
                },
                ...outerRoutes,
                {
                    path: '*',
                    component: NoMatch
                }
            ]
        };

        // 所有reducers
        let reducers = this.props.sections.reduce((p, c) => {
            return Object.assign(p, c.reducers);
        }, {});
		// 所有actions
        let actions = this.props.sections.reduce((p, c) => {
            let action = {};
            if(!c.key) console.warn('请给你的section添加一个key值，section name:' + c.name);
            action[c.key] = c.actions;
            return Object.assign(p, action);
        }, {});

        // react-router中的browserHistory
        let store = configStore(reducers, browserHistory);

        const {sections, title, copyright} = this.props;
        store.dispatch(initLayout(title, copyright, sections, actions));
        store.dispatch(actions.auth.auth.initAuth());

        const history = syncHistoryWithStore(browserHistory, store);

        return (
            <Provider store={store}>
                <Router history={history} routes={rootRoute}/>
            </Provider>
        );
    }
}

export default Root;
```



## [Co](https://github.com/tj/co#readme)

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



## 例子

### Redux

index.html

```html
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title></title>
    <link rel="shortcut icon" href="assets/images/favicon.ico">
</head>
<body>
<div id='App'></div>
<script type="text/javascript" src="index.js"></script>
</body>
</html>

```

index.js

```jsx
import React,{Component} from 'react';
import PropTypes from 'prop-types';
import ReactDOM from 'react-dom';
import {createStore} from 'redux';
import {Provider,connect} from 'react-redux';

class Counter extends Component {
    render(){
        const {value,onIncreaseClick}=this.props;
        return (
            <div>
            	<span>{value}</span>
                <button onClick={onIncreaseClick}>+1</button>
            </div>
        )
    }
}

Counter.propTypes={
    value: PropTypes.number.isRequired,
    onIncreaseClick: PropTypes.func.isRequired
}

const increaseAction ={type:'increase'}

function counter(state={count:0},action){
    const count=state.count;
    switch(action.type){
        case 'increase':
            return {count:count+1};
        default:
            return state;
    }
}

const store=createStore(counter);

function mapStateToProps(state){
    return {
        value:state.count
    }
}

function mapDispatchToProps(dispatch){
    return {
        onIncreaseClick: ()=>dispatch(increaseAction)
    }
}

const App=connect(
    mapStateToProps,
    mapDispatchToProps
)(Counter)

ReactDOM.render(
    <Provider store={store}>
        <App/>
    </Provider>,
    document.getElementById('app')
)
```
