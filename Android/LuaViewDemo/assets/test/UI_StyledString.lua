-- Created by LuaView.
-- Copyright (c) 2017, Alibaba Group. All rights reserved.
--
-- This source code is licensed under the MIT.
-- For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.

--1. 普通设置url，size，center，contentmode等

as0 = StyledString("测试一啊12345", { fontColor = 0xffffff })
as1 = StyledString("测试一啊12345", { fontColor = 0xffff0000, fontWeight = 600})
as11 = StyledString("测试一啊12345", { fontColor = 0xffff0000, fontStyle = "bold" })
as2 = StyledString("测试2", { backgroundColor = 0xaa00ff00, fontSize = 10 })
as3 = StyledString("3333", { fontColor = 0xff0000ff, fontSize = 14 })
as4 = StyledString(Unicode(0xe607), { fontColor = 0xff00aaff, fontSize = 40 })


label0 = Label()
label0.text(as0)
print(label0.textColor())

label = Label()
label.xy(0, 20)
label.text(as1)

label2 = Label()
label2.xy(0, 40)
label2.text(as11)

button = Button()
button.text(as2)
button.xy(0, 60)


button2 = Button()
button2.text(as1 + as2 + as3)
button2.xy(0, 150)


button3 = Button()
button3.fontName("fonts/iconfont");
button3.text(as4 + as2)
button3.xy(0, 200)

print(button3.fontName())








