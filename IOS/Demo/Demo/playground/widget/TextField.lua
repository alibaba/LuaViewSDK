--
-- Created by IntelliJ IDEA.
-- User: tuoli
-- Date: 17/2/28
-- Time: 16:52
-- To change this template use File | Settings | File Templates.
--

require("kit.common")
require("kit.platform")

local _pica = require("kit.pica")

local function start()
    if (Platform.isAndroid) then
        _pica:parseXml("widget/textfield_android.xml")
    else
        _pica:parseXml("widget/textfield_ios.xml")
    end
end

Navigation:title("TextField.lua")
start()