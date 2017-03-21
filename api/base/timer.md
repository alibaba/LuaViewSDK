> Class

# Timer

定时器

---

| ID   | API         | 参数             | 返回值  | 平台   | 备注   |
| ---- | ----------- | -------------- | ---- | ---- | ---- |
| 1    | delay       | v: Number      | v    | -    | 启动延时 |
| 2    | repeat      | v: Number      | v    | -    | 重复次数 |
| 3    | repeatCount | v: Number      | v    | -    | 重复次数 |
| 4    | interval    | v: Number      | v    | -    | 重复间隔 |
| 5    | start       | v: Number      | v    | -    | 启动   |
| 6    | callback    | v: LuaFunction | v    | -    | 回调   |
| 7    | cancel      | -              | -    | -    | 取消   |

## 示例

```lua
local timer = Timer()
timer:interval(0.1)
timer:repeatCount(1000)
timer:callback(function(){
  --定时器回调
})
timer:start()
```
