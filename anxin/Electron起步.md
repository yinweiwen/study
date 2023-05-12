## Electron

### 快速启动

https://www.electronjs.org/zh/docs/latest/tutorial/quick-start



> 设置阿里镜像
> npm config set registry=https://registry.npmmirror.com
> npm config set disturl=https://registry.npmmirror.com/-/binary/node
>
> 设置electron仓库
> npm config set electron_mirror=https://registry.npmmirror.com/-/binary/electron/



```sh
# Clone this repository
git clone https://github.com/electron/electron-quick-start
# Go into the repository
cd electron-quick-start
# Install dependencies
npm install
# Run the app
npm start
```



![image-20230223201718356](imgs/Electron%E8%B5%B7%E6%AD%A5/image-20230223201718356.png)



```nodejs
npm install --save-dev electron

# package.json
{
  "scripts": {
    "start": "electron ."
  }
}
```



发布：

```sh
npm install --save-dev @electron-forge/cli
npx electron-forge import

npm run make
```

