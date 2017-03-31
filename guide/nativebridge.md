# Native自定义功能桥接

LuaViewSDK提供了非常强大灵活的桥接能力，可以将任意用户自定义的功能模块桥接到lua脚本层，方便开发者在lua脚本中调用App native环境里提供的自定义功能，变为环境API的一部分。那么，如何实现这一功能呢？

## iOS部分

* 首先，LuaViewSDK提供了OC对象或类注册到桥接层的接口，这样在lua脚本层可以直接使用注册的对象或者类，如下所示

  ```objectivec
  LuaView *lv = [[LuaView alloc] init]; //create a LuaView instance

  lv[@"myCustomInstance"] = [MyCustomClass sharedInstance]; //register an object to LuaViewSDK
  lv[@"MyCustomClass"] = [MyCustomClass class]; //register a Class to LuaViewSDK
  ```

  *LuaView对象实现了`luaviewObj[@"luaObj"] = nativeObj`的语法糖，方便进行注册*

* 在native中注册之后，就可以直接在lua脚本层通过注册的名称直接调用对象的native方法

  假设myCustomInstance有如下的native方法：

  ```objectivec
  -(void)method1:(NSString*)param1;
  -(void)method2:(NSString*)param1 with:(NSString*)param2 and:(NSString*)param3;
  ```

  在lua中需要这样调用

  ```lua
  --对象调用
  myCustomInstance:method1(param1)
  myCustomInstance:method2_with_and(param1, param2, param3)
  ```

  注：参数不支持block

## Android部分