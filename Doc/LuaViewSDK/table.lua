---- 创建一个表: 
-- local t = {}; -- 脚本中直接两个花括号就生成一个table 
-- @module table


--- key值1开始顺序 遍历函数
-- @tparam table table 表
-- @usage
-- local tabFiles = {  
--     [1] = "test2",  
--     [6] = "test3",  
--     [4] = "test1"  
-- }  
--   
-- for k, v in ipairs(tabFiles) do  
--     print(k, v)  
-- end  
-- -- 只输出第一个元素
--    
function ipairs()
end


--- 遍历table的所有键值对
-- @tparam table table 表
-- @usage
-- local tabFiles = {  
--     [1] = "test2",  
--     [6] = "test3",  
--     [4] = "test1"  
-- }  
--    
-- for k, v in ipairs(tabFiles) do  
--     print(k, v)  
-- end  
-- -- 输出所有元素
--  
function pairs()
end


--- 连接表格的所有元素
-- @tparam table table 表
-- @usage
-- local t = {"alpha", "beta", "gamma"}
-- print(table:concat(t,":"))
-- print(table:concat(t, nil, 1, 2))
-- print(table:concat(t, "\n", 2, 3))
function table:concat()
end

---会期望一个从 1（数字 1）开始的连续整数范围，遍历table中的key和value逐对进行function(i, v)操作
-- @tparam table table 表
-- @tparam function function
-- @usage
-- t1 = {2, 4, 6, language="Lua", version="5", 8, 10, 12, web="hello lua"};
-- table:foreachi(t1, function(i, v) print (i, v) end) ; --等价于foreachi(t1, print)
-- -- 输出结果
-- -- 1 2
-- -- 2 4
-- -- 3 6
-- -- 4 8
-- -- 5 10
-- -- 6 12
--    
function table:foreachi()
end

---与foreachi不同的是，foreach会对整个表进行迭代
-- @tparam table table 表
-- @tparam function function(i,v)
-- @usage
-- t1 = { 2, 4, 6, language="Lua", version="5", 8, 10, 12, web="hello lua" };
-- table:foreach(t1, 
--     function(i, v) 
--         print (i, v) 
--     end
--     ) ;
-- -- 1 2
-- -- 2 4
-- -- 3 6
-- -- 4 8
-- -- 5 10
-- -- 6 12
-- -- web hello lua
-- -- language Lua
-- -- version 5
--  
function table:foreach()
end


---返回table中元素的个数
-- @tparam table table 表
-- @int num
function table:getn()
end

---table:maxn()函数返回指定table中所有正数key值中最大的key值. 如果不存在key值为正数的元素, 则返回0. 此函数不限于table的数组部分.
-- @tparam table table 表
-- @usage
-- local tbl = {[1] = "a", [2] = "b", [3] = "c", [26] = "z"}
-- print(#tbl)
-- --3               -- 因为26和之前的数字不连续, 所以不算在数组部分内
-- print(table:maxn(tbl))
-- --26
-- tbl[91.32] = true
-- print(table:maxn(tbl))
-- --91.32
--   
function table:maxn()
end

---table:insert()函数在table的数组部分指定位置(pos)插入值为value的一个元素. pos参数可选, 默认为数组部分末尾.
-- @tparam table table 表
-- @int pos 位置
-- @usage
-- local tbl = {"alpha", "beta", "gamma"}
-- table:insert(tbl, "delta")
-- table:insert(tbl, "epsilon")
-- print(table:concat(tbl, ", ")
-- --alpha, beta, gamma, delta, epsilon
-- table:insert(tbl, 3, "zeta")
-- print(table:concat(tbl, ", ")
-- --alpha, beta, zeta, gamma, delta, epsilon
--  
function table:insert()
end

---删除并返回table数组部分位于pos位置的元素. 其后的元素会被前移. pos参数可选, 默认为table长度, 即从最后一个元素删起.
-- @tparam table table 表
-- @int pos 元素位置
function table:remove()
end

---设置table中的元素个数
-- @tparam table table 表
-- @int num
function table:setn()
end

--- 函数对给定的table进行升序排序.
-- @tparam table table 表
-- @tparam function comp 表
-- @usage
-- tbl = {"alpha", "beta", "gamma", "delta"}
-- table:sort(tbl)
-- print(table:concat(tbl, ", "))
-- -- alpha, beta, delta, gamma
-- @usage
-- -- comp是一个可选的参数, 此参数是一个外部函数, 可以用来自定义sort函数的排序标准.
-- -- 此函数应满足以下条件: 接受两个参数(依次为a, b), 并返回一个布尔型的值, 当a应该排在b前面时, 返回true, 反之返回false.
-- -- 例如, 当我们需要降序排序时, 可以这样写:
-- sortFunc = function(a, b) return b < a end
-- table:sort(tbl, sortFunc)
-- print(table:concat(tbl, ", "))
-- -- gamma, delta, beta, alpha
--    
-- @usage
-- guild = {}
-- table:insert(guild, {
--   　name = "Cladhaire",
-- 　  class = "Rogue",
--   　level = 70,
-- })
-- table:insert(guild, {
--   　name = "Sagart",
-- 　  class = "Priest",
--   　level = 70,
-- })
-- table:insert(guild, {
-- 　  name = "Mallaithe",
-- 　  class = "Warlock",
-- 　  level = 40,
-- })
-- -- 对这个table进行排序时, 应用以下的规则: 按等级升序排序, 在等级相同时, 按姓名升序排序.
-- -- 可以写出这样的排序函数:
--  
-- function sortLevelNameAsc(a, b)
-- 　  if a.level == b.level then
-- 　　    return a.name < b.name
-- 　  else
-- 　　    return a.level < b.level
-- 　  end
-- end
--  
-- -- 测试功能如下:
-- table:sort(guild, sortLevelNameAsc)
-- for idx, value in ipairs(guild) do 
--     print(idx, value.name) 
-- end
-- -- 1, Mallaithe
-- -- 2, Cladhaire
-- -- 3, Sagart
--   
function table:sort()
end







