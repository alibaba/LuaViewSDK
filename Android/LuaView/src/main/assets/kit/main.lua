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
    if (args and type(args) == "table") then
        local page = require(args.page)
        if (page.onCreate == nil) then
            Toast("请实现" .. pageName .. ".lua页面的meta:onCreate()方法!")
        else
            page:onCreate(args)
        end
    else
        Toast("页面间透传参数错误!")
    end
end

main(Bridge:args())
