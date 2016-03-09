----
-- 字符串处理库
-- @classmod string


--- 字符串第几个到第几个 字符值
-- @int i 起始位置 (可选参数)
-- @int j 终点位置 (可选参数)
-- @usage
-- local t = "123456789";
-- print( t.byte(2,4) ) -- 输出: 50	51	52
--
function byte()
end

--- 字符拼接
-- @usage
-- print( string:char(51,52,53) ) -- 输出: 345
--
function string:char()
end

--- 函数 string:dump 返回一个函数二进制形式的字符串
-- 函数 string:dump 实现了函数的序列化，函数可以很轻松的传递，
-- 并在其他作用域调用。函数 string:dump 出来的二进制字符串，可以用 load 函数反序列回来直接调用。
-- @tparam function function 函数
-- @usage 
-- function test()
--     print("just a test")
-- end
-- print(string:dump(test))
--
function string:dump()
end

--- 查找字符串 s 里第一个符合查找字符 pattern 的位置
-- 如果找到了目标字符 pattern，则返回它的开始和结束位置
-- @string pattern  查找字符串
-- @treturn start
-- @treturn end
-- @usage 
-- local s = "just a test";
-- startPos, endPos = s.find("st")
-- print(startPos, endPos)
--
function find()
end

--- 用于把字符串格式化输出
-- 函数的第一个参数是用来指定字符串的格式
-- @usage
-- print(string:format("%s is %d", "ten", 10))
-- @usage
-- .           所有字符
-- %a          字母
-- %c          控制符
-- %d          数字
-- %D          非数字
-- %l          小写字母
-- %p          标点符号
-- %s          字符串
-- %u          大写字母
-- %w          字母数字
-- %x          十六进制数
-- %z          用 0 表示的字符
function string:format()
end

--- 会返回一个迭代函数，可通过该函数遍历到一个字符串 s 中所有出现指定匹配模式 pattern 的地方
--@usage
-- HELP_MESSAGE = [[
-- This is the first line of help to show.
-- And here is the second.
-- And here is the third.
-- Let's make sure to include enough text to bore the user.
-- ]];
-- for line in HELP_MESSAGE.gfind('[^\n]+') do
--    print(line);
-- end
--
function gfind()
end

--- gmatch 会返回一个迭代函数，可通过该函数遍历到一个字符串 s 中所有出现指定匹配模式 pattern 的地方
-- @usage
-- s = "just a test"
-- for w in s.gmatch("%a+") do
-- 	   print(w)
-- end
--
function gmatch()
end

--- 用于全局字符串替换，字符串 s 里满足匹配模式 pattern 格式的字符都会被替换成 repl 参数的值
--@string pattern 匹配模式
--@string repl 替换结果
--@int n 替换次数 (可选参数)
--@usage
-- local s = "just a test";
-- print( s.gsub("st", "*"))
-- -- 输出: ju* a te*	2
--
--@usage
-- local s = "num is 1234567890"
-- print( s.gsub("%d", "*"))
-- --  输出: num is **********	10
--
--@usage
-- s = "sethook, setlocal, setmetatable, setupvalue, setuservalue"
-- print( s.gsub("s%a+", "S", 2) )
-- --  输出: S, S, setmetatable, setupvalue, setuservalue	2
--
function gsub()
end

--- 用于返回字符串 s 的长度
-- @usage 
-- local s = "123";
-- print( s.len() )
--
function len()
end

--- 用于把字符串 s 里的字母转为小写
-- @usage 
-- local s = "ABC";
-- print( s.lower() )
--
function lower()
end

--- 用于查找字符串 s 里第一个匹配对模式 pattern 的值，并返回匹配值
-- @string pattern 匹配模式(可以是正则表达式)
-- @usage
-- local s = "just a test"
-- print(s.match("test"))
-- @usage
-- s = "today is 2003-5-31"
-- print(s.match("%d+-%d+-%d+"))
-- @usage
-- s = "abcdabcd"
-- print(s.match("a"))
--
function match()
end

--- 生成一个由分隔符 sep 隔开的重复（repeat）n 次字符 s 的字符串
-- @int n重复次数
-- @string sep
-- @usage 
-- print(string:rep("你好", 3))
-- --输出: 你好你好你好
--
function rep()
end

--- 用于倒转一个字符串 s 的排序
-- @usage
-- local s = "reverse";
-- print(s.reverse())
-- -- 输出: esrever
--
function reverse()
end

--- 用于从字符串 s 里截取一个从第 i 个字符到第 j 个字符间的子字符串
-- @int i起始字符index
-- @int j结束字符index
-- @treturn string 子字符串
-- @usage 
-- local s = "abcdefg";
-- print(s.sub(2, 4))
--
function sub(  )
end

--- 用于把字符串 s 里的字母转为大写
function upper()
end

