--
-- Copyright 2017 Alibaba Group
-- License: MIT
-- Website: https://alibaba.github.io/LuaViewSDK
-- User: tuoli
-- Date: 17/3/30
--

Navigation:title("Loading.lua")

local meta = object:new()

function meta:onCreate(args)
    self.views = pica:getInstance():render("widget/loading.xml")
    self.loading = self.views["loading"]
    self.loading:start()
end

return meta

