--工具包引入
require "kit/lv_kit"

-- 活动指示器
local SCREEN_WIDTH = System.screenSize()

function createCircleIndicator()
    local indicator = PagerIndicator()
    indicator.size(SCREEN_WIDTH / 2, 100)
--    indicator.pageColor(0xffff0000)
--    indicator.fillColor(0xff0000ff)
--    indicator.radius(10)
--    indicator.strokeWidth(4)
--    indicator.strokeColor(0xff00aaaa)
--    indicator.backgroundColor(0x33333333)
    indicator.alignCenter()
    return indicator
end

function createSelfDefinedIndicator()
    local indicator = CustomPagerIndicator({
        Init = function(cell, pos, currentPos)
            cell.btn = Button()
        end,
        Layout = function(cell, pos, currentPos)
            cell.btn.size(50, 50)
            if (pos == currentPos) then
                cell.btn.text("+")
--                cell.btn.setBackgroundColor(0xffff0000)
            else
                cell.btn.text(""..pos)
--                cell.btn.setBackgroundColor(0xff00ff00)
            end
        end
    })
    indicator.size(SCREEN_WIDTH/2, 200)
    indicator.backgroundColor(0x33333333)
    indicator.alignCenter()
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
--pagerView.indicator(createSelfDefinedIndicator())



