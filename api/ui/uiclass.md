# UI 类继承关系

UI类分为容器类和非容器类（控件类）两类。两种UI类都继承自同一个虚拟的BaseView类，其提供了一些基础的方法。容器对象可以作为容器添加其它任何的UI对象，包括容器和非容器；非容器对象无法添加其它UI对象

![](https://gw.alicdn.com/tfs/TB1zkpDQXXXXXXLXFXXXXXXXXXX-389-688.png)

*示例*

```lua
local view = View()     --创建一个View对象，这是一个容器
view:frame(0,0,200,200) --设置view的frame

local button = Button() --创建一个Button对象
button:frame(0,0,50,50) --这是button的frame

view:addView(button)    --将button添加到容器view中

button:hide() --隐藏button
```

