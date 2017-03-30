--
-- Copyright 2017 Alibaba Group
-- License: MIT
-- Website: https://alibaba.github.io/LuaViewSDK
-- User: tuoli
-- Date: 17/3/30
--

require("kit.pica")

local function start()
    Pica:getInstance():render("widget/image.xml")
end

Navigation:title("Image.lua")
start()
