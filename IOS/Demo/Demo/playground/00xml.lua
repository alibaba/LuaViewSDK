--
-- Created by IntelliJ IDEA.
-- User: tuoli
-- Date: 17/3/11
-- Time: 15:24
-- To change this template use File | Settings | File Templates.
--

Navigation:title("XML.lua")

local function start()
    local pica = require("kit.pica")

    local xml = File:read("xml/test_view.xml")
    pica:parseXml(xml)

    local view = pica:getViewByName("button4")
    if (view) then
        view:backgroundColor(0x000fff)
    end
end

start()