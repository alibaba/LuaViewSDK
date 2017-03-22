-- Created by LuaView.
-- Copyright (c) 2017, Alibaba Group. All rights reserved.
--
-- This source code is licensed under the MIT.
-- For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.

screenWidth, screenHeight = System.screenSize()
bg = View()
bg.backgroundColor(0xdddddd, 0.5)
bg.size(screenWidth, 60)

a = Label()
a.text("测试")
a.backgroundColor(0xaaaa00)
a.xy(10, 10)
a.size(100, 50)

b = Label()
b.text(StyledString("测试说", {fontColor=0xff0000, fontWeight=FontWeight.BOLD--[[fontStyle="bold", fontWeight="bold"]]}))
b.fontSize(12)
b.backgroundColor(0xaa00aa)
b.cornerRadius(5)
b.xy(50, 10)

c = Label()
c.font("fonts/iconfont.ttf", 40)
c.text(Unicode(0xe617) .. "123")
c.backgroundColor(0x999999)
c.xy(180, 10)

view = Label()
view.text("测试1")
view.font(20)
view.lines(2)
view.backgroundColor(0xaaaa00)
view.frame(50, 120, 50, 50)


view2 = Label()
view2.text("测试2")
view2.font(20)
view2.lines(2)
view2.backgroundColor(0xaaaa00)
view2.frame(50, 190, 50, 50)

view3 = Label()
view3.text("测试3测试3测试3测试3测试3测试3测试3测试3测试3测试3测试3测试3测试3测试3测试3测试3测试3测试3测试3测试3测试3测试3测试3测试3测试3测试3测试3测试3测试3测试3测试3测试3测试3测试3测试3测试3测试3测试3测试3测试3测试3测试3测试3测试3测试3")
view3.font(20)
view3.lines(2)
view3.backgroundColor(0xaaaa00)
view3.frame(50, 270, 100, 100)

view4 = Label()
view4.text("测试4测试4测试4测试4测试4")
view4.font(20)
view4.lines(1)
view4.backgroundColor(0xaaaa00)
view4.frame(50, 400, 100, 100)

btn = Button()
btn.text("adjustFontSize")
btn.xy(0, 70)
btn.callback(function()
    view.text(view.text() .. "测试1")
    view.adjustFontSize()
end)


btn2 = Button()
btn2.xy(200, 70)
btn2.text("adjustSize")
btn2.callback(function()
    view2.adjustSize()
    view3.adjustSize()
    view4.adjustSize()
end)




