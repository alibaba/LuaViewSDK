-- Created by LuaView.
-- Copyright (c) 2017, Alibaba Group. All rights reserved.
--
-- This source code is licensed under the MIT.
-- For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.

local screenWidth, screenHeight = System.screenSize()

-- Test Label
local label = Label()
label.text(System.ios() and "iOS" or "Android")
label.textColor(0x000000)
label.frame(0, 0, screenWidth, screenHeight)
label.textAlign(TextAlign.CENTER)

-- Test Button
local btn = Button()
btn.size(200, 200)
btn.text("Click Me")
btn.backgroundColor(0xff0000)
btn.callback(function()
    Alert("Hello LuaView")
end)
