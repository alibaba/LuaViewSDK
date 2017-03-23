--
-- Created by IntelliJ IDEA.
-- User: tuoli
-- Date: 17/2/28
-- Time: 16:51
-- To change this template use File | Settings | File Templates.
--

require("kit.pickup")

local function start()
    buttonObjs = Pickup:getInstance():render("widget/button.xml")

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
