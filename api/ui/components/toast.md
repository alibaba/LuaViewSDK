> Class

## Toast

提示信息组件。

---

### *方法*

Toast(message)

- message: String/<a href="#styled_string">StyledString</a>/<a href="#unicode">Unicode</a>

| ID   | API  | 参数                                       | 返回值  | 平台   | 备注   |
| ---- | ---- | ---------------------------------------- | ---- | ---- | ---- |
| 1    | show | v: String/<a href="#styled_string">StyledString</a>/<a href="#unicode">Unicode</a> | -    | -    | 显示提示 |

### 示例

```lua
Toast("测试")

Toast(StyledString("测试", {fontColor=0xffff0000, backgroundColor=0xff00ff00, fontSize=50}))
```




