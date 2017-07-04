--工具包引入
require "kit/lv_kit"


-- 活动指示器
local SCREEN_WIDTH = System.screenSize()

local scrollView = HorizontalScrollView()
b0 = Button()
b0.text("scrollTo")
b0.callback(function()
    scrollView.offset(20, 0)
end)

b1 = Button()
b1.text("scrollBy")
b1.xy(300, 0)
b1.callback(function()
    scrollView.scrollBy(20, 0)
end)

b3 = Button()
b3.text("smoothScrollTo")
b3.xy(0, 200)
b3.callback(function()
    scrollView.offset(20, 0, true)
end)

b4 = Button()
b4.text("smoothScrollBy")
b4.xy(500, 200)
b4.callback(function()
    scrollView.smoothScrollBy(20, 0)
end)


for i = 0, 10, 1 do
    local btn = Label()
    btn.text("BTN" .. i)
    btn.frame(i * 400, 0, 398, 198)
    btn.backgroundColor(0xff00ff00)
    scrollView.addView(btn)
end

scrollView.xy(0, 500)
scrollView.size(SCREEN_WIDTH, 200)
scrollView.backgroundColor(0xffcccccc)
