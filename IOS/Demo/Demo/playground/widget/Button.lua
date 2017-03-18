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
    local callback = function(data)
        print("tuoli", "xml read end")
        pica:parseXml(data)
        local leftbutton3 = pica:getViewByName("left.button3")
        leftbutton3:callback(function()
            Toast("Clicked")
        end)
    end
    File:read("widget/button.xml", callback)
end

start()
