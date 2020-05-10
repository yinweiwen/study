# GCC学
GNU Compiler Collection(GCC)
支持编译C、C++、Objective-C、Fortran、Java、Ada和Go语言
![](GCC_files/1.jpg)

GCC使用的软件工具：

|工具		|描述																																							|
|addr2line	|给出一个可执行文件的内部地址，addr2line 使用文件中的调试信息将地址翻泽成源代码文 件名和行号。该程序是 binutils 包的一部分										|
|ar			|这是一个程序，可通过从文档中增加、删除和析取文件来维护库文件。通常使用该工具是为了创建和管理连接程序使用的目标库文档。该程序是 binutils 包的一部分				|
|as			|GNU 汇编器。实际上它是一族汇编器，因为它可以被编泽或能够在各种不同平台上工作。 该程序是 binutils 包的一部分													|
|autoconf	|产生的 shell 脚木自动配置源代码包去编泽某个特定版木的 UNIX																										|
|c++filt	|程序接受被 C++ 编泽程序转换过的名字（不是被重载的），而且将该名字翻泽成初始形式。 该程序是 binutils 包的一部分													|
|f2c		|是 Fortran 到C的翻译程序。不是 GCC 的一部分																													|
|gcov		|gprof 使用的配置工具，用来确定程序运行的时候哪一部分耗时最大																									|
|gdb		|GNU 调试器，可用于检查程序运行时的值和行为																														|
|GNATS		|GNU 的调试跟踪系统（GNU Bug Tracking System）。一个跟踪 GCC 和其他 GNU 软件问题的在线系统																		|
|gprof		|该程序会监督编泽程序的执行过程，并报告程序中各个函数的运行时间，可以根据所提供 的配置文件来优化程序。该程序是 binutils 包的一部分								|
|ld			|GNU 连接程序。该程序将目标文件的集合组合成可执行程序。该程序是 binutils 包的一部																				|
|libtool	|一个基本库，支持 make 程序的描述文件使用的简化共享库用法的脚木																									|
|make		|一个工具程序，它会读 makefile 脚木来确定程序中的哪个部分需要编泽和连接，然后发布 必要的命令。它读出的脚木（叫做 makefile 或 Makefile）定义了文件关系和依赖关系	|
|nlmconv	|将可重定位的目标文件转换成 NetWare 可加载模块（NetWare Loadable Module, NLM）。该 程序是 binutils 的一部分														|
|nm			|列出目标文件中定义的符号。该程序是 binutils 包的一部分																											|
|objcopy	|将目标文件从一种二进制格式复制和翻译到另外一种。该程序是 binutils 包的一部分																					|
|objdump	|显示一个或多个目标文件中保存的多种不同信息。该程序是 binutils 包的一部分																						|
|ranlib		|创建和添加到 ar 文档的索引。该索引被 Id 使用来定位库中的模块。该程序是 binutils 包的一部分																		|
|ratfor		|Ratfor 预处理程序可由 GCC 激活，但不是标准 GCC 发布版的一部分																									|
|readelf	|从 ELF 格式的目标文件显示信息。该程序是 binutils 包的一部分																									|
|size		|列出目标文件中每个部分的名字和尺寸。该程序是 binutils 包的一部分																								|
|strings	|浏览所有类型的文件，析取出用于显示的字符串。该程序是 binutils 包的一部分																						|
|strip		|从目标文件或文档库中去掉符号表，以及其他调试所需的信息。该程序是 binutils 包的一部																				|
|vcg		|Ratfor 浏览器从文木文件中读取信息，并以图表形式显示它们。而 vcg 工具并不是 GCC 发布中的一部分，但 -dv 选项可被用来产生 vcg 可以理解的优化数据的格式			|
|windres	|Window 资源文件编泽程序。该程序是 binutils 包的一部分						


$gcc main.c -o ./out/main.out
> -o 输出文件

将编译和链接分开：
$gcc -c main.c
> 编译，输出.o文件（对于微软编译器（内嵌在Visual C++）生成.obj文件）
$gcc main.o
> 链接，输出.out文件；

GCC 编译器在编译一个C语言程序时需要经过以下 4 步：
1. 将C语言源程序预处理，生成.i文件。
2. 预处理后的.i文件编译成为汇编语言，生成.s文件。
3. 将汇编语言文件经过汇编，生成目标文件.o文件。
4. 将各个模块的.o文件链接起来生成一个可执行程序文件。

## GCC常用编译选项
|gcc编译选项		|选项的意义								|
|-c					|编译、汇编指定的源文件，但是不进行链接	|
|-S					|编译指定的源文件，但是不进行汇编		|
|-E					|预处理指定的源文件，不进行编译			|
|-o [file1] [file2]	|将文件 file2 编译成可执行文件 file1	|
|-I directory		|指定 include 包含文件的搜索目录		|
|-g					|生成调试信息，该程序可以被调试器调试	|

[More](http://www.runoob.com/w3cnote/gcc-parameter-detail.html)

-g选项可生成能被 gdb 调试器所使用的调试信息。只有使用了该选项后生成的可执行文件，才带有程序中引用的符号表。这时 gdb 调试程序才能对该可执行程序进行调试。

## gcc/Makefile/CMake 关系
程序较大时gcc逐个编译工作量大
make工具：类似批处理，调用Makefile（makefile内部调用编译器(如gcc)）
工程量很大时，makefile文件复杂
CMake工具生成makefile，根据CMakeLists.txt,支持跨平台

## Window上c/c++运行调试
安装MinGW >> [MinGW官方网址](http://www.mingw.org/) >> 安装完成后加入到系统路径下
`MinGW(Minimalist GNU For Windows)`是Windows上GNU程序编译开发工具集,体积轻量级(相比Cygwin)，包含：
GCC  / GNU Binutils/ mingw-get(用于Windows平台安装和部署MinGW和MSYS的命令行安装器) /mingw-get-inst(用于GUI打包)

安装VSCode，c/c++扩展
[](https://code.visualstudio.com/docs/languages/cpp)

	IntelliSense Ctrl+Shift+P > Config
	Build Ctrl+Shift+B > Tasks: Configure Task > Others
	Debugging  C++ (GDB/LLDB)
	
c_cpp_properties.json
```json
{
    "configurations": [
        {
            "name": "Win32",
            "includePath": [
                "${workspaceFolder}/**"
            ],
            "defines": [
                "_DEBUG",
                "UNICODE",
                "_UNICODE"
            ],
            "windowsSdkVersion": "10.0.15063.0",
            "compilerPath": "C:/Program Files (x86)/Microsoft Visual Studio/2017/Community/VC/Tools/MSVC/14.10.25017/bin/Hostx64/x64/cl.exe",
            "cStandard": "c11",
            "cppStandard": "c++17",
            "intelliSenseMode": "msvc-x64"
        }
    ],
    "version": 4
}
```

tasks.json
```json
{
    // See https://go.microsoft.com/fwlink/?LinkId=733558
    // for the documentation about the tasks.json format
    "version": "2.0.0",
    "tasks": [
        {
            "label": "echo",
            "type": "shell",
            "command": "gcc",
            "args": [
                "-g",
                "-o",
                "echo.exe",
                "-I",
                "./",
                "src/main.c",
                "src/a.c"
            ],
            "group": {
                "kind": "build",
                "isDefault": true
            }
        }
    ]
}
```

launch.json
```json
{
    // Use IntelliSense to learn about possible attributes.
    // Hover to view descriptions of existing attributes.
    // For more information, visit: https://go.microsoft.com/fwlink/?linkid=830387
    "version": "0.2.0",
    "configurations": [
        {
            "name": "(gdb) Launch",
            "type": "cppdbg",
            "request": "launch",
            "program": "${workspaceFolder}/echo.exe",
            "args": [],
            "stopAtEntry": false,
            "cwd": "${workspaceFolder}",
            "environment": [],
            "externalConsole": true,
            "MIMode": "gdb",
            "miDebuggerPath": "C:\\TDM-GCC-64\\bin\\gdb.exe",
            "setupCommands": [
                {
                    "description": "Enable pretty-printing for gdb",
                    "text": "-enable-pretty-printing",
                    "ignoreFailures": true
                }
            ],
            "preLaunchTask": "echo"
        }
    ]
}	
```

## mediaPusher build

在ubuntu中构建需加上 -D_GLIBCXX_USE_CXX11_ABI=0
```cmake
cmake_minimum_required(VERSION 3.7)
set(CMAKE_CXX_COMPILER g++)
add_compile_options(-std=c++11 -D_GLIBCXX_USE_CXX11_ABI=0)

project (mediaPusher)
include_directories(${CMAKE_SOURCE_DIR}/restbed/include)
include_directories(./)
link_directories(${CMAKE_SOURCE_DIR}/lib)

set(SRC_LIST mediaPusher.cpp web_api.cpp jsoncpp.cpp nvr_hik.cpp push_rtmp.cpp)
add_executable(mediaPusher ${SRC_LIST})
target_link_libraries(mediaPusher restbed hcnetsdk analyzedata rtmp pthread)

```

构建后无法执行：
```shell
$ ldd mediaPusher 
	linux-vdso.so.1 =>  (0x00007ffd221a0000)
	librestbed.so.4 => not found
	libhcnetsdk.so => not found
	libanalyzedata.so => not found
```

加入库连接
```
/etc/ld.so.conf
>>add lib path
sudo ldconfig
```

## RTMP协议
RTMP(Real Time Messaging Protocol)  将消息分块(chunk)发送
CSID(chunk stream id)
优点：RTMP 是专为流媒体开发的协议，对底层的优化比其它协议更加优秀，同时它 Adobe Flash 支持好，基本上所有的编码器（摄像头之类）都支持 RTMP 输出。
缺点：Adobe 私有协议；基于TCP协议的应用层协议

[详细介绍](https://www.jianshu.com/p/00aceabce944)

## H264视频压缩算法
帧内预测压缩，解决的是空域数据冗余问题。
帧间预测压缩（运动估计与补偿），解决的是时域数据冗余问题。
整数离散余弦变换（DCT），将空间上的相关性变为频域上无关的数据然后进行量化。
CABAC压缩。

经过压缩后的帧分为：I帧，P帧和B帧:

I帧：关键帧，采用帧内压缩技术。
P帧：向前参考帧，在压缩时，只参考前面已经处理的帧。采用帧音压缩技术。
B帧：双向参考帧，在压缩时，它即参考前而的帧，又参考它后面的帧。采用帧间压缩技术。
 GOP
 
 
 
## CGO

https://chai2010.cn/advanced-go-programming-book/ch2-cgo/readme.html

https://github.com/golang/go/wiki/cgo

> 编写C动态库
// number.h
int number_add_mod(int a, int b, int mod);

// number.c
#include "number.h"

int number_add_mod(int a, int b, int mod) {
    return (a+b)%mod;
}

// number.def
LIBRARY number.dll

EXPORTS
number_add_mod

linux下编译
gcc -shared -o libnumber.so number.c
windows下编译
gcc -shared -o number.dll number.c

CGO使用动态库，Linux下可以直接链接so文件，但是在Windows下必须为dll创建一个.a文件用于链接
CGO无法使用windows下number.lib格式的链接库，
$ dlltool -dllname number.dll --def number.def --output-lib libnumber.a

window编译
gcc -c number.c
gcc -shared -o number.dll number.o -Wl,--out-implib,libnumber.dll.a


## Scons
[ref](https://blog.csdn.net/guotianqing/article/details/92003258)
Scons是Python实现的自动化、跨平台、多语言、自动依赖分析的构建工具，用来代替（简化）make。

helloworld的编译实例：
```shell

% cat SConstruct
Program("hello.c")
```
`scons -c ` 执行清理任务
`scons -Q` 减少冗余信息


SCons 支持的编译类型有：

+ Program： 编译成可执行程序（在 Windows 平台上即是 exe 文件），这是最常用的一种编译类型。
+ Object： 只编译成目标文件。使用这种类型，编译结束后，只会产生目标文件。在 POSIX 系统中，目标文件以 .o 结尾，在 Windows平台上以 .OBJ 结尾。
+ Library： 编译成库文件。SCons 默认编译的库是指静态链接库。
+ StaticLibrary： 显示的编译成静态链接库，与上面的 Library 效果一样。
+ SharedLibrary： 在 POSIX 系统上编译动态链接库，在 Windows 平台上编译 DLL。

复杂点的例子，编译 'helloscons2.c', 'file1.c', 'file2.c'
```
Program('hello-scons2',['helloscons2.c', 'file1.c', 'file2.c'],
	LIBS='m',
	LIBPATH=['/usr/lib'],
	CCFLAGS='-DHELLOSCONS')

LIBS: 需要链接的库
LIBPATH: 链接库的搜索路径
CCFLAGS: 编译选项。上例定义了宏 HELLOSCONS
CPPPATH: 指定头文件路径

# 指定g++编译环境
env = Environment(CC = 'g++')
env.Program("client", "client.c", LIBS = 'm', CPPPATH = '../include', CCFLAGS = '-std=c++11')
```