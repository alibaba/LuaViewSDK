--工具包引入
require "kit/lv_kit"

local width = System.screenSize()

btn1 = Button()
btn1.setCallback(function()
    Toast(StyledString("测试一下Toast", {fontColor=0xffff0000, backgroundColor=0xff00ff00, fontSize=50}))
end)

btn2 = Button()
btn2.callback(function()
    local notice = Toast();
    notice.show("xx")
end)


btn1.text("第一")
btn1.frame(0, 0, width, 100)
btn2.text("第二")
btn2.frame(0, 150, width, 100)


