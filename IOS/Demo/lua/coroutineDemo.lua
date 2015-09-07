

--[ local variable definition --]
--[ repeat loop execution --]
local a = 1
repeat
    print("value of a:", a)
    a = a + 1
until( a > 5 )


print("coroutine begin");

function foo(a)
    print("foo", a)
    return coroutine:yield(2 * a)
end

co = coroutine:create(function ( a, b )
    print("co-body1", a, b)
    local r = foo(a + 1)
    print("co-body2", r)
    local r, s = coroutine:yield(a + b, a - b)
    print("co-body3", r, s)
    return b, "end"
end)

function demo(  )
    print("测试1");
    print("test2");
    print("test3");
end

demo();
print("main1", coroutine:resume(co, 1, 10))
print("main2", coroutine:resume(co, "r"))
print("main3", coroutine:resume(co, "x", "y"))
print("main4", coroutine:resume(co, "x", "y"))
print("main4", coroutine:resume(co, "x", "y"))

