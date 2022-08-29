2022web学习中的记录

### 浏览器http自动跳转https

1. 地址栏中输入 chrome://net-internals/#hsts 
2. 在 Delete domain securitypolicies 中输入项目的域名，并 Delete 删除 
3. 可以在 Query domain 测试是否删除成功 这里如果还是不行，
4. Chrome 桌面快捷方式上右击，“属性”，在“目标”路径的后面添加
    --allow-running-insecure-content 参数。打开浏览器后就会自动启动混合模式。
5. 请**清除浏览器缓存**！



### React Router

react 是SPA（单页面应用）

`react-router-dom`: 基于`react-router`，加入了在浏览器运行环境下的一些功能（其中`Switch、Route、Router、Redirect`等组件是直接引入`react-router`中的，新增了`Link、BrowserRouter、HashRouter`组件。）

```routeros
export { Switch, Route, Router, Redirect } from 'react-router'
```

https://react-guide.github.io/react-router-cn/docs/Introduction.html

### [React](https://so.csdn.net/so/search?q=React&spm=1001.2101.3001.7020)中几种页面跳转方式

https://blog.csdn.net/qq_46503396/article/details/123813712

```html
<Link to=""/>

<Link to={{
	  	  pathname:'/path/ww',
	      state:{
      		data1:{},
      		data2:[]
      	}
      }}/>
```

通过react-router-redux中的push

```html
dispatch(push("/path/newpath'", param1));
push - 跳转到指定路径
replace - 替换历史记录中的当前位置
go - 在历史记录中向后或向前移动相对数量的位置
goForward - 向前移动一个位置。相当于go(1)
goBack - 向后移动一个位置。相当于go(-1)
```

用RouteComponentProps中的history进行页面回退

this.props.history.goBack();



