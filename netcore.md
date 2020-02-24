# .Net Core
## 环境准备
+ 下载 https://dotnet.microsoft.com/download
```shell
dotnet new console -o myApp

Did you mean to run dotnet SDK commands? Please install dotnet SDK from

~~>where dotnet
C:\Program Files (x86)\dotnet\dotnet.exe
C:\Program Files\dotnet\dotnet.exe

手动修改系统Path（更改顺序或删除多余）

dotnet --list-sdks
```

##  IDE

### 1. Visual Studio 2019

### 2. Visual Studio Code
在控制台运行它 dotnet run    
调试：
1. 安装 C# 插件
2. 选中programs.cs 自动加载 OmniSharp 缺少调试配置，选择自动生成yes
3. F5运行



## install runtime on docker

docker pull mcr.microsoft.com/dotnet/core/runtime