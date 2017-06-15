-- 活动指示器
local SCREEN_WIDTH = System:screenSize()

local scrollView = HScrollView()


for i = 0, 10, 1 do
    local btn = Label()
    btn:text("BTN" .. i)
    btn:frame(i * 60, 10, 59, 80)
    btn:backgroundColor(0xff00ff00)
    scrollView:addView(btn)
end

scrollView:xy(0, 300)
scrollView:size(SCREEN_WIDTH, 100)
scrollView:backgroundColor(0xffcccccc)
scrollView:showScrollIndicator(false)


scrollView:callback({
    Scrolling = function(x, y)
        print("Scrolling", x, y)
        print(scrollView:offset())
    end
})
