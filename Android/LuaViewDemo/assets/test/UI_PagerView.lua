--工具包引入
require "kit/lv_kit"

local width, height = System.screenSize()

vp = PagerView({
    PageCount = 5,
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
            page.icon.callback(function()
                if(pos % 2 ~= 0) then
                    print("aaa")
                    vp.autoScroll(1)
                else
                    print("bbb")
                    vp.autoScroll(0)
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

vp.setBackgroundColor(0xaaaa0000)
vp.setFrame(0, 20, width, height - 20)

