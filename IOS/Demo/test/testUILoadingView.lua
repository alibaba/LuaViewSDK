--工具包引入
require "kit/lv_kit"



local loading = LoadingIndicator()
loading.backgroundColor(0xaaaa0000)
loading.size(200, 200)
loading.alignCenter()

local width = System.screenSize()

btn1 = Button()
btn1.callback(function()
    if (loading) then
        loading.show()
    end
end)

btn2 = Button()
btn2.callback(function()
    if (loading) then
        loading.hide()
    end
end)


btn1.text("Start")
btn1.frame(0, 200, width, 200)
btn2.text("Stop")
btn2.frame(0, 400, width, 200)


