-- Created by LuaView.
-- Copyright (c) 2017, Alibaba Group. All rights reserved.
--
-- This source code is licensed under the MIT.
-- For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.

--工具包引入
require "kit/lv_kit"


-- 活动指示器
local SCREEN_WIDTH = System.screenSize()

local scrollView = HScrollView()
b0 = Button()
b0.text("scrollTo")
b0.callback(function()
    scrollView.offset(2, 0)
end)

b1 = Button()
b1.text("scrollBy")
b1.xy(100, 0)
b1.callback(function()
    scrollView.scrollBy(20, 0)
end)

b3 = Button()
b3.text("smoothScrollTo")
b3.xy(0, 75)
b3.callback(function()
    scrollView.offset(20, 0, true)
end)

b4 = Button()
b4.text("smoothScrollBy")
b4.xy(150, 75)
b4.callback(function()
    scrollView.smoothScrollBy(20, 0)
end)


for i = 0, 10, 1 do
    local btn = Label()
    btn.text("BTN" .. i)
    btn.frame(i * 60, 10, 59, 80)
    btn.backgroundColor(0xff00ff00)
    scrollView.addView(btn)
end

scrollView.xy(0, 300)
scrollView.size(SCREEN_WIDTH, 100)
scrollView.backgroundColor(0xffcccccc)
scrollView.showScrollIndicator(false)
