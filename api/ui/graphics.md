> Class

## CustomView

绘图专用UI类，容器类，继承自 -> [View](view.html)

### *方法*

| ID   | API    | 参数       | 返回值  | 平台   | 备注            |
| ---- | ------ | -------- | ---- | ---- | ------------- |
| 1    | onDraw | function | -    | -    | 回调参数为canvas对象 |





<br/>

<br/>

> Class

## Canvas

画布类，不直接生成，通过CustomView的onDraw方法回调进行使用，详见示例

---

### *方法*

### a. 绘制API

| ID   | API           | 参数                                       | 返回值  | 平台   | 备注                                       |
| ---- | ------------- | ---------------------------------------- | ---- | ---- | ---------------------------------------- |
| 1    | drawLine      | x1: Number<br/> y1: Number<br/> x2: Number<br/> y2: Number<br/> | -    | -    | 绘制线条。(x1,y1), (x2, y2)分别为起点终点            |
| 2    | drawPoint     | x: Number<br/> y: Number<br/>            | -    | -    | 绘制点                                      |
| 3    | drawRect      | x: Number<br/> y: Number<br/> w: Number<br/> h: Number<br/> | -    | -    | 绘制矩形。x起点x坐标，y起点y坐标，w宽度，h高度               |
| 4    | drawRoundRect | x: Number<br/> y: Number<br/> w: Number<br/> h: Number<br/> rx: Number<br/> ry: Number<br/> | -    | -    | 绘制圆角矩形。x起点x坐标，y起点y坐标，w宽度，h高度，rx为x轴圆角半径，ry为y轴圆角半径 |
| 5    | drawCircle    | x: Number<br/> y: Number<br/> r: Number  | -    | -    | 绘制圆。(x, y)为圆心坐标，r为半径                     |
| 6    | drawOval      | x: Number<br/> y: Number<br/> rx: Number<br/> ry: Number | -    | -    | 绘制椭圆。(x, y)为圆心坐标，rx, ry为椭圆半径             |
| 7    | drawArc       | x: Number<br/> y: Number<br/> w: Number<br/> h: Number<br/> startAngle: Number<br/> sweepAngle: Number<br/> useCenter: Boolean | -    | -    | 绘制扇形。(x,y)为左上角坐标，(w,h)为扇形宽高，startAngle为开始角度，sweepAngle为覆盖角度, useCenter为是否覆盖完整扇形面积（默认为false） |
| 8    | drawText      | text: String/<a href="#styled_string">StyledString<a/>/<a href="#unicode">Unicode<a/> <br/> x: Number<br/> y:Number | -    | -    | 绘制文本                                     |
| 9    | drawImage     | image: String/<a href="#image">Image</a> <br/> x: Number<br/> y: Number<br/> w: Number<br/> h: Number | -    | -    | 绘制图片，支持本地图和Image对象                       |

### b. 画笔属性API

| ID   | API         | 参数                                       | 返回值  | 平台   | 备注       |
| ---- | ----------- | ---------------------------------------- | ---- | ---- | -------- |
| 1    | color       | color: Number                            | -    | -    | 设置画笔颜色   |
| 2    | alpha       | alpha: Number                            | -    | -    | 设置画笔透明度  |
| 3    | strokeWidth | w: Number                                | -    | -    | 设置画笔粗细   |
| 4    | style       | style: <a href="#paint_style">PaintStyle</a> | -    | -    | 设置画笔填充样式 |
| 5    | textSize    | size: Number                             | -    | -    | 设置文本字体大小 |
| 6    | font        | name: String                             | -    | -    | 设置字体名称   |
| 7    | bold        | bold: Boolean                            | -    | -    | 设置是否粗体   |
| 8    | resetPaint  | -                                        | -    | -    | 重置画笔所有属性 |

### c. 画布变换API

| ID   | API       | 参数                                       | 返回值  | 平台      | 备注                       |
| ---- | --------- | ---------------------------------------- | ---- | ------- | ------------------------ |
| 1    | translate | dx: Number<br/> dy: Number               | -    | -       | 位移变换，dx为x轴移动距离，dy为y轴移动距离 |
| 2    | scale     | sx: Number<br/> sy: Number               | -    | -       | 缩放变换，sx为x轴缩放比率，sy为y轴缩放比率 |
| 3    | rotate    | r: Number<br/> x: Number <br/> y: Number | -    | -       | 旋转变换，r为角度；(x,y) 坐标（可选）   |
| 4    | skew      | x: Number<br/> y: Number<br/>            | -    | Android | 斜切变换，xy斜切比率              |

### d. 其它API

| ID   | API       | 参数                                       | 返回值           | 平台   | 备注            |
| ---- | --------- | ---------------------------------------- | ------------- | ---- | ------------- |
| 1    | nativeObj | -                                        | native canvas | -    | 获取对应的native对象 |
| 2    | save      | -                                        | -             | -    | 保存当前画布状态      |
| 3    | restore   | -                                        | -             | -    | 恢复当前画布状态      |
| 4    | clipRect  | left: Number<br/> top: Number<br/> right: Number<br/> bottom: Number | -             | -    | 裁剪矩形区域        |
| 5    | size      | -                                        | w, h          | -    | 获取画布尺寸        |



### *示例*

```lua
local view = CustomView() -- onDraw方法需要配合CustomView使用
view.onDraw(function(canvas)
	print(canvas)
	-- drawLine
	canvas:color(0xff0000)
	canvas:strokeWidth(2)
	canvas:drawLine(0, 50, 100, 50)
	canvas:drawLine(50, 0, 50, 100)

	canvas:resetPaint()
	canvas:color(0x00ff00)
	canvas:alpha(0.5)
	canvas:drawLine(0, 0, 100, 0)
	canvas:drawLine(100, 0, 100, 100)
	canvas:drawLine(100, 100, 0, 100)
	canvas:drawLine(0, 100, 0, 0)
	canvas:drawLine(0, 0, 100, 100)
	canvas:drawLine(100, 0, 0, 100)

	-- drawPoint
	canvas:color(0xff0000)
	canvas:strokeWidth(2)
	canvas:drawPoint(1, 5)
	canvas:drawPoint(99, 93)

	-- drawRect
	canvas:resetPaint()
	canvas:style(PaintStyle.STROKE)
	canvas:drawRect(5, 5, 5, 5)
	canvas:style(PaintStyle.FILL)
	canvas:drawRect(10, 10, 5, 5)

	-- drawRoundRects
	canvas:drawRoundRect(45, 1, 5, 5, 2, 2)
	canvas:drawRoundRect(45, 5, 10, 5, 2, 2)

	-- drawCircle
	canvas:drawCircle(80, 0, 5)
	canvas:drawCircle(80, 15, 5)

	-- drawText
	canvas:textSize(20)
	canvas:drawText("x", 20, 55)
	canvas:textSize(14)
	canvas:drawText("y", 20, 65)
	canvas:resetPaint()

	-- drawOval
	canvas:drawOval(45, 50, 25, 10)
	canvas:drawOval(45, 60, 25, 10)

	-- draw Arc
	canvas:drawArc(30, 30, 20, 20, 0, 90, true)

	-- drawBitmap
	canvas:save()
	canvas:rotate(-10, 100, 100)
	canvas:scale(1.2)
	canvas:translate(-10, -10)
	canvas:strokeWidth(10)
	canvas:textSize(15)
	canvas:bold(true)
	canvas:drawText("测试一下", 20, 150)
	canvas:alpha(0.5)
	canvas:drawImage("animate1", 0, 100, 100, 100)
	canvas:restore()
	canvas:resetPaint()

	print(img)
	if(img) then
			canvas:drawImage(img, 100, 0, 100, 100)
	end

	-- clipRect
	canvas:clipRect(100, 100, 35, 35)
	canvas:drawCircle(100, 100, 40)

	canvas:clipRect(150, 150, 30, 30)
	canvas:drawText("TestABCDEFGHEFGHIJKLMOPQRST", 150, 150)

	print(canvas:nativeObj())
end)



-- ps
1. Android所有绘制API支持同时多个参数，如：
canvas:drawLine({
        { 0, 0, 100, 0 },
        { 100, 0, 100, 100 },
        { 100, 100, 0, 100 },
        { 0, 100, 0, 0 },
        { 0, 0, 100, 100 },
        { 100, 0, 0, 100 }
    })
2. Android所有绘制API支持设置绘制参数，如：		
canvas:drawText({
        { "xx", 20, 75, { color = 0x0000ff, strikeThrough = true, textSize = 20, textSkewX = 1.5, bold = true } },
        { "yy", 20, 85 },
    }, { color = 0x0fff00, underline = true, textSize = 10, textScaleX = 3, letterSpacing = 0.2, linearText = true })
```



