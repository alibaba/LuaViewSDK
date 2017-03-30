--
-- Copyright 2017 Alibaba Group
-- License: MIT
-- Website: https://alibaba.github.io/LuaViewSDK
-- User: tuoli
-- Date: 17/3/30
--

function main(args)
    if (args ~= nil) then
        require('kit.launcher'):run(args)
    else
        require('kit.launcher'):run('App')
    end
end

main(Bridge:args())
