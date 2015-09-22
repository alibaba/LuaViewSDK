--工具包引入
require "lv_kit"

local width, height= System.screenSize()

pageView = UIPageView({
    PageCount = 12,
    Pages = {
        Init = function(page, pos)
            page.icon = UIButton()
        end,
        Layout = function(page, pos)
            page.icon.setText("测试按钮"..pos)
            page.icon.setFrame(0, 0, width, 200)
        end
    },
    Callback = {

        Scrolling = function( pageIndex, percent , offset )
            print("Scrolling", pageIndex, percent, offset)
        end,

        ScrollEnd = function( pageIndex )
            print("ScrollEnd", pageIndex )
        end
        
    }
})

windowWidth , windowHeight = window.size();
pageView.setFrame(0,0,windowWidth,windowHeight-64)