--工具包引入
require "kit/lv_kit"

local loading
local width = System.screenSize()
local timer = Timer()

btn1 = Button()
btn1.callback(function()
    loading = LoadingDialog();
    loading.show()

    if (timer) then
        timer.start(5).callback(function()
            Toast("canceld by Timer")
            if (loading) then
                loading.hide()
            end
        end)
    end
end)

btn1.text("Start")
btn1.frame(0, 0, width, 100)

