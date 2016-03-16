--- Toast
-- 弹出提示框
-- @classmod Toast


--- 弹出一个提示框(3秒自动消失的)
-- @usage     
-- local callback = function(location)
--     if location == nil then
--         Toast("获取地理位置错误")
--         return
--     end
--     local str = "地理位置\n"
--     for k, v in pairs(location) do
--         str = str .. k .. ": " .. tostring(v) .. "\n"
--     end
--     Toast(str)
--     --[[
--     星号表示字符串类型，其他为number
--     经纬度 longitude latitude
--     水平精度 accuracy
--     城市编码和名字 cityCode* cityName*
--     --]]
-- end
-- bridge.location(callback)
---
function Toast()
end




