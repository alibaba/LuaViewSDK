-- 测试json字符串转成lua对象
object = Json:toTable( '{"a":false, "b":true, "c":{}, "d":0, "f":1 }' );
print(object);

print( Json:toString(object) )

-- 测试Lua对象转成native
dic = viewController:testJson(object);
print(dic)