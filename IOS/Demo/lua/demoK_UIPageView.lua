--工具包引入
require "lv_kit"

local scrW, scrH= System.screenSize()

pageView = UIPageView ();
pageView.delegate = {
    PageCount = 12,
    Pages = {
        Init = function(page, pos)
            page.button = UIButton()
        end,
        Layout = function(page, pos)
            page.button.setText("测试按钮"..pos)
            page.button.setFrame(0, 0, scrW, 200)
            page.button.setBackgroundColor(0x777777);
            page.button.delegate = ^(){
                print("第",pos,"页");
            }
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
};


pageView.setFrame(0, 0, scrW, scrH-64 )