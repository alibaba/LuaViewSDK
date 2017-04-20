--工具包引入
require "lv_kit"

local scrW, scrH= System:screenSize()

pageView = PagerView{
    PageCount = 3,
    Pages = {
        Init = function(page, pos)
            page.button = Button()
        end,
        Layout = function(page, pos)
            page.button:title("测试按钮"..pos)
            page.button:frame(0, 0, scrW, 200)
            page.button:backgroundColor(0x777777);
            page.button:callback( function()
                print("第",pos,"页");
                pageView:currentPage(pos+1);
                print(pageView:currentPage() );
            end)
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

pageView:frame(0, 0, scrW, scrH-64 )

indicator = PagerIndicator();
indicator:pageColor(0xff00FF);
pageView:indicator( indicator );
indicator:frame(0,10,scrW,100);


pageView:autoScroll(1, false)



