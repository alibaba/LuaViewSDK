> Class

## View

所有UI容器类的基类，容器类，继承自-> [BaseView](../baseview.html)

------

### 方法

| ID   | API            | 参数             | 返回值  | 平台   | 备注                   |
| ---- | -------------- | -------------- | ---- | ---- | -------------------- |
| 1    | onShow         | v: LuaFunction | v    | -    | 显示监听                 |
| 2    | onHide         | v: LuaFunction | v    | -    | 隐藏监听                 |
| 3    | onBack         | v: LuaFunction | v    | -    | 返回按钮监听               |
| 4    | onLayout       | v: LuaFunction | v    | -    | 布局监听                 |
| 5    | addView        | v: View        | -    | -    | 添加子View              |
| 6    | removeView     | v: View        | -    | -    | 移除子View              |
| 7    | removeAllViews | -              | -    | -    | 移除所有子View            |
| 8    | children       | v: LuaFunction | -    | -    | 子View构造函数            |
| 9    | flexChildren   | v: View[]      | -    | -    | Flexbox 设置childViews |

### *示例*

> onShow

```lua
view:onShow(function()
	print("i am show")
end)
```

> onHide

```lua
view:onHide(function()
	print("i am hide")
end)
```

> onBack

```lua
view:onBack(function()
	print("back pressed")
end)
```

> onLayout

```lua
view:onLayout(function()
	print("i am layouted")
end)
```

> addView

```lua
child = View()
parent = View()
parent:addView(child)
```

> removeView

```lua
child = View()
parent = View()
parent:addView(child)
parent:removeView(child)
```

> removeAllViews

```lua
child = View()
parent = View()
parent:addView(child)
parent:removeAllViews()
```

> children

```lua
parent.children(function(parent) -- 所有在函数里创建的View都会被自动添加到parent里
	view = View()
	...
end)
```

> flexChildren

```lua
child1 = View()
child2 = View()
parent = View()
parent:flexChildren(child1, child2)
```