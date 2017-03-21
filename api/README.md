# LuaView API 基础概念

## 语法

LuaViewSDK基于Lua虚拟机，当前支持版本为5.1.4 LuaC

## 静态类 - Static Class

静态类，无法被实例化，用于提供一些静态方法调用

*示例*

```lua
local version = System.osVersion() -- 获取操作系统版本号
local network = System.network() -- 获取当前网络状态
```

## 类 - Class

类，需要实例化为对象之后使用

*示例*

```lua
local http = Http() -- 生成Http类型的对象
http:get("http://luaview.github.io", { -- 调用对象的实例方法
    query = 1
}, function(response)
    print("called success")
end)
```

## 虚拟类 - Virtual Class

虚拟类，不能直接使用。在LuaView中，目前只有一个虚拟类**BaseView**，作为所有UI类的基类存在

## 常量 - Constants

在静态类里提供，用于提供SDK定义的一些常量供使用

*示例*

```lua
local view = View()
view:align(Align.RIGHT, Align.BOTTOM) -- 调用定义在静态类中的常量来设置view的对齐属性
```

## UI类

* UI类分为容器类和控件类，容器类可以添加其它容器类，也可以添加控件类；控件类不能添加其它UI类
* 容器类和控件类都继承自一个虚拟的BaseView类

## 非UI类

* 非UI类分为系统，网络传输，存储，定时器，编码，数据，JSON，硬件相关等几类

## 目录

* [UI类](ui/uiclass.html)
* [基础类](base/baseclass.html)
* [常量](constants/constants.html)
