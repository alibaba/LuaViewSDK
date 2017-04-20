# 欢迎来到LuaViewSDK

LuaViewSDK是一个跨iOS和Android平台的界面动态化解决方案，使用同一套Lua脚本在iOS和Android平台构建原生页面，同时兼具了H5的灵活性和Native的高性能

## 为什么选择Lua

为了解决类似的问题，市面上有许多类似的方案，比如React Native和Weex，他们使用了JS框架，配合DSL来进行界面布局，使用JS引擎进行脚本动态解析，实现了动态化的Native界面渲染能力。LuaViewSDK使用lua虚拟机进行脚本解析，通过构建lua与native之间的一系列基础bridge功能，从另一个角度实现了动态化的native能力。相比于基于JS虚拟机的解决方案，LuaViewSDK有如下特性：

- lua虚拟机极为轻量高效。单个lua虚拟机只消耗大约200到300k的内存空间，同屏渲染几十个LuaView没有任何负担
- Lua天生就是作为一个极其轻量、可嵌入的设计实现，它更适合做API的封装或是数据传输的中间层。在App的开发上，更适合作为胶水语言，来直接操作native对象
- 使用App开发人员更为熟悉的Native编程模式，直接创建和操作各种界面对象
- 轻松桥接native组件，只需要一行代码即可实现在LuaView脚本中操作用户自定义的native功能

## 试用Playground App

LuaViewSDK Playground App下载地址 [Download](https://github.com/alibaba/LuaViewSDK/releases)

## 如何使用LuaViewSDK

- [使用说明](https://alibaba.github.io/LuaViewSDK/guide.html)
- [接口文档](https://alibaba.github.io/LuaViewSDK/apimenu.html)

## 开发者

- [董希成](xicheng.dxc@alibaba-inc.com)
- [陈松涛](songtao.cst@alibaba-inc.com)

## 许可证

[MIT](https://github.com/alibaba/LuaViewSDK/blob/master/LICENSE.txt)

## 社区

[Github Wiki](https://github.com/alibaba/LuaViewSDK/wiki)

QQ群：539262083

Twitter：[@luaview](https://twitter.com/luaview)