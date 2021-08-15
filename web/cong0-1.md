# Cong0-WEB-01

## 准备工作

安装Node.js

`fashion`脚手架

> npm set config registry=http://***.105:7000
```shell
npm install -g yo
npm install -g generator-fs-app

yo fs-app
```


安装VSCODE
> [vscode.cdn.azure.cn](https://vscode.cdn.azure.cn/stable/c185983a683d14c396952dd432459097bc7f757f/VSCodeUserSetup-x64-1.55.0.exe)


脚手架目录结构：
```
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

## 做(chao)一个Login界面

编写routes
```js
'use strict';

import { Login } from './containers';

export default [
    { type: 'outer', route: { path: "login", component: Login } }
];

// type : home / inner(layout内容页) / outer (脱离layout)
```

编写Container
```js
class LoginContainer extends React.Component {
}

function mapStateToProps(state) {
    const { auth } = state;
    return {
        user: auth.user,
        error: auth.error,
        logining: auth.isRequesting
    }
}

export default connect(mapStateToProps)(LoginContainer);
```






