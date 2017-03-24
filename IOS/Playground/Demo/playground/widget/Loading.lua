--
-- Created by IntelliJ IDEA.
-- User: tuoli
-- Date: 17/2/28
-- Time: 16:52
-- To change this template use File | Settings | File Templates.
--

local _pica = require("kit.pica")

local function start()
    _pica:parseXml("widget/loading.xml")

    local loading = _pica:getViewByName("loading")
    loading:start()
end

Navigation:title("Loading.lua")
start()

