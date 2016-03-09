--- Date
-- 日期
-- @classmod Date


--- 创建日期对象 当前时间
-- @treturn Date date 日期
function Date()
end

--- 创建日期对象(参数距1970的秒数值)
-- @number timeIntervalSince1970 秒数
-- @treturn Date date 日期
function Date()
end

--- 创建日期对象(字符串)
-- @string s 日期字符串
-- @string format 格式 (可省缺 默认值yyyyMMddHHmmss)
-- @treturn Date date 日期
function Date()
end

--- 开始播放
function timeInterval()
end

--- 时间相减
-- @tparam Date date 日期
-- @treturn number 相差的秒数
-- @usage
-- local date1 = Date(20150801093000);
-- local date2 = Date();
-- print(date1 - date2);
function __sub()
end


--- 时间是否相同
-- @tparam Date date 日期
-- @treturn bool 是否相同
-- @usage
-- local date1 = Date(201508271013);
-- local date2 = Date();
-- if ( date1 == date2 ) then
--     --code
-- end
function __eq()
end
