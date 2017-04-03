--
-- Copyright 2017 Alibaba Group
-- License: MIT
-- Website: https://alibaba.github.io/LuaViewSDK
-- User: tuoli
-- Date: 17/3/30
--

require("kit.object")
require("kit.util")
require("kit.sys")
require("kit.pica")

function main(args)
    local page = require(args)
    if (page.onCreate == nil) then
        Toast("请实现" .. pageName .. ".lua页面的meta:onCreate()方法!")
    else
        page:onCreate()
    end
end

main(Bridge:args())
