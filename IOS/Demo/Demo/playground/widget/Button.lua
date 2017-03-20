--
-- Created by IntelliJ IDEA.
-- User: tuoli
-- Date: 17/2/28
-- Time: 16:51
-- To change this template use File | Settings | File Templates.
--

Navigation:title("Button.lua")

local function start()
    local pica = require("kit.pica")

    print("tuoli", "xml read start")
    local data = File:read("widget/button.xml")
    print("tuoli", "xml read end")
    pica:parseXml(data)

    local button1 = pica:getViewByName("button1")
    button1:callback(function()
        Toast("Clicked")
    end)
    local button2 = pica:getViewByName("button2")
    button2:callback(function()
        Toast("Clicked")
    end)
    local button3 = pica:getViewByName("button3")
    button3:callback(function()
        Toast("Clicked")
    end)
    local button4 = pica:getViewByName("button4")
    button4:callback(function()
        Toast("Clicked")
    end)
    local button5 = pica:getViewByName("button5")
    button5:callback(function()
        Toast("Clicked")
    end)
    local button6 = pica:getViewByName("button6")
    button6:callback(function()
        Toast("Clicked")
    end)
end

start()
