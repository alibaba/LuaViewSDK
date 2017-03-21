> Class

## CustomPanel

自定义组件，用于包装native的自定义ui控件，容器。继承自-> [View](view.html)

---

### *方法*

| ID   | API           | 参数   | 返回值  | 平台   | 备注           |
| ---- | ------------- | ---- | ---- | ---- | ------------ |
| 1    | nativeView    | -    | -    | -    | 获取NativeView |
| 2    | getNativeView | -    | -    | -    | 获取NativeView |


供第三方扩展用，**不可直接创建**。第三方通过扩展CustomPanel，实现已有的 Native UI 组件与 lua 互操作。

扩展后的CustomPanel，有所有[View](view.html)容器的API，如 frame、size、backgroundColor 等。

### *示例*

> CustomPanel 扩展 - Android

```java
//1. 扩展LVCustomPanel，创建Native UI，并将创建出来的View添加到CustomPanel里

public class CustomLoading extends LVCustomPanel {

    public CustomLoading(Globals globals, LuaValue metaTable, Varargs varargs) {
        super(globals, metaTable, varargs);
    }

    @Override
    public void initPanel() {
        final View customLoading = new NativeLoading(getContext());
        LayoutParams layoutParams = LuaViewUtil.createRelativeLayoutParamsWW();
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        customLoading.setVisibility(View.VISIBLE);
        addView(customLoading, layoutParams);
    }
}

// 2. 使用LuaView对象初始化的时候注册该 CustomPanel

luaview.registerPanel(CustomLoading.class)

//或

luaview.registerPanel("CustomLoading", CustomLoading.class)
```


```lua
-- 3. 在lua里使用该 CustomPanel，CustomPanel有所有View的api

local loading = CustomLoading()
loading:frame(0, 0, 100, 100)
```