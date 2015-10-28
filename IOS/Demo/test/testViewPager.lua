--工具包引入
require "kit/lv_kit"

local width, height = System.screenSize()

local vp = PagerView({
    PageCount = 12,
    Indicator = function()
--        活动指示器
        local indicator = CirclePagerIndicator()
        indicator.size(width / 2, 200)
        indicator.pageColor(0xffff0000)
        indicator.fillColor(0xff0000ff)
        indicator.radius(10)
        indicator.strokeWidth(4)
        indicator.strokeColor(0xff00aaaa)
        indicator.backgroundColor(0x33333333)
        indicator.alignCenter()
        return indicator
    end,
    Pages = {
        Title = function(pos)
            return "Title"
        end,
        Init = function(page, pos)
            page.icon = Button()
        end,
        Layout = function(page, pos)
            page.icon.text("测试按钮"..pos)
            page.icon.frame(0, 0, width, 200)
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

vp.backgroundColor(0xaaaa0000)
vp.frame(0, 20, width, height - 20)