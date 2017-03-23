--
-- Created by IntelliJ IDEA.
-- User: tuoli
-- Date: 17/2/28
-- Time: 16:52
-- To change this template use File | Settings | File Templates.
--

local _pica = require("kit.pica")

local function start()
    _pica:parseXml("widget/image.xml")
end

Navigation:title("Image.lua")
start()
