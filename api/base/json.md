> Static Class

## Json

JSON组件，静态类

| ID   | API     | 参数                                       | 返回值         | 平台      | 备注             |
| ---- | ------- | ---------------------------------------- | ----------- | ------- | -------------- |
| 1    | toTable | v: String/<a href="#data">Data</a>/LuaTable | r: LuaTable | -       | 给定内容转成LuaTable |
| 2    | isValid | v: String/<a href="#data">Data</a>       | r: Boolean  | Android | 是否有效JsonString |

## 示例

```lua
local luaTable = JSON.toTable('{"a":"1"}')
```

