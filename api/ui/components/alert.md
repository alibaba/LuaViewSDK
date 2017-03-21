> Class

## Alert

对话框组件，非容器。继承自 -> [BaseView](../baseview.html)

---

### *方法*

Alert(title, content, buttonTexts[], buttonCallbacks[]) **初始化之后直接展示**

- title: String/<a href="#styled_string">StyledString</a>/<a href="#unicode">Unicode</a>
- content: String/<a href="#styled_string">StyledString</a>/<a href="#unicode">Unicode</a>
- buttonTexts[]: String/<a href="#styled_string">StyledString</a>/<a href="#unicode">Unicode</a> []
- buttonCallbacks[]: LuaFunction[]

### *示例*

> Alert

```lua
Alert("提示", "这是一个提示", "OK", function()
	print("OK clicked")
end)


Alert("提示", "这是一个提示", "OK", "Cancel", function()
	print("OK clicked")
end, function()
	print("Cancel clicked")
end)


local as1 = StyledString("一个按钮", { fontColor = 0xffff0000, backgroundColor = 0xff00ff00, fontSize = 30 })
local text = StyledString("文字", { fontColor = 0xffff0000, backgroundColor = 0xff00ff00, fontSize = 30 })
local ok = StyledString("确定", { fontColor = 0xffff0000, backgroundColor = 0xff00ff00, fontSize = 30 })
Alert(as1, text, ok, function()
    print("点击了")
end)
```
