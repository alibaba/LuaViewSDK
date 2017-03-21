> Class

## Animation

动画类，可以配合所有的UI类使用，包括容器和非容器。有两种方式可以对view添加动画：

1. 通过`animation`对象的`with(View)`方法，设置当前`animation`的`target`
2. 通过`view`的`startAnimation(Animation[])`方法，添加`animation`对象

---

### *方法*

| ID   | API          | 参数                                       | 返回值        | 平台      | 备注                  |
| ---- | ------------ | ---------------------------------------- | ---------- | ------- | ------------------- |
| 1    | with         | v: [View](components/view.html)          | -          | -       | 设置动画target          |
| 2    | start        | -                                        | -          | -       | 开始动画                |
| 3    | alpha        | v0: Number<br/> v1: Number               | -          | -       | 设置alpha动画           |
| 4    | rotation     | v0: Number<br/> v1: Number               | -          | -       | 设置旋转动画              |
| 5    | scale        | x: Number<br/> y: Number                 | -          | -       | 设置缩放动画(x轴缩放比、y轴缩放比) |
| 6    | scaleX       | x: Number<br/>                           | -          | -       | 设置x轴缩放动画            |
| 7    | scaleY       | y: Number<br/>                           | -          | -       | 设置y轴缩放动画            |
| 8    | translation  | x: Number<br/> y: Number<br/>            | -          | -       | 设置x轴、y轴位移动画         |
| 9    | translationX | x: Number                                | -          | -       | 设置x轴位移动画            |
| 10   | translationY | y: Number                                | -          | -       | 设置y轴位移动画            |
| 11   | duration     | time: Number                             | -          | -       | 设置动画时长              |
| 12   | delay        | time: Number                             | -          | -       | 设置动画启动延时            |
| 13   | repeatCount  | count: Number                            | -          | -       | 设置动画重复测试（<0表示一直重复）  |
| 14   | interpolator | v: <a href="#interlator">Interplator<a/> | -          | -       | 插值器                 |
| 15   | cancel       | -                                        | -          | -       | 取消动画                |
| 16   | pause        | -                                        | -          | -       | 暂停动画                |
| 17   | isPaused     | -                                        | v: Boolean | -       | 动画是否暂停              |
| 18   | isRunning    | -                                        | v: Boolean | -       | 动画是否运行              |
| 19   | resume       | -                                        | -          | -       | 恢复动画                |
| 20   | reverses     | v: Boolean                               | -          | -       | 动画重复播放时是否反转         |
| 21   | values       | v: Number[]                              | -          | -       | 设置动画用到的参数           |
| 22   | callback     | v: LuaTable                              | -          | -       | 设置动画的回调             |
| 23   | onStart      | v: LuaFunction                           | -          | -       | 动画开始回调              |
| 24   | onEnd        | v: LuaFunction                           | -          | -       | 动画结束回调              |
| 25   | onRepeat     | v: LuaFunction                           | -          | -       | 动画重复回调              |
| 26   | onCancel     | v: LuaFunction                           | -          | -       | 动画取消回调              |
| 27   | onPause      | v: LuaFunction                           | -          | -       | 动画暂停回调              |
| 28   | onUpdate     | v: LuaFunction                           | -          | Android | 动画状态更新回调            |
| 29   | onResume     | v: LuaFunction                           | -          | -       | 动画恢复回调              |

### *示例*

```lua
-- alpha动画
animation = Animation():scale(2, 0.5):duration(2):delay(1)

-- 位移动画

-- 方法一
view = View()
Animation():with(view):translation(100, -100):duration(3):interpolator(Interpolator.ACCELERATE_DECELERATE):callback({
    onStart = function()
        print("Running")
    end,
    onCancel = function()
        print("Canceled")
    end,
    onEnd = function()
        print("End")
    end,
    onPause = function()
        print("Paused")
    end,
    onResume = function()
        print("Running")
    end,
})

-- 方法二
view2 = View()
anim1 = Animation():alpha(1, 0):duration(1)
anim2 = Animation():scale(1, 0):duration(2):delay(0.2)
view2:startAnimation(anim1, anim2)
```

