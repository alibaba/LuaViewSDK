--
-- Copyright 2017 Alibaba Group
-- License: MIT
-- Website: https://alibaba.github.io/LuaViewSDK
-- User: tuoli
-- Date: 17/3/30
--

object = {}

function object:new(o)
    o = o or {}
    setmetatable(o, self)
    self.__index = self
    return o
end

