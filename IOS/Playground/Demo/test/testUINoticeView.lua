--工具包引入
require "kit/lv_kit"

local width = System.screenSize()

btn1 = Button()

btn1.callback(function()
    Notice("测试一下Toast")
end)

btn2 = Button()
btn2.callback(function()
    local notice = Notice();
    notice.show("xx")
end)


btn1.text("第一")
btn1.frame(0, 0, width, 200)
btn2.text("第二")
btn2.frame(0, 700, width, 200)


