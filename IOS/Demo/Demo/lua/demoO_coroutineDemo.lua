scrW,scrH = System:screenSize();
label = Label();
label:frame(0,0,scrW,scrH-64);
label:lineCount(100);

function myprint( s , s2 )
	s = s .. tostring(s2)
	print(s);
	local text = label:text();
	if( text ) then 
		label:text( text .. "\n" .. s);
	else
		label:text( s);
	end
end


--[ local variable definition --]
--[ repeat loop execution --]
local a = 1
repeat
    myprint("value of a:", a)
    a = a + 1
until( a > 5 )


myprint("coroutine begin");

function foo(a)
    myprint("foo", a)
    return coroutine.yield(2 * a)
end

co = coroutine.create(function ( a, b )
    myprint("co-body1", a, b)
    local r = foo(a + 1)
    myprint("co-body2", r)
    local r, s = coroutine.yield(a + b, a - b)
    myprint("co-body3", r, s)
    return b, "end"
end)

function demo(  )
    myprint("测试1");
    myprint("test2");
    myprint("test3");
end

demo();
myprint("main1", coroutine.resume(co, 1, 10))
myprint("main2", coroutine.resume(co, "r"))
myprint("main3", coroutine.resume(co, "x", "y"))
myprint("main4", coroutine.resume(co, "x", "y"))
myprint("main4", coroutine.resume(co, "x", "y"))

