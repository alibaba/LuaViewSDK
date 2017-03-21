# SDK使用说明 - Android

## 如何接入

## 基本用法

```java
//1. 创建LuaView
LuaView luaview = LuaView.create(getContext());

//2. 注册扩展
luaview.registerPanel(CustomLoading.class);
luaview.register("bridge", new CustomBridge());

//3. 加载资源
luaview.load("脚本uri");

//如：
//本地, 
luaview.load("main.lua"); //加载 assets下的main.lua
//网络, 
luaview.load("http://luaview.github.com/test.zip"); //加载 网络资源test.zip，LuaView会自行下载并解压执行
```

## SDK接口

### LuaView对象接口

| API                   | 参数                                       | 返回值                     | 备注                            |
| --------------------- | ---------------------------------------- | ----------------------- | ----------------------------- |
| create                | context: Context                         | LuaView                 | 创建LuaView                     |
| createAsync           | context: Context, callback: CreatedCallback | -                       | 异步创建LuaView                   |
| load                  | url: String                              | LuaView                 | 加载指定资源（asset、本地、网络）           |
| load                  | url: String, callback: ScriptExecuteCallback | LuaView                 | 加载指定资源（asset、本地、网络），带回调       |
| loadScript            | script: String                           | LuaView                 | 加载指定脚本                        |
| loadScript            | script: String, callback: ScriptExecuteCallback | LuaView                 | 加载指定脚本，带回调                    |
| loadScriptBundle      | scriptBundle: ScriptBundle               | LuaView                 | 加载指定脚本包                       |
| loadScriptBundle      | scriptBundle: ScriptBundle, callback: ScriptExecuteCallback | LuaView                 | 加载指定脚本包，带回调                   |
| loadScriptBundle      | scriptBundle: ScriptBundle, main_entry: String, callback: ScriptExecuteCallback | LuaView                 | 加载指定脚本包，执行main_entry入口文件，带回调  |
| register              | name: String, bridge: Object             | LuaView                 | 注册一个名称为name的bridge对象          |
| unregister            | name: String                             | LuaView                 | 反注册一个名称为name的bridge对象         |
| registerPanel         | clazz: Class<? extends LVCustomPanel>    | LuaView                 | 注册一个名称为clazz类名，类型为clazz的panel |
| registerPanel         | name: String, clazz: Class<? extends LVCustomPanel> | LuaView                 | 注册一个名称为name，类型为clazz的panel    |
| registerLibs          | binders: LuaValue[]                      | LuaView                 | 注册自定义库                        |
| registerImageProvider | clazz: Class<? extends ImageProvider>    | LuaView                 | 注册一个ImageProvider             |
| getImageProvider      | -                                        | provider: ImageProvider | 获取ImageProvider               |
| callLuaFunction       | funName: String, params: Object[]        | result: Object          | 调用lua的某个全局函数                  |
| callWindowFunction    | funName: String, params: Object[]        | result: Varargs         | 调用window.callback下的某个函数       |
| getUri                | -                                        | uri: String             | 获取当前LuaView加载的Uri             |
