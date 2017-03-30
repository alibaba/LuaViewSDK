--
-- Copyright 2017 Alibaba Group
-- License: MIT
-- Website: https://alibaba.github.io/LuaViewSDK
-- User: tuoli
-- Date: 17/3/30
--

require("kit.pica")

local function start()
    buttonObjs = Pica:getInstance():render("widget/button.xml")

    local button1 = buttonObjs["button1"]
    button1:callback(function()
        Toast("Clicked")
    end)
    local button2 = buttonObjs["button2"]
    button2:callback(function()
        Toast("Clicked")
    end)
    local button3 = buttonObjs["button3"]
    button3:callback(function()
        Toast("Clicked")
    end)
    local button4 = buttonObjs["button4"]
    button4:callback(function()
        Toast("Clicked")
    end)
    local button5 = buttonObjs["button5"]
    button5:callback(function()
        Toast("Clicked")
    end)
    local button6 = buttonObjs["button6"]
    button6:callback(function()
        Toast("Clicked")
    end)
end

Navigation:title("Button.lua")
start()
