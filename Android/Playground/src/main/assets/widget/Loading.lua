--
-- Copyright 2017 Alibaba Group
-- License: MIT
-- Website: https://alibaba.github.io/LuaViewSDK
-- User: tuoli
-- Date: 17/3/30
--

require("kit.pica")

local function start()
    loadingObjs = Pica:getInstance():render("widget/loading.xml")

    local loading = loadingObjs["loading"]
    loading:start()
end

Navigation:title("Loading.lua")
start()

