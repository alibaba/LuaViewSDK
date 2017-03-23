-- Created by LuaView.
-- Copyright (c) 2017, Alibaba Group. All rights reserved.
--
-- This source code is licensed under the MIT.
-- For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.

--工具包引入
require "kit/lv_kit"

-- 活动指示器
local SCREEN_WIDTH = System.screenSize()

function createCircleIndicator()
    local indicator = PagerIndicator()
--    indicator.size(SCREEN_WIDTH / 2, 100)
--    indicator.alignCenter()
    indicator.frame(10, 100, SCREEN_WIDTH / 2, 100)
    indicator.selectedColor(0xff0000)
    indicator.unselectedColor(0x00ff00)
--    indicator.fillColor(0xff0000)
--    indicator.pageColor(0x00ff00)
    return indicator
end

pagerView = PagerView({
    PageCount = 12,
    Pages = {
        Title = function(pos)
            return "Title"
        end,
        Init = function(page, pos)
            page.icon = Button()
        end,
        Layout = function(page, pos)
            page.icon.text("测试按钮"..pos)
            page.icon.frame(0, 0, SCREEN_WIDTH, 50)
        end
    },
    Callback = {
        Scrolling=function(pos, percent, distance)
            Toast("滑动"..pos)
        end,
        Selected=function(pos)
            Toast("选中"..pos)
        end
    }
})

--活动指示器
pagerView.indicator(createCircleIndicator())



