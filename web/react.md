# React 

```shell
npm config set registry https://registry.npm.taobao.org
npm install -g cnpm

cnpm install -g create-react-app
$ create-react-app my-app
$ cd my-app/
$ npm start
```

# [菜鸟教程](https://www.runoob.com/react)
Props验证
propTypes
```js
class MyTitle extends React.Component{
	render(){
		return (
			<h1>Hello, {this.props.title}</h1>
		);
	}
}

MyTitle.propTypes={
	title: PropTypes.string
};
```
```js
var MyTitle=React.createClass({
	propTypes:{
		title: React.PropTypes.string.isRequired,
	},
	
	render:function(){
		return <h1>{this.props.title}</h1>
	}
});
```
React.PropTypes.array/bool/func/number/object/string /node /element / instanceOf(Message)
oneOf(['News','Photos']) / oneOfType([React.PropTypes.string,React.PropTypes.number])
isRequired 要求属性不为空
React.PropTypes.any.isRequired // 不为空的任意类型

## React 事件处理


// <!--<button onClick={activateLasers}/>-->

阻止默认行为：
```js
function ActionLink(){
	function handleClick(e){
		e.preventDefault(); // 阻止默认行为
		console.log('');
	}
	
	return (
		<a href="#" onClick={handleClick}>
			click me!
		</a>
	)
}
```

```js
class Toggle extends React.Component {
	constructor(props){
		super(props)
		this.state={toggled:true};
		// 类的方法默认是不会绑定 this 的
		this.handleClick=this.handleClick.bind(this);
		// 不用bind的两种写法：
		// 1.handleClick=()=>{...}
		// 2.onClick={(e)=>this.handleClick(e)}
	}
	
	handleClick(){
		this.setState(preState=>(
			{toggled:!preState.toggled}
		));
	}
	
	render(){
		return (
			<button onClick={this.handleClick}>
				{this.state.toggled?"ON":"OFF"}
			</button>
		);
	}
}
```
传递参数：
```js
class Popper extends React.Component{
	constructor(){
		super();
		this.state={name:'hello'};
	}
	preventPop(name,e){
		e.preventDefault();
		alert(name);
	}
	render(){
		return(
			<div>
				<p>hello</p>
				<a href="..." onClick={this.preventPop.bind(this,this.state.name)}Click</a>
			</div>
		)
	}
}
```

## React列表
```js
function NumberList(props){
	const numbers=props.numbers;
	const listItems=numbers.map((number)=>
		<li key={number.toString()}>{number}</li>
	);
	return (<ul>{listItems}</ul>)
}
```
index作为键
```js
const todoItems=todos.map((todo,index)=>
	<li key={index}>
		{todo.text}
	</li>
)
```

## React API

1. setState`>`

合并sate，更新UI
2. replaceState`>`

替换state，更新UI
3. setProps`>`

向(子)组件传递数据，合并props，更新组件UI
4. relaceProps`>`

向(子)组件传递数据，替换props，更新组件UI
5. forceUpdate`>`

强制刷新，尽量避免使用
6. findDOMNode`>`

查找本地浏览器DOM元素；如果render返回null或false时该方法返回null

7. isMounted`>`

判断组件是否已挂载到DOM中

状态更新可能是异步的
```js
// wrong
this.setState({counter:this.state.counter+this.props.increment,});
// correct
this.setState((prevState,props)=> ({
	counter:prevState.counter+props.increment
}));
```

## React Lifecicle
1. componentWillMount

在渲染前调用,在客户端也在服务端。
2. componentDidMount

在第一次渲染后调用，只在客户端。
通常在这里获取远端数据; 也可以在这里调用setState（通常当你需要丈量DOM节点大小时）
3. componentWillReceiveProps

在组件接收到一个新的 prop (更新后)时被调用。初始化时不会调用
4. shouldComponentUpdate(nextProps, nextState)

渲染前执行；执行forceUpdate时不会触发
5. componentWillUpdate

在组件接收到新的props或者state但还没有render时被调用。在初始化时不会被调用。
6. componentDidUpdate(prevProps, prevState, snapshot)

组件被更新的时候；初始渲染不会触发
7. componentWillUnmount

在组件从 DOM 中移除之前立刻被调用。

![](imgs/1.jpg)

### render

组件中唯一必须的方法；可以返回： 通过JSX创建的react element, Arrays and fragments,Portals,String and numbers,Booleans or null 什么都不渲染（return test && <Child />）
+ render should be pure, meaning that it does not modify component state

### constructor()

+ should called `super(props)` first
+ two purposes: 1-initializing local state 2-binding event handle
+ constructor is the only place where you should assign `this.state` directly.
+ Avoid copying props into state;
```js
constructor(props){
	super(props);
	// Don't do this!
	this.state={color: props.color};
}
```

 static getDerivedStateFromProps(props, state)

=========

# [React Doc](https://reactjs.org/docs/getting-started.html)

## 组装 or 继承 (Composition VS Inheritance)

</p>建议通过组装来进行代码重用
```js
function Dialog(props) {
  return (
    <FancyBorder color="blue">
      <h1 className="Dialog-title">
        {props.title}
      </h1>
      <p className="Dialog-message">
        {props.message}
      </p>
      {props.children}
    </FancyBorder>
  );
}

class SignUpDialog extends React.Component {
  constructor(props) {
    super(props);
    this.handleChange = this.handleChange.bind(this);
    this.handleSignUp = this.handleSignUp.bind(this);
    this.state = {login: ''};
  }

  render() {
    return (
      <Dialog title="Mars Exploration Program"
              message="How should we refer to you?">
        <input value={this.state.login}
               onChange={this.handleChange} />
        <button onClick={this.handleSignUp}>
          Sign Me Up!
        </button>
      </Dialog>
    );
  }

  handleChange(e) {
    this.setState({login: e.target.value});
  }

  handleSignUp() {
    alert(`Welcome aboard, ${this.state.login}!`);
  }
}
```


## React AJAX

```js
class UserGist extends React.Component{
	constructor(props){
		super(props);
		this.state={username:'',lastGistUrl:''};
	}
	
	componentDidMount(){
		this.serverRequest= $.get(this.props.source,function(result){
			var lastGist=result[0];
			this.setState({
				username:lastGist.owner.login,
				lastGistUrl:lastGist.html_url
			});
		}.bind(this));
	}
	
	componentWillUnmount(){
		this.serverRequest.abort();
	}
	
	render(){
		return (
			<div>
				{this.state.username} 用户最新的 Gist 共享地址：
				<a href={this.state.lastGistUrl}>{this.state.lastGistUrl}</a>
			</div>
		)
	}
}
```

## React 表单

使用Input的onChange方法更新组件的state。
```js
class Input extends React.Component{
	constructor(props){
		super(props);
		this.state={value:'hello runoob'};
		this.handleChange=this.handleChange.bind(this);
	}
	handleChange(event){
		this.setState({value:event.target.value});
	}
	render(){
		var value=this.state.value;
		return <div>
		<input type="text" value={value} onChange={this.handleChange}/>
		<h4>{value}</h4>
		</div>
	}
}
```

```js
class Selector extend React.Component{
	constructor(props){
		super(props);
		this.state={value:'cocount'};
		this.handleChange=this.handleChange.bind(this);
		this.handleSubmit=this.handleSubmit.bind(this);
	}
	
	handleChange(event){
		this.setState({value:event.target.value});
	}
	
	handleSubmmit(event){
		event.preventDefault();
	}
	
	render(){
		return (
			<form onSubmit={this.handleSubmit}>
				<label>
					<select value={this.state.value} onChange={this.handleChange}>
						<option value='gg'>Google</option>
						<option value='rn'>Runoob</option>
					</select>
				</label>
				<input type='submit' value='提交'/>
			</form>
		)
	}
}
```

多个input时
```js
class MultiInput extends React.Component{
	constructor(props){
		super(props);
		this.state={isgoing:true,numberOfGuests:2};
		this.handleInputChange=this.handleInputChange.bind(this);
	}
	
	handleInputChange(event){
		const target=event.target;
		const value=target.type=='checkbox'?target.checked:target.value;
		const name=target.name;
		this.setState({[name]:value});
	}
	
	render(){
		return (
			<form>
				<label>do you leave?
				<input name='isgoing' type='checkbox' checked={this.state.isgoing} onChange={this.handleInputChange}/>
				</label>
				<br />
				<label>visitors number:
				<input name='numberOfGuests' type='number' value={this.state.numberOfGuests} onChange={this.handleInputChange}/>
				</label>
			</form>
		);
	}
}
```

## React Refs

```js
class RefComponent extends React.Component{
	handleClick(){
		this.refs.myInput.focus();
	}
	render(){
		return (
			<div>
				<input type='text' ref='myInput'/>
				<input type='button' value='focus'
				onClick={this.handleClick.bind(this)}/>
			</div>
		);
	}
}
```


-----------------

# [React中文文档](https://react.docschina.org/docs/getting-started.html)

## A re-introduction to JavaScript (JS tutorial)
JavaScript is a multi-paradigm, dynamic language with types and operators, standard built-in objects, and methods.

多范式(面向对象、面向函数);
类型简图：
+ Number
+ String
+ Boolean
+ Symbol (new in ES2015)
+ Object
	+ Function
	+ Array
	+ Date
	+ RegExp
+ null
+ undefined

Number
```js
parseInt("020");
parseFloat("3.14")
+ "0x10"
isNaN(NaN)
isFinite(-Infinity)
```

String
```js
'hello'.length
'hello'.charAt(0); // "h"
'hello, world'.replace('world', 'mars'); // "hello, mars"
'hello'.toUpperCase(); // "HELLO"
```

Other Types
```js
null <> undefined
boolen type: true false(0 /"" /NaN /null /undefined)

```

### [JSX](https://react.docschina.org/docs/introducing-jsx.html)

是一个JavaScript的语法扩展，

可以嵌入任何JavaScript表达式：
```js
const name='Josh';
const element=<h2> hello {name} </h2>
```

JSX也是一个表达式，可以`return <div tabIndex="0"></div>`;

Babel会把JSX转译成一个名为`React.createElement()`函数使用：
```js
const element=(
	<h1 className="greeting">
		Hello, world!
	</h1>
);

const element=React.createElement(
	'h1',
	{className:'greeting'},
	'Hello, world!'
);
```

### 元素渲染

__注意： 组件名称必须以大写字母开头。__

__组件的props不被更改__

__State的更新可能是异步的__
```js
this.setState((state,props)=>({
	counter:state.counter+props.increment
}));
```

__数据是向下流动的__
:当前组件的 state 作为子组件的 props 向下传递

### 条件渲染

```js
if(true){
	return <C1/>
}else{
	return <C2/>
}


(a.length>0 && 
	<h1> asd </h1>
)

{isLoggedIn ? (
        <LogoutButton onClick={this.handleLogoutClick} />
      ) : (
        <LoginButton onClick={this.handleLoginClick} />
      )}
			
// 阻止渲染
render(){
	if(boolean){
		return null;
	}
}


const todoItems = todos.map((todo) =>
  <li key={todo.id}>
    {todo.text}
  </li>
);
```

[MORE](https://react.docschina.org/docs/forms.html)