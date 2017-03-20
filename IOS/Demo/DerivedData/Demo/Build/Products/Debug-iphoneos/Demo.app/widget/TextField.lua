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
    local data = File:read("widget/textfield.xml")
    print("tuoli", "xml read end")
    pica:parseXml(data)
end

start()