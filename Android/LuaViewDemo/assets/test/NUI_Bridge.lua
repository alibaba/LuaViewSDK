-- Created by LuaView.
-- Copyright (c) 2017, Alibaba Group. All rights reserved.
--
-- This source code is licensed under the MIT.
-- For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.

local label = Label()
label.text("请看日志输出")
label.frame(0, 0, System.screenSize())


bridge.testParams({"a", "b"})

bridge.testParams({aa="a", bb="b"})

-- 从Native注册一个bridge对象给lua，并在lua中调用native函数
print("Map", type(bridge.testMap()), bridge.testMap(), bridge.testMap().get("key1"))
print("List", type(bridge.testList()), bridge.testList(), bridge.testList().get(1))
print("String", type(bridge.testString()), bridge.testString())
print("int", type(bridge.testInt()), bridge.testInt())
print("Int2", type(bridge.testInt2()), bridge.testInt2())
print("long", type(bridge.testLong()), bridge.testLong())
print("Long2", type(bridge.testLong2()), bridge.testLong2())
print("double", type(bridge.testDouble()), bridge.testDouble())
print("Double2", type(bridge.testDouble2()), bridge.testDouble2())
print("boolean", type(bridge.testBoolean()), bridge.testBoolean())
print("Boolean", type(bridge.testBoolean2()), bridge.testBoolean2())

