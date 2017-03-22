-- Created by LuaView.
-- Copyright (c) 2017, Alibaba Group. All rights reserved.
--
-- This source code is licensed under the MIT.
-- For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.

--
-- Created by IntelliJ IDEA.
-- User: song

--1 一个按钮
as1 = StyledString("一个按钮", { fontColor = 0xffff0000, backgroundColor = 0xff00ff00, fontSize = 30 })
as2 = StyledString("两个按钮", { fontColor = 0xffff0000, backgroundColor = 0xff00ff00, fontSize = 30 })
as3 = StyledString("只有内容", { fontColor = 0xffff0000, backgroundColor = 0xff00ff00, fontSize = 30 })

text = StyledString("文字", { fontColor = 0xffff0000, backgroundColor = 0xff00ff00, fontSize = 30 })


ok = StyledString("确定", { fontColor = 0xffff0000, backgroundColor = 0xff00ff00, fontSize = 30 })


Alert(as1, text, ok, function()
    print("点击了")
end)

--2 两个按钮
Alert(as2, text, ok, "取消", function()
    print("点击了")
end, function()
    print("取消了")
end)

--3 只有内容
Alert(nil, as3)

--4 只有标题
Alert("只有标题")
