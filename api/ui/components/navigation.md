> Static Class

## Navigation

导航条组件，静态类，用于直接操作当前的导航栏对象

---

### *方法*

| ID   | API        | 参数                                       | 返回值  | 平台   | 备注      |
| ---- | ---------- | ---------------------------------------- | ---- | ---- | ------- |
| 1    | title      | v: String/<a href="#styled_string">StyledString</a>/<a href="#unicode">Unicode</a> | v    | -    | 导航条标题   |
| 2    | background | v: String/<a href="#image">Image</a>     | -    | -    | 设置导航条背景 |
| 3    | left       | v: Boolean                               | v    | -    | 显示左侧按钮  |
| 4    | right      | v: Boolean                               | v    | -    | 显示右侧按钮  |

### *示例*

```lua
Navigation.title("测试view")


img = Image();
img:image("http://gtms02.alicdn.com/tps/i2/TB1qmXnHpXXXXcuaXXXQG.m0FXX-640-128.jpg",function()
	Navigation.background(img)
end);
```



