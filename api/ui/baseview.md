> Virtual Class

# BaseView

所有UI组件的基类，**无法直接用来创建UI对象**，用于提供UI对象的基础方法

---

### 方法

| ID   | API                     | 参数                                       | 返回值           | 平台      | 备注                            |
| ---- | ----------------------- | ---------------------------------------- | ------------- | ------- | ----------------------------- |
| 1    | initParams              | table: LuaTable                          | -             | Android | 初始化参数                         |
| 2    | invalidate              | -                                        | -             | -       | 强制重绘                          |
| 3    | padding                 | left: Number<br/>top: Number<br/>right: Number<br/>bottom: Number<br/> | -             | -       | 内边距                           |
| 4    | frame                   | x: Number<br/>y: Number<br/>width: Number<br/>height: Number | -             | -       | View尺寸                        |
| 5    | backgroundColor         | color: Number<br/> alpha: Number<br/>    | color, alpha  | -       | 背景色&alpha                     |
| 6    | size                    | width: Number<br/> height: Number<br/>   | width, height | -       | 尺寸                            |
| 7    | xy                      | x: Number<br/> y: Number<br/>            | x,y           | -       | x、y坐标                         |
| 8    | align                   | aligns[]: <a href="#align">Align</a>     | -             | -       | 设置自身在父容器的布局                   |
| 9    | alignLeft               | -                                        | -             | -       | 设置自身位于父容器Left&Top             |
| 10   | alignTop                | -                                        | -             | -       | 设置自身位于父容器Left&Top             |
| 11   | alignRight              | -                                        | -             | -       | 设置自身位于父容器Right&Top            |
| 12   | alignBottom             | -                                        | -             | -       | 设置自身位于父容器Left&Bottom          |
| 13   | alignLeftTop            | -                                        | -             | Android | 设置自身位于父容器Left&Top             |
| 14   | alignTopLeft            | -                                        | -             | Android | 设置自身位于父容器Left&Top             |
| 15   | alignCenterTop          | -                                        | -             | Android | 设置自身位于父容器Center&Top           |
| 16   | alignTopCenter          | -                                        | -             | Android | 设置自身位于父容器Center&Top           |
| 17   | alignRightTop           | -                                        | -             | Android | 设置自身位于父容器Right&Top            |
| 18   | alignTopRight           | -                                        | -             | Android | 设置自身位于父容器Right&Top            |
| 19   | alignLeftBottom         | -                                        | -             | Android | 设置自身位于父容器Left&Bottom          |
| 20   | alignBottomLeft         | -                                        | -             | Android | 设置自身位于父容器Left&Bottom          |
| 21   | alignCenterBottom       | -                                        | Android       | -       | 设置自身位于父容器Center&Bottom        |
| 22   | alignBottomCenter       | -                                        | Android       | -       | 设置自身位于父容器Center&Bottom        |
| 23   | alignRightBottom        | -                                        | -             | Android | 设置自身位于父容器Right&Bottom         |
| 24   | alignBottomRight        | -                                        | -             | Android | 设置自身位于父容器Right&Bottom         |
| 25   | alignCenter             | -                                        | -             | -       | 设置自身位于父容器Center               |
| 26   | alignLeftCenter         | -                                        | -             | Android | 设置自身位于父容器Left&Center          |
| 27   | alignCenterLeft         | -                                        | -             | Android | 设置自身位于父容器Left&Center          |
| 28   | alignRightCenter        | -                                        | -             | Android | 设置自身位于父容器Right&Center         |
| 29   | alignCenterRight        | -                                        | -             | Android | 设置自身位于父容器Right&Center         |
| 30   | alignCenterHorizontal   | -                                        | -             | Android | 设置自身位于父容器center in horizontal |
| 31   | alignHorizontalCenter   | -                                        | -             | Android | 设置自身位于父容器center in horizontal |
| 32   | alignCenterVertical     | -                                        | -             | Android | 设置自身位于父容器center in vertical   |
| 33   | alignVerticalCenter     | -                                        | -             | Android | 设置自身位于父容器center in vertical   |
| 34   | center                  | x: Number<br/> y: Number                 | x, y          | -       | 中心点坐标                         |
| 35   | x                       | x: Number                                | x             | -       | x坐标                           |
| 36   | y                       | y: Number                                | y             | -       | y坐标                           |
| 37   | left                    | left: Number                             | left          | -       | 距离父容器左侧边距                     |
| 38   | top                     | top: Number                              | top           | -       | 距离父容器上侧边距                     |
| 39   | right                   | right: Number                            | right         | -       | 距离父容器右侧边距                     |
| 40   | bottom                  | bottom: Number                           | bottom        | -       | 距离父容器底部边距                     |
| 41   | width                   | width: Number                            | width         | -       | 宽度                            |
| 42   | minWidth                | width: Number                            | width         | Android | 最小宽度                          |
| 43   | height                  | height: Number                           | height        | -       | 高度                            |
| 44   | centerX                 | x: Number                                | x             | -       | 中心点x坐标                        |
| 45   | centerY                 | y: Number                                | y             | -       | 中心点y坐标                        |
| 46   | visible                 | v: Boolean                               | v             | Android | 可见性                           |
| 47   | hidden                  | v: Boolean                               | v             | -       | 可见性                           |
| 48   | show                    | -                                        | -             | -       | 显示                            |
| 49   | isShow                  | -                                        | v: Boolean    | -       | 是否可见                          |
| 50   | hide                    | -                                        | -             | -       | 隐藏                            |
| 51   | isHide                  | -                                        | v: Boolean    | -       | 是否隐藏                          |
| 52   | enabled                 | v: Boolean                               | v             | -       | 是否可用                          |
| 53   | alpha                   | alpha: Number                            | alpha         | -       | 透明度                           |
| 54   | borderWidth             | width: Number                            | width         | -       | 边框宽度                          |
| 55   | borderColor             | color: Number                            | color         | -       | 边框颜色                          |
| 56   | clipsToBounds           | v: Boolean                               | v             | iOS     | View边框是否剪接                    |
| 57   | shadowPath              | v: Boolean                               | v             | iOS     | 只对边框外部加阴影                     |
| 58   | masksToBounds           | v: Boolean                               | v             | iOS     | 设置边框是否裁剪                      |
| 59   | shadowOffset            | v: Number                                | v             | iOS     | 设置View阴影偏移位置                  |
| 60   | shadowRadius            | v: Number                                | v             | iOS     | 设置View阴影高斯模糊半径                |
| 61   | shadowOpacity           | v: Number                                | v             | iOS     | 设置View阴影透明度                   |
| 62   | shadowColor             | v: Number                                | v             | iOS     | 设置View阴影颜色                    |
| 63   | sizeToFit               | -                                        | -             | -       | 适应View内容的大小                   |
| 64   | addGestureRecognizer    | -                                        | -             | iOS     | 添加手势                          |
| 65   | removeGestureRecognizer | -                                        | -             | iOS     | 移除手势                          |
| 66   | transform3D             | v: Number[]                              | -             | iOS     | 设置3D变换矩阵                      |
| 67   | anchorPoint             | x: Number<br/> y: Number                 | -             | -       | 锚点                            |
| 68   | removeFromSuper         | -                                        | -             | -       | 从父容器移除                        |
| 69   | removeFromParent        | -                                        | -             | -       | 从父容器移除                        |
| 70   | hasFocus                | -                                        | v: Boolean    | -       | 是否有焦点                         |
| 71   | requestFocus            | -                                        | -             | -       | 请求焦点                          |
| 72   | clearFocus              | -                                        | -             | -       | 取消焦点                          |
| 73   | rotation                | v: Number                                | -             | -       | 旋转角度                          |
| 74   | rotationXY              | rx: Number<br/>ry: Number                | rx, ry        | -       | 根据x坐标和y坐标得到的旋转角度，pivot        |
| 75   | scale                   | sx: Number<br/>sy: Number                | sx, sy        | -       | x，y缩放                         |
| 76   | scaleX                  | sx: Number                               | sx            | -       | x坐标缩放                         |
| 77   | scaleY                  | sy: Number                               | sy            | -       | y坐标缩放                         |
| 78   | translation             | tx: Number<br/> ty: Number               | x, y          | -       | x、y位移                         |
| 79   | translationX            | tx: Number                               | tx            | -       | x坐标位移                         |
| 80   | translationY            | ty: Number                               | ty            | -       | y坐标位移                         |
| 81   | bringToFront            | -                                        | -             | Android | 将view设置到前台                    |
| 82   | scrollTo                | sx: Number<br/>sy: Number                | -             | Android | 滚动到某个位置                       |
| 83   | scrollBy                | sx: Number<br/>sy: Number                | -             | Android | 移动一段距离                        |
| 84   | scrollX                 | sx: Number                               | sx            | Android | x方向滚动到某个位置                    |
| 85   | offsetX                 | sx: Number                               | sx            | Android | x方向滚动到某个位置                    |
| 86   | scrollY                 | sy: Number                               | sy            | Android | y方向滚动到某个位置                    |
| 87   | offsetY                 | sy: Number                               | sy            | Android | y方向滚动到某个位置                    |
| 88   | scrollXY                | sx: Number<br/> sy: Number               | sx, sy        | Android | x、y方向移动到某个位置                  |
| 89   | offsetXY                | sx: Number<br/> sy: Number               | sx, sy        | Android | x、y方向移动到某个位置                  |
| 90   | offset                  | sx: Number<br/> sy: Number               | sx, sy        | Android | x、y方向移动到某个位置                  |
| 91   | showScrollIndicator     | h: Boolean<br/> v: Boolean               | h, v          | Android | 设置滚动条是否显示（横向、纵向）              |
| 92   | callback                | v: LuaTable                              | v             | -       | 监听view的各种事件                   |
| 93   | onClick                 | v: LuaFunction                           | v             | -       | 设置view的点击事件                   |
| 94   | onLongClick             | v: LuaFunction                           | v             | Android | 设置view的长按事件                   |
| 95   | adjustSize              | -                                        | -             | -       | 调整大小以适应内容                     |
| 96   | cornerRadius            | radius: Number                           | radius        | -       | 设置边框圆角半径                      |
| 97   | startAnimation          | anims: <a href="#animation">Animation[]</a> | -             | -       | 开始播放动画                        |
| 98   | stopAnimation           | -                                        | -             | -       | 停止动画播放                        |
| 99   | isAnimating             | -                                        | v: Boolean    | -       | 是否正在播放动画                      |
| 100  | flexCss                 | v: String                                | v             | -       | 设置flex属性                      |
| 101  | flxLayout               | v: String                                | v             | iOS     | 设置flex布局                      |
| 102  | effects                 | effect: <a href="#view_effect">ViewEffect</a><br/> color: Number<br/> alpha: Number | effect        | -       | 设置view的特效                     |
| 103  | nativeView              | -                                        | v: Object     | -       | 获取NativeView                  |
| 104  | borderDash              | v: Number                                | -             | -       | 设置边框虚线                        |
| 105  | margin                  | l: Number<br/> t: Number<br/> r: Number<br/> b: Number | l, t, r, b    | Android | 边距                            |
| 106  | onTouch                 | v: LuaFunction                           | v             | 设置触摸事件  |                               |

### *示例*

> initParams

```lua
TableView():initParams({
})
```

> frame

```lua
view:frame(0, 0, 100, 100)
view:frame()
```

> padding

```lua
view:padding(5, 5, 5, 5)
```

> backgroundColor

```lua
view:backgroundColor(0xff0000, 0.5)
```

> align

```lua
view:align(Align.RIGHT, Align.BOTTOM)
```

> startAnimation

```lua
anim1 = Animation():alpha(1, 0):duration(1)
anim2 = Animation():scale(1, 0):duration(2):delay(0.2)
view:startAnimation(anim1, anim2)
```

> flexCss

```lua
view = View()
view:flexCss("margin-left: 10, sizetofit: 1, align-self: center")
```

> flxLayout

```lua
view = View()
view:flxLayout(true, function()
	print("do something") -- layout事件回调处理
end)
```

> effects

```lua
view:effects(ViewEffect.CLICK) -- 点击特效
view:effects(ViewEffect.CLICK, 0xff0000, 0.5) -- 点击特效，颜色红色，alpha=0.5
view:effects(ViewEffect.NONE) -- 无效果
```

> onClick事件注册

```lua
view:onClick(function()
    -- 点击事件回调处理
  end)
```