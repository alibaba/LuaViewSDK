-- Created by LuaView.
-- Copyright (c) 2017, Alibaba Group. All rights reserved.
--
-- This source code is licensed under the MIT.
-- For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.

--工具包引入
require "kit/lv_kit"

local width, height = System.screenSize()

vp = PagerView({
    PageCount = 4,
    Pages = {
        Title = function(pos)
            return "Title"
        end,
        Init = function(page, pos)
            page.icon = Button()
        end,
        Layout = function(page, pos)
            page.icon.text("测试按钮"..(pos-1))
            page.icon.frame(0, 0, width, 200)
            page.icon.callback(function()
                if(pos == 1) then
                    vp.looping(true)
                    vp.autoScroll(1)
                elseif(pos == 2) then
                    vp.looping(false)
                    vp.autoScroll(0)
                elseif(pos == 3) then
                    vp.autoScroll(1, true)
                elseif(pos == 4) then
                    vp.looping(true)
                end
            end)
        end
    },
    Callback = {
        Scrolling=function(pos, percent, distance)
            Toast("滑动"..pos)
            print("滚动"..pos)
        end,
        Selected=function(pos)
            Toast("选中"..pos)
            print("选中"..pos)
        end
    }
})

vp.backgroundColor(0xaaaa0000)
vp.frame(0, 20, width, height - 20)

