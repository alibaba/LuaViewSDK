-- Created by LuaView.
-- Copyright (c) 2017, Alibaba Group. All rights reserved.
--
-- This source code is licensed under the MIT.
-- For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.

require("kit/lv_kit")


local label = Label()
label.text("请看日志输出")
label.frame(0, 0, System.screenSize())


data = Data(97, "abc", "def")
print("1", data)


data1 = Data("a")
data2 = Data("b")

print("2", data1 + data2)


print("3", Data('a').toString("latin-1"))

printTable("4", Data('{"a":"1"}').toTable())

print("5", Data('{"a":"1"}').toJson())

-- not json
print("6", Data('a').toJson())

print("7", Data('{"a":"1"}'))


print("-----Json------")

printTable("8", Json.toTable('{"a":"1"}'))

printTable("9", Json.toTable(Data('{"a":"1"}')))

printTable("10", Json.toTable("a"))

printTable("11", Json.toTable())


print("-----isValid------")

print("12", Json.isValid('{"a":"1"}'))

print("13", Json.isValid(Data('{"a":"1"}')))

print("14", Json.isValid("a"))

print("15", Json.isValid())

print("-----nil------")
print("16")
local table = Json.toTable('{}')
printTable(table)

print("17")
table = Json.toTable('')
printTable(table)

print("18")
printTable(Json.toTable({
    a = '1'
}))

print("-----async------")
Json.toTable('{"a":"1"}', function(result)
    print("19")
    printTable(result)
end)

data = '{"a"=1,"b"="3","c"={"c1"=1}}'
printTable(Json.toTable(data))

Json.toTable(data, function(result)
    printTable(result)
end)



