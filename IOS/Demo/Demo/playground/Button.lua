--
-- Created by IntelliJ IDEA.
-- User: tuoli
-- Date: 17/2/28
-- Time: 16:51
-- To change this template use File | Settings | File Templates.
--

Navigation:title("Button.lua")

local function start()
pica = require("kit.pica"):new()

    local xml = File:read("xml/button.xml")
    pica:parseXml(xml)

    local leftbutton3 = pica:getViewByName("left.button3")
    leftbutton3:callback(function()
        Toast("Clicked")
    end)
end

start()
