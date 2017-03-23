--
-- Created by IntelliJ IDEA.
-- User: tuoli
-- Date: 17/2/28
-- Time: 16:52
-- To change this template use File | Settings | File Templates.
--

Navigation:title("TextField.lua")

local function start()
    local pica = require("kit.pica")

    print("tuoli", "xml read start")
    if (System:android()) then
        local xml = File:read("widget/textfield_android.xml")
        print("tuoli", "xml read end")
        pica:parseXml(xml)
    else
        local xml = File:read("widget/textfield_ios.xml")
        print("tuoli", "xml read end")
        pica:parseXml(xml)
    end
end

start()