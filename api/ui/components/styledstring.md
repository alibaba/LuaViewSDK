> Class

## StyledString

富文本字符串，需要和其他UI组件配合使用

---

### *方法*

StyledString(text, config)

- text: String/<a href="#unicode">Unicode</a>
- config: LuaTable
  - fontSize: Number, 文字大小
  - fontColor: Number, 文字颜色
  - fontName: String, 文字样式
  - fontWeight: Number/<a href="#font_weight">FontWeight</a>, 文字权重
  - fontStyle: String/<a href="#font_style">FontStyle</a>，文字样式
  - backgroundColor: Number, 文字背景色
  - strikethrough: Boolean，是否删除线
  - underline: Boolean，是否下划线

| ID   | API    | 参数                                       | 返回值  | 平台   | 备注   |
| ---- | ------ | ---------------------------------------- | ---- | ---- | ---- |
| 1    | append | v: <a href="#styled_string">StyledString</a> | v    | -    | 新增一段 |

### *示例*

```lua
StyledString("test", { fontColor = 0xff0000ff, fontSize = 14 })

string = StyledString(Unicode(0xe607), { fontColor = 0xff00aaff, fontStyle = "bold" })

label = Label()
label:text(string) -- 给label设置styledString
```






