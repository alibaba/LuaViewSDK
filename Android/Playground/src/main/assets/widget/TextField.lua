--
-- Copyright 2017 Alibaba Group
-- License: MIT
-- Website: https://alibaba.github.io/LuaViewSDK
-- User: tuoli
-- Date: 17/3/30
--

require("kit.pica")

local function start()
    if (Sys.android) then
        Pica:getInstance():render("widget/textfield_android.xml")
    else
        Pica:getInstance():render("widget/textfield_ios.xml")
    end
end

Navigation:title("TextField.lua")
start()