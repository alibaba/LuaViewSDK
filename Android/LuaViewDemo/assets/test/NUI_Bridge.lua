-- 简单动画
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

-- 从Native注册一个bridge对象给lua，并在lua中调用native函数

local dict = {
    a = "a",
    b = "b"
}
bridge.luaUT2101("key", dict)