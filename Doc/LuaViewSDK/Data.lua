--- Data(数据块)
-- 创建byte数组
-- @classmod Data


--- 创建byte数组
-- @treturn Data data 数组
function Data()
end

--- 创建byte数组
-- @number len 数组长度
-- @treturn Data data 数组
function Data()
end

--- 创建byte数组
-- @string s 字符串
-- @treturn Data data 数组
function Data()
end

--- 读取值
-- @int index 索引号
-- @usage 
-- local data = Data("123456");
-- print(data[2]);
function __index()
end

--- 赋值
-- @int index 索引号
-- @usage 
-- local data = Data("123456");
-- data[2] = 50;
-- print(data[2]);
function __newindex()
end

--- 两个数组合并成一个数组
-- @tparam Data data 数组
-- @treturn bool 是否相同
-- @usage
-- local data1 = Data('201508271013' );
-- local data2 = Data('测试');
-- print(data1 + data2);
function __add()
end


--- byte数组按UTF8编码转换成字符串
-- @treturn string string 字符串
function toString()
end