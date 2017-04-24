img = Image()

view = CustomView()
view:frame(100, 100, 200, 200)
view:backgroundColor(0x000000, 0.3)

img:hidden(true)

view:onDraw(function(canvas)
    testCanvasAndroidAndIos(canvas)
end)

function testCanvasAndroidAndIos(canvas)
    print(canvas)
    -- drawLine
    canvas:color(0xff0000)
    canvas:strokeWidth(2)
    canvas:drawLine(0, 50, 100, 50)
    canvas:drawLine(50, 0, 50, 100)

    canvas:resetPaint()
    canvas:color(0x00ff00, 0.5)
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
    -- canvas:skew(1 )
    canvas:scale(1.2)
    canvas:translate(-10, -10)
    canvas:strokeWidth(10)
    canvas:textSize(15)
    canvas:bold(true)
    canvas:drawText("测试一下", 20, 150)
    canvas:alpha(0.5)
    canvas:drawImage("animate1.jpg", 0, 100, 100, 100)
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
end


img:image("https://gju1.alicdn.com/bao/uploaded/i4/100000120700895002/TB2Qu0_fXXXXXb9XpXXXXXXXXXX_!!0-0-juitemmedia.jpg_560x560Q90.jpg", function()
    view:invalidate()
    print("image load finish")
end)
 