----
-- 数学库math
-- @module math

--- 绝对值
-- @usage local t = -3;
-- t = math:abs( t );
function math:abs()
end

--- 反余弦函数
function math:acos()
end

--- 反正弦函数
function math:asin()
end

--- 反正切值(y,x)
--@number y
--@number x
function math:atan2()
end

--- 反正切值(x,y)
--@number x
--@number y
function math:atan()
end

--- 返回大于或者等于指定表达式的最小整数
--@number number
function math:ceil()
end

--- 双曲余弦值
-- @number number
--@number number
function math:cosh()
end

--- 余弦
--@number number
function math:cos()
end

--- 弧度 转 角度
--@number number
function math:deg()
end

--- 角度 转 弧度
--@number number
function math:rad()
end

--- 高等数学里以自然常数e为底的指数函数，它又是航模名词，全称Exponential(指数曲线)
--@number number
function math:exp()
end

--- 返回比参数小的最大整数
--@number number
function math:floor()
end

--- 计算x对y的模，即x/y的求余运算 （参见百科词条mod中的“求余函数” 条目），若y是0，则返回NaN。
-- @number x
-- @number y
function math:fmod()
end

--- 把一个浮点数分解为尾数和指数
-- 其中 x = 尾数 * 2^指数
-- @number x
function math:frexp()
end

--- 计算value乘以2的exp次幂 （ value * ( 2^exp ) ）
-- @number value
-- @number exp
-- @treturn number 计算结果
function math:ldexp()
end

--- 以10为底的对数函数
-- @number x
function math:log10()
end

--- 计算以e为底的对数值
-- @number x
-- @treturn 以e为底的对数值
function math:log()
end

--- 最大值(支持多个参数)
-- @treturn number 返回最大值
function math:max()
end

--- 最小值(支持多个参数)
-- @treturn number 返回最小值
function math:min()
end

--- 分解x，以得到x的整数和小数部分
-- @number x 数值
function math:modf()
end

--- 计算x的y次幂
-- @number x
-- @number y
function math:pow()
end

--- 获取随机数
--@number min 最小值(可选)
--@number max 最大值(可选)
--@treturn number 随机数
function math:random()
end

--- 设置随机发生器的种子
function math:randomseed()
end

--- 双曲正弦函数
function math:sinh()
end

--- 正弦函数
function math:sin()
end

--- 开平方根
function math:sqrt()
end

--- 双曲正切
function math:tanh()
end

--- 正切函数
function math:tan()
end
