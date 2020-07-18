## Ant Design
\
\
[Ant-Design](https://www.yuque.com/ant-design/course/wybhm9)

```
>npm install -g cnpm --registry=https://registry.npm.taobao.org

>cnpm init -y

cnpm install umi --save-dev

cnpm install umi-plugin-react --save-dev

cnpm run dev

cnpm install --save antd
```

```js
export default {
  plugins: [
    ['umi-plugin-react', {
      // 这里暂时还没有添加配置，该插件还不会有作用，我们会在后面的课程按照需求打开相应的配置
    }],
  ],
  routes: [{
    path: '/',
    component: './HelloWorld',
  }],
}
```