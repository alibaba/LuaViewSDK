--
-- Copyright 2017 Alibaba Group
-- License: MIT
-- Website: https://alibaba.github.io/LuaViewSDK
-- User: tuoli
-- Date: 17/4/6
--

local meta = object:new()

function meta:cancel()
    self.isCancel = true
end

function meta:request(url, callback)
    print("tuoli", "http request start")
    self.isCancel = false
    Http():get(url, function(response)
        print("tuoli", "http request end")
        if (callback ~= nil and self.isCancel == false) then
            callback(response)
        end
    end)
end

return meta

