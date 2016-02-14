--工具包引入
require "kit/lv_kit"

local loading
local width = System.screenSize()

btn1 = Button()
btn1.callback(function()
    loading = LoadingDialog();
    loading.show()
end)

btn2 = Button()
btn2.callback(function()
    if(loading) then
        loading.hide()
    end
end)


btn1.text("Start")
btn1.frame(0, 0, width, 200)
btn2.text("Stop")
btn2.frame(0, 700, width, 200)


