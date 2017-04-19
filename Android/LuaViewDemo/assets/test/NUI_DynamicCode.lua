-- Created by LuaView.
-- Copyright (c) 2017, Alibaba Group. All rights reserved.
--
-- This source code is licensed under the MIT.
-- For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.


---------------------------------------------------------------------------
print("case1:")

code = [[
    param = "Hello world!!!"
    print(param)
]]


hi = load(code)
hi()

---------------------------------------------------------------------------

print("case2:")

hi2 = load("param = param .. 'ha ha ha '; x = 1; x = x+1; print(param, x)")
hi2()

hi3 = load("x=x+1; print(param, x)")
hi3()

hi4 = load("fun = function() print('this is a function') end")
hi4()
fun()

fun2 = nil
hi5 = load("fun2 = function(p) print('this is a function', p) end")
hi5()
fun2("hahaha")



---------------------------------------------------------------------------
print("case3:")

x = "hello abc"

dump = string:dump(function() print(x) end)

hello = load(dump)
hello()

---------------------------------------------------------------------------
print("case4:")

audioplayer = loadfile("NUI_AudioPlayer.lua")
audioplayer()

