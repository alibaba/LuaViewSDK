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
--            print("yesong", "Layout", pos-1)
            page.icon.text("测试按钮"..(pos-1))
            page.icon.frame(0, 0, width, 200)
            page.icon.callback(function()
                if(pos == 1) then
                    print("yesong", "1111")
                    vp.looping(true)
                    vp.autoScroll(1)
                elseif(pos == 2) then
                    print("yesong", "2222")
                    vp.looping(false)
                    vp.autoScroll(0)
                elseif(pos == 3) then
                    print("yesong", "3333")
                    vp.autoScroll(1, true)
                elseif(pos == 4) then
                    vp.looping(true)
                    print("yesong", "4444")
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

