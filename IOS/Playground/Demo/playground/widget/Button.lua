--
-- Created by IntelliJ IDEA.
-- User: tuoli
-- Date: 17/2/28
-- Time: 16:51
-- To change this template use File | Settings | File Templates.
--
local _pica = require("kit.pica")

local function start()
    _pica:parseXml("widget/button.xml")

    local button1 = _pica:getViewByName("button1")
    button1:callback(function()
        Toast("Clicked")
    end)
    local button2 = _pica:getViewByName("button2")
    button2:callback(function()
        Toast("Clicked")
    end)
    local button3 = _pica:getViewByName("button3")
    button3:callback(function()
        Toast("Clicked")
    end)
    local button4 = _pica:getViewByName("button4")
    button4:callback(function()
        Toast("Clicked")
    end)
    local button5 = _pica:getViewByName("button5")
    button5:callback(function()
        Toast("Clicked")
    end)
    local button6 = _pica:getViewByName("button6")
    button6:callback(function()
        Toast("Clicked")
    end)
end

Navigation:title("Button.lua")
start()
