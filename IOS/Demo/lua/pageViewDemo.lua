--工具包引入
require "lv_kit"

local width = System.screenSize()

UIPageView({
    PageCount = 12,
    Indicator = function()
--        活动指示器
        local indicator = UIIndicator()
        indicator.alignCenter()
        return indicator
    end,
    Pages = {
        Title = function(pos)
            return "Title"
        end,
        Init = function(page, pos)
            page.icon = UIButton()
        end,
        Layout = function(page, pos)
            page.icon.setText("测试按钮"..pos)
            page.icon.setFrame(0, 0, width, 200)
        end
    },
    Listeners = {
        Scrolled=function( index )
            UIToast("Scrolled")
        end,
        Selected=function( index )
            UIToast("Selected")
        end
    }
})