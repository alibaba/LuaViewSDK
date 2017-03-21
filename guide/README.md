# LuaViewSDK概述
## 1. LuaViewSDK是什么？

> **LuaViewSDK 主要解决两个问题**

1. 相同的业务逻辑需要在 iOS 和 Android 平台各实现一次，除了成本还有体验的细微差别，是否有一种方案可以 Write once run both？LuaViewSDK 是答案。
2. 移动 APP，要体验发布就不灵活（Native），要发布灵活就体验欠缺（H5），有没有一种方案可以兼顾 Nativa 的体验和 H5的发布灵活性？LuaViewSDK 就是答案。

LuaView 是一种运行在一个 ViewController/Activity 中，可以灵活加载Lua 脚本，并能够按照 Native 的方式运行的一种面向业务的开发技术方案。可以快速开发电商应用中既要求体验又要求灵活性的页面功能，例如各类活动页面，大促会场页面等。现在使用 LuaViewSDK 完成的线上功能有聚划算的品牌团，聚名品，量贩团，非常大牌，聚家装，全球精选等页面

LuaViewSDK目前已经在 Alibaba 整体开源框架下完整开源（MIT协议），欢迎大家使用、讨论。
开源地址：[https://github.com/alibaba/LuaViewSDK](https://github.com/alibaba/LuaViewSDK)

## 2. 为什么选择lua

市面上有许多类似的方案，比如React Native和Weex，他们使用了JS框架，配合DSL来进行界面布局，使用JS引擎进行脚本动态解析，实现了动态化的Native界面渲染能力。LuaViewSDK使用lua虚拟机进行脚本解析，通过构建lua与native之间的一系列基础bridge功能，从另一个角度实现了动态化的native能力。相比于基于JS虚拟机的解决方案，LuaViewSDK选择的lua有如下优势：

- lua虚拟机极为轻量高效。单个lua虚拟机只消耗大约200到300k的内存空间，同屏渲染几十个LuaView没有任何负担
- lua天生就是作为一个极其轻量、可嵌入的设计实现，它更适合做API的封装或是数据传输的中间层。在App的开发上，更适合作为胶水语言，来直接操作native对象
- lua语法精炼，直观，native开发人员上手基本没有难度
- 使用native开发人员更为熟悉的Native编程模式，直接创建和操作各种界面对象

## 3. LuaViewSDK基础架构

> **LuaView SDK主要由Lua Engine、UI Lib、Script Manager以及Security模块构成。**

![](http://gtms03.alicdn.com/tps/i3/TB1wmiNKFXXXXbvXXXXVxDVQFXX-1750-1402.png)

1. **Lua Engine**

   Lua脚本到Native语言的桥梁。可以将Lua脚本翻译成目标语言。目前Android端使用LuaJ、iOS端使用LuaC作为脚本解析器。

2. **Lua-Native UI Lib**

   整个LuaView的核心组件，主要在Android、iOS端统一了各种UI组件，包括View、Label、Image、Button、TableView、ViewPager、Alert等等组件。开发者也使用自定义的UI组件，在Lua层直接创建和操作Native对象。

3. **Script Manager**

   Lua脚本管理器，主要完成脚本的解压、加载等功能。

4. **Security**

   主要完成脚本的验证、解密等功能。