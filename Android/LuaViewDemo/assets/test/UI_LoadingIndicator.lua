-- Created by LuaView.
-- Copyright (c) 2017, Alibaba Group. All rights reserved.
--
-- This source code is licensed under the MIT.
-- For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.

--工具包引入
require "kit/lv_kit"



local loading = LoadingIndicator()
loading.backgroundColor(0xaaaa0000)
loading.size(50, 50)
loading.alignCenter()

local width = System.screenSize()

btn1 = Button()
btn1.callback(function()
    if (loading) then
        loading.show()
    end
end)

btn2 = Button()
btn2.callback(function()
    if (loading) then
        loading.hide()
    end
end)


btn1.text("Start")
btn1.frame(0, 100, width, 100)
btn2.text("Stop")
btn2.frame(0, 300, width, 100)


