--
-- Copyright 2017 Alibaba Group
-- License: MIT
-- Website: https://alibaba.github.io/LuaViewSDK
-- User: tuoli
-- Date: 17/3/30
--

Navigation:title("Image.lua")

local meta = object:new()

function meta:onCreate(args)
    self.views = pica:getInstance():render("widget/image.xml")
end

return meta
