> Class

## Data

二进制数据类

初始化方法：Data(str)

* str: String/JsonString

| ID   | API      | 参数             | 返回值         | 平台   | 备注             |
| ---- | -------- | -------------- | ----------- | ---- | -------------- |
| 1    | append   | v: Data/byte[] | -           | -    | 新增部分数据         |
| 2    | toString | code: String   | v: String   | -    | 转成String(编码格式) |
| 3    | toJson   | -              | v: String   | -    | 转成Json String  |
| 4    | toTable  | -              | v: LuaTable | -    | 转成LuaTable     |

## 示例

```
Data("a")

Data(97, "abc", "def")

Data('{"a":"1"}')

Data('a'):toString("latin-1")

Data('{"a":"1"}'):toTable()

Data('{"a":"1"}'):toJson()
```
