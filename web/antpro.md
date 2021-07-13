## 使用

### 技术文档归总

[Ant Pro](https://pro.ant.design/docs/getting-started-cn)

[Umi Quick start](https://umijs.org/zh-CN/docs/getting-started)

[Umi UI](https://umijs.org/zh-CN/docs/use-umi-ui)

[Dva](https://dvajs.com/guide/)

>  环境说明：aliyun ECS CentOS7 ; nodejs 16.4.1

### 开始使用

```shell
npm create umi
选择ant-design-pro
```

目录结构

```
├── config                   # umi 配置，包含路由，构建等配置
├── mock                     # 本地模拟数据
├── public
│   └── favicon.png          # Favicon
├── src
│   ├── assets               # 本地静态资源
│   ├── components           # 业务通用组件
│   ├── e2e                  # 集成测试用例
│   ├── layouts              # 通用布局
│   ├── models               # 全局 dva model
│   ├── pages                # 业务页面入口和常用模板
│   ├── services             # 后台接口服务
│   ├── utils                # 工具库
│   ├── locales              # 国际化资源
│   ├── global.less          # 全局样式
│   └── global.ts            # 全局 JS
├── tests                    # 测试工具
├── README.md
└── package.json
```

启动

```shell
npm install
npm start
```



### 问题汇总

1. 出现无法解析react

```sh
npm i --legacy-peer-deps
npm install
```



## 开发

### 通过Umi UI 区块开发（未使用）

```shell
# 国内源
npm i yarn tyarn -g
tyarn -v

yarn create @umijs/umi-app
# 安装依赖
yarn

# 启动项目
yarn start

yarn global add umi

$ yarn add @umijs/preset-ui -D
$ UMI_UI=1 umi dev

```



## WEBAPI

使用飞尚科技的webapi脚手架创建WEBAPI

### webapi脚手架搭建：

step 1：

step 2：

step 3：

step 4：



### 前端调试

修改config/proxy.js

```js
export default {
  dev: {
    '/api/': {
      target: 'http://localhost:3000',
      changeOrigin: true,
      pathRewrite: {
        '^/api': '',
      },
    },
  }
}
```

> 注意：proxy配置只针对开发和测试环境，商用环境通过nginx等方式实现跨域访问

运行no-mock数据模式

```sh
npm run start:no-mock
```



### 数据库模型

自动生产sequelize models

```shell
npm install -g freesun-sequelize-automate
```

sequelize-automate.config.js

```js
module.exports = {
    // 数据库配置 与 sequelize 相同
    dbOptions: {
        database: 'db',
        username: 'root',
        password: '123456',
        dialect: 'mysql',
        host: 'localhost',
        port: 3306,
        define: {
            underscored: false,
            freezeTableName: false,
            charset: 'utf8mb4',
            timezone: '+00:00',
            dialectOptions: {
                collate: 'utf8_general_ci',
            },
            timestamps: false,
        },
    },
    options: {
    type: 'freesun', // 指定 models 代码风格
    camelCase: true, // Models 文件中代码是否使用驼峰命名
    modalNameSuffix: false, // 模型名称是否带 ‘Model’ 后缀
    fileNameCamelCase: true, // Model 文件名是否使用驼峰法命名，默认文件名会使用表名，如 `user_post.js`；如果为 true，则文件名为 `userPost.js`
    dir: './model', // 指定输出 models 文件的目录
    typesDir: 'models', // 指定输出 TypeScript 类型定义的文件目录，只有 TypeScript / Midway 等会有类型定义
    emptyDir: false, // ！！！ 谨慎操作 生成 models 之前是否清空 `dir` 以及 `typesDir`
    tables: null, // 指定生成哪些表的 models，如 ['user', 'user_post']；如果为 null，则忽略改属性
    skipTables: null, // 指定跳过哪些表的 models，如 ['user']；如果为 null，则忽略改属性
    tsNoCheck: false, // 是否添加 `@ts-nocheck` 注释到 models 文件中
    ignorePrefix: [ 'ncbs_' ], // 生成的模型名称忽略的前缀，因为 项目中有以下表名是以 t_ 开头的，在实际模型中不需要， 可以添加多个 [ 't_data_', 't_',] ,长度较长的 前缀放前面
    attrLength: false, // 在生成模型的字段中 是否生成 如 var（128）这种格式，公司一般使用 String ，则配置为 false
  },
}
```

执行 `npm run automate`

```json
{
    "scripts":{
        "automate": "sequelize-automate -c sequelize-automate.config.js"
    }
}
```



## 发布

### 发布到我的阿里云

npm run build构建到dist目录

登录阿里云服务器(还是恋情XShell)

`106.15.89.47 root/******`

安装和配置httpd

```sh
# 安装 httpd
yum -y install httpd httpd-manual mod_ssl mod_perl mod_auth_mysql

# 配置httpd
vi /etc/httpd/conf/httpd.conf 
# 默认端口80

# 启动httpd
systemctl restart httpd
```

发布我的网站

```sh

#拷贝dist文件到/var/www/html 目录
unzip dist.zip
mv dist/* ./html/

# 启动 httpd
systemctl restart httpd
```

发布api

```sh
npm install --production
# 拷贝到服务器上

#安装nodejs
 wget https://npm.taobao.org/mirrors/node/v16.4.1/node-v16.4.1-linux-x64.tar.xz
 xz -d node-v16.4.1-linux-x64.tar.xz
 tar -xf node-v16.4.1-linux-x64.tar
 ln -s ~/node-v16.4.1-linux-x64/bin/node /usr/bin/node
 ln -s ~/node-v16.4.1-linux-x64/bin/npm /usr/bin/npm
 ln -s ~/node-v16.4.1-linux-x64/bin/npx /usr/bin/npx
 
# start_api.sh
nohup node api/server.js -p 9080 -g mysql://root:Aliyun123_@localhost:3306/db > ./api/log.log 2>&1 &

```

### 域名

1.在华为云上购买的域名(华为云账户-手机号)

2.按照阿里云备案提示进行备案

3.备案完成后进入华为云，网站解析

![image-20210707193435319](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\image-20210707193435319.png)

配置Nginx反向代理：

参考[Nginx](#Nginx)章节



访问：http://yinweiwen.cn





> 以下内容为其他技术的学习笔记，比较零散，后续总结归档

## DvaJS

Quick start

```sh
npm install dva-cli -g
dva new demo

# babel-plugin-import 按需加载
npm install antd babel-plugin-import --save
npm start

```

.webpackrc 使babel-plugin生效

```json
{
  "extraBabelPlugins": [
    ["import", { "libraryName": "antd", "libraryDirectory": "es", "style": "css" }]
  ]
}
```

[官网例子](https://dvajs.com/guide/getting-started.html)再敲一遍

`routes/Products.js`

```js
import React from 'react';

const Products=(props)=>(
	<h2>List of Products1</h2>
);

export default Products;
```

`router.js`

```js
import React from 'react';
import { Router, Route, Switch } from 'dva/router';
import IndexPage from './routes/IndexPage';
import Products from './routes/Products';

function RouterConfig({ history }) {
  return (
    <Router history={history}>
      <Switch>
        <Route path="/" exact component={IndexPage} />
        <Route path="/products" exact component={Products} />
      </Switch>
    </Router>
  );
}

export default RouterConfig;

```

1. components目录中定义组件

2. models目录下定义模型(加入index)

3. connect
4. index中初始化

`conponent/ProductList.js`

```js
import React from 'react';
import PropTypes from 'prop-types';
import { Button, Table, Popconfirm } from 'antd';

const ProductList = ({ onDelete, products }) => {
    const columns = [{
        title: 'name',
        dataIndex: 'name',
    }, {
        title: 'Actions',
        render: (text, record) => {
            return (
                <Popconfirm title="Delete?" onConfirm={() => onDelete(record.id)}>
                    <Button>Delete</Button>
                </Popconfirm>
            );
        },
    }];
    return (
        <Table dataSource={products} columns={columns}/>
    );
};

ProductList.propTypes={
    onDelete:PropTypes.func.isRequired,
    products:PropTypes.array.isRequired,
};

export default ProductList;
```

`models/products.js`

```js
export default {
    namespace: 'products', // 全局state的key
    state: [], // 初始值（数组）
    reducers: {
        'delete'(state, { payload: id }) {
            return state.filter(item => item.id !== id);
        },
    },
};
```

`routes\Products`

```js
import React from 'react';
import { connect } from 'dva';
import ProductList from '../components/componet';

const Products = ({dispatch, products}) => {
	function handleDelete(id) {
		dispatch({
			type: 'products/delete',
			payload: id,
		});
	}
	return (
		<div>
			<h2>List of Products</h2>
			<ProductList 
			onDelete={handleDelete} 
			products={products} />
		</div>
	);
};

export default connect(({ products }) => ({
	products,
  }))(Products);
```

index.js

```js
// 1. Initialize
const app = dva({
    initialState: {
        products: [
            { name: 'dva', id: 1 },
            { name: 'yww', id: 2 },
        ]
    }
});
```



例子：[用户管理](https://github.com/sorrycc/blog/issues/62)

`.webpackrc`

```json
{
	"proxy": {
		"/api": {
			"target": "http://jsonplaceholder.typicode.com/",
			"changeOrigin": true,
			"pathRewrite": {
				"^/api": ""
			}
		}
	}
}
```

umi中文件即路由



### 概念

Action是改变State的唯一途径，action中带type和参数。dispatch是出发行为的方式，reducer （纯函数）描述如何改变数据。

```
type dispatch = (a: Action) => Action
```

![image-20210704083510960](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\image-20210704083510960.png)

## Fake Api Data

```sh
http://jsonplaceholder.typicode.com/
```

http://jsonplaceholder.typicode.com/

https://mockend.com/ 安装mockend到你的Github

https://docs.mockend.com/

```shell
$ curl https://mockend.com/yinweiwen/study/posts
```



## [Redux中文](http://cn.redux.js.org/index.html)



## [Express](https://www.expressjs.com.cn/starter/generator.html)

Express 是一个保持最小规模的灵活的 Node.js Web 应用程序开发框架，为 Web 和移动应用程序提供一组强大的功能。

**HelloWorld**

```sh
npm init
npm install express --save
touch index.js
```

```js
const express = require('express')
const app = express()
const port = 3000

app.get('/', (req, res) => {
  res.send('Hello World!')
})

app.listen(port, () => {
  console.log(`Example app listening at http://localhost:${port}`)
})
```

```sh
node index.js
```

`ExpressGenerator` 快速创建应用骨干

```sh
npx exress-generator
npm install -g express-generator
express -h
```

使用pub模板创建一个应用

```sh
express --view=pug myapp
SET DEBUG=myapp:* & npm start
```

托管静态文件

```js
app.use('/static', express.static('public'))
```



## [Helmet](https://www.kancloud.cn/surahe/front-end-notebook/773986)

Helmet通过设置各种HTTP标头来帮助您保护Express应用程序。

`app.use(helmet())`

|                                                              |        |                                |
| :----------------------------------------------------------- | :----- | :----------------------------- |
| 模块                                                         | 默认？ | 功能                           |
| [contentSecurityPolicy](https://helmetjs.github.io/docs/csp/) |        | 设置Content Security Policy    |
| [expectCt](https://helmetjs.github.io/docs/expect-ct/)       |        | 证书透明度                     |
| [dnsPrefetchControl](https://helmetjs.github.io/docs/dns-prefetch-control) | ✓      | 控制浏览器DNS prefetching      |
| [frameguard](https://helmetjs.github.io/docs/frameguard/)    | ✓      | 防止点击挟持                   |
| [hidePoweredBy](https://helmetjs.github.io/docs/hide-powered-by) | ✓      | 移除X-Powered-By header        |
| [hpkp](https://helmetjs.github.io/docs/hpkp/)                |        | HTTP公钥固定                   |
| [hsts](https://helmetjs.github.io/docs/hsts/)                | ✓      | HTTP Strict Transport Security |
| [ienoopen](https://helmetjs.github.io/docs/ienoopen/)        | ✓      | 为IE8设置X-Download-Options    |
| [nocache](https://helmetjs.github.io/docs/nocache/)          |        | 禁用浏览器缓存                 |
| [noSniff](https://www.npmjs.com/package/helmet)              | ✓      | 防止客户端嗅探MIME类型         |
| [referrerPolicy](https://helmetjs.github.io/docs/referrer-policy) |        |                                |
| [xssFilter](https://helmetjs.github.io/docs/xss-filter)      | ✓      |                                |



## SVG

`Scalable Vector Graphics`可伸缩矢量图形  **SVG 是使用 XML 来描述二维图形和绘图程序的语言。**



## Gitlab

在CentOS7上安装

```sh
　　yum -y install policycoreutils openssh-server openssh-clients postfix
　　systemctl enable postfix && systemctl start postfix
　　
　 gitlab的下载地址：https://mirrors.tuna.tsinghua.edu.cn/gitlab-ce/yum/el7/
 
　　rpm -ivh gitlab-ce-14.0.3-ce.0.el7.x86_64.rpm
　　
　　vim  /etc/gitlab/gitlab.rb 
　　external_url   http://git.yinweiwen.cn

　　unicorn['port'] = 8088
　  // 14.0之后unicorn已经被移除，使用puma https://docs.gitlab.com/ee/administration/operations/puma.html
　　puma['port']=8088
　　
　　gitlab-ctl reconfigure // 修改配置后一定要执行，重启没用
　　gitlab-ctl start
　　
　　# root 登录 修改密码(默认密码在/etc/gitlab/initial_root_password)
ECS内存不足，放弃
gitlab-ctl stop
rpm -e gitlab-ce
ps aux | grep gitlab
kill -9 18777
find / -name gitlab | xargs rm -rf
```

## [Gitolite](https://www.cnblogs.com/Black-Hawk/articles/11731185.html)

放弃Gitlab，使用轻量级git服务器

```shell
# 创建git用户
adduser git
passwd git

#创建SSH公钥并将其复制到git用户
ssh-keygen
cp .ssh/id_rsa.pub sk.pub
cp sk.pub /home/git
#安装 Gitolite
su git
mkdir ~/bin
git clone git://github.com/sitaramc/gitolite #git克隆到最新的Gitolite存储库
gitolite/install -ln ~/bin
~/bin/gitolite setup -pk sk.pub #SSH公钥安装Gitolite，把root用户的key加入到git管理权限

#测试git
git clone git@localhost:gitolite-admin #从新安装的Gitolite服务器克隆gitolite-admin存储库，做此操作后可以获取到git的admin权限
cd /root/gitolite-admin
vim conf/gitolite.conf #把新添加key的用户名加入到conf文件，@admins相当于一个变量！后面指定用户的名字就可以了！
git add .
git commit -m "msg"
git push origin master #然后才生效！

##### 不小心把admin权限搞丢了
vi .gitolite/conf/gitolite.conf
~/bin/gitolite compile
```

gitconf权限模板

```shell
@admins = jeff awen1  # 组权限
@kf_users =  ajun AKingdrake1 aries #组权限
@cs_users =  monroe setevn dc_kf # 组权限
 
@repos = xy00000 xy00001 # 仓库名
@react = react_pro  # 仓库名
@other = android hb000  # 仓库名
@test = test_zent   # 仓库名
 
repo gitolite-admin  # 项目仓库
        RW+     =       @admins   # 分支权限
  
repo @repos   #项目仓库
        -   master =    @kf_users  # 分支权限
        -   master =    @cs_users   # 分支权限
        -   gray   =    @kf_users  # 分支权限
        -   dev    =    @kf_users  # 分支权限
        -   dev*   =    @kf_users  # 分支权限
        RW+        =    @admins  # 分支权限
        RW         =    @all  # 分支权限
 
repo @react    #项目仓库
        -   master =    @kf_users  #分支
        -   master =    @cs_users  # 分支
        RW+   =  @admins #特殊权限
        RW    =  @all   # 特性权限
 
repo @other #项目仓库
        RW+   =  @all  #特殊权限
repo @test   #项目仓库
        RW+   =  @all  #特殊权限
```

我的配置

```sh
@admins = git awen  # 组权限
 
@repos = web some # 仓库名

repo gitolite-admin  # 项目仓库
        RW+     =       @admins   # 分支权限
        
repo @repos   #项目仓库
        RW+        =    @admins  # 分支权限
        RW         =    @all  # 分支权限
```



配置windows客户端

```sh
ssh-keygen -t rsa # 生成公钥

然后在将密钥通过puttygen.exe来load，然后在Save private key来生成ppk文件

公钥上传到git服务器，在git用户下通过  ~/bin/gitolite setup -pk rsa.pub  加入


git clone git@106.15.89.47:web
通过TortoiseGIT进行文件的增减、提交工作
通过git push origin master提交

git add xx // 提交时添加文件
git commit -a -m msg // -a 提交所有改动文件
```



## Nginx

安装

```sh
yum install -y gcc
yum install -y pcre pcre-devel
yum install -y zlib zlib-devel
yum install -y openssl openssl-devel
wget http://nginx.org/download/nginx-1.9.9.tar.gz  
tar -zxvf  nginx-1.9.9.tar.gz

./configure
make
make install

#启动
./nginx

# /usr/local/nginx/sbin
# /usr/local/nginx

# 配置后更新
nginx -s reload

```



配置

题外：`df -ahlT` 查看磁盘空间

发布gitlab网站

```json
server {
        listen 80;
        server_name git.yinweiwen.cn;
        location / {
            proxy_pass    http://localhost:8088;
        }
}
```

发布个人主页

```json
server {
        listen 80;
        server_name yinweiwen.cn;
    	# 需要去掉nginx.conf中logformat注释
        access_log ywwlogs/access.log main;
    	# 注意： 这里api后面必须加/。同时必须使用host名称(localhost不可以)
        location /api/ {
            proxy_pass    http://iZuf69j4skaou5p4tq5il9Z:9080/;
        }
        location / {
            proxy_pass    http://iZuf69j4skaou5p4tq5il9Z:8080;
        }
}

```

注意httpd配置需加上

`Listen 0.0.0.0:8080`



Https配置示例 TODO

```json
server {
        listen 80;
        return 301 https://$host$request_uri;
        server_name xx.com;
}

server {
        listen 443;
        server_name xx.com;
        ssl on;
        ssl_certificate /home/iota/product/certs/console.theiota.cloud.pem;
        ssl_certificate_key /home/iota/product/certs/console.theiota.cloud.key;
        ssl_session_timeout 5m;
        ssl_protocols SSLv3 TLSv1 TLSv1.1 TLSv1.2;
        ssl_ciphers "HIGH:!aNULL:!MD5 or HIGH:!aNULL:!MD5:!3DES";
        ssl_prefer_server_ciphers on;

        client_max_body_size 10M;

    location /v1/api {
        proxy_pass  http://anxin-m1:9090/v1/api;
        proxy_cookie_domain localhost localhost;
        proxy_cookie_path / /;
        proxy_connect_timeout 1;
        proxy_send_timeout 120;
        proxy_read_timeout 300;
        proxy_set_header X-Real-Ip $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }

    location / {
        proxy_pass    http://anxin-m1:9080;
        proxy_cookie_domain localhost localhost;
        proxy_cookie_path / /;
        proxy_connect_timeout 1;
        proxy_send_timeout 600;
        proxy_read_timeout 60;
    }
}
```



## MySql

安装

```sh
wget http://dev.mysql.com/get/mysql57-community-release-el7-10.noarch.rpm
yum -y install mysql57-community-release-el7-10.noarch.rpm
yum -y install mysql-community-server

systemctl start mysqld.service
mysql -uroot -p
```



忘记密码：

```sh
安装 mysql 之后，注意添加软连接

mysql 忘记密码操作，

vim /etc/my.cnf

在 [mysqld] 的段中加上一句：skip-grant-tables

重启 mysql 服务， service mysql restart

在输入 mysql -u root -p  输入回车即可进入 mysql 数据库

use mysql;

低版本修改：update user set Password = password('LDtianzhe.') where user = 'root';

高版本修改：update user set authentication_string=password('Aliyun123_') where user = 'root';

再次vim /etc/my.cnf 注释掉 上面添加的重启服务即可再次进入数据库

如果出现此错误：
ERROR 1175 (HY000): You are using safe update mode and you tried to update a table without a WHERE that uses a KEY column
需要关闭保护模式

mysql > set sql_safe_updates=off;

退出，注释掉 skip-grant-tables

重启服务即可
```



## FQ

https://portal.shadowsocks.nz/knowledgebase/182/

Pass: yww0828@126.com

复制链接

![image-20210713105905727](imgs/antpro/image-20210713105905727.png)

安装小猫

- Windows 访问 https://dl.trojan-cdn.com/trojan/windows/ 下载 **Clash.for.Windows.Setup.[版本号].exe**
- macOS 访问 https://dl.trojan-cdn.com/trojan/macos/ 下载 **Clash.for.Windows-[版本号].dmg**
- 访问 [Github Releases](https://github.com/Fndroid/clash_for_windows_pkg/releases) 下载

![image-20210713113348641](imgs/antpro/image-20210713113348641.png)

复制链接到profile中进行download。

到proxies中选择代理节点。点击猫头重启