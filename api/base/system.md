> Static Class

# System

系统信息类，静态类，主要用于获取运行环境的各类环境变量，以及部分硬件操作

---

## 使用

| ID   | API          | 参数         | 返回值                      | 平台   | 备注                                       |
| ---- | ------------ | ---------- | ------------------------ | ---- | ---------------------------------------- |
| 1    | ios          | -          | v: Boolean               | -    | 是否iOS平台                                  |
| 2    | android      | -          | v: Boolean               | -    | 是否Android平台                              |
| 3    | sdkVersion   | -          | v: String                | -    | LuaViewSDK版本                             |
| 4    | osVersion    | -          | v: String                | -    | 操作系统版本                                   |
| 5    | platform     | -          | v: String                | -    | 平台系统型号                                   |
| 6    | scale        | -          | v: Number                | -    | 屏幕缩放比                                    |
| 7    | device       | -          | v: LuaTable              | -    | 设备信息                                     |
| 8    | screenSize   | -          | w: Number<br/> h: Number | -    | 屏幕尺寸                                     |
| 9    | network      | -          | v: String                | -    | 网络类型("none", "2g", "3g", "4g", "wifi", "unknown") |
| 10   | gc           | -          | -                        | -    | 执行内存回收                                   |
| 11   | keepScreenOn | v: Boolean | -                        | -    | 是否保持屏幕常亮                                 |

## 示例


```lua
network = System.network() --获取系统当前的网络状态
osVersion = System.osVersion() --获取宿主操作系统版本
sdkVersion = System.sdkVersion() --获取LuaViewSDK版本号
```
