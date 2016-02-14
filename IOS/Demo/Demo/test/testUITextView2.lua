--1.
view = Label()
view.text("测试1")
view.fontSize(30)
view.lineCount(2)
view.backgroundColor(0xaaaa0000)
view.frame(200, 200, 200, 200)


view2 = Label()
view2.text("测试2")
view2.fontSize(30)
view2.lineCount(2)
view2.backgroundColor(0xaaaa0000)
view2.frame(200, 500, 200, 200)

view3 = Label()
view3.text("测试3测试3测试3测试3测试3测试3测试3测试3测试3")
view3.fontSize(30)
view3.lineCount(2)
view3.backgroundColor(0xaaaa0000)
view3.frame(200, 800, 200, 200)

view4 = Label()
view4.text("测试4测试4测试4测试4测试4")
view4.fontSize(30)
view4.lineCount(1)
view4.backgroundColor(0xaaaa0000)
view4.frame(200, 1100, 200, 200)

btn = Button()
btn.text("adjustFontSize")
btn.callback(function()
    view.text(view.text() .. "测试1")
    view.adjustFontSize()
end)


btn2 = Button()
btn2.xy(500, 0)
btn2.text("adjustSize")
btn2.callback(function()
    view2.adjustSize()
    view3.adjustSize()
    view4.adjustSize()
end)


