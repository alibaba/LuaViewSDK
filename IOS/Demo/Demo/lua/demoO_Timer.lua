scrW,scrH = System:screenSize();
label = Label();
label:frame(0,0,scrW,scrH-64);
label:lineCount(100);

function myprint( s , s2 )
	s = s .. " " .. tostring(s2)
	print(s);
	local text = label:text();
	if( text ) then 
		label:text( text .. "\n" .. s);
	else
		label:text( s);
	end
end



-- 1
timer1 = Timer(function()
    myprint("Timer1 Run")
end);
timer1 = timer1:start(1.0, false);

--2
index2 = 1;
timer2 = Timer()
timer2:callback(
	function() 
		myprint("Timer2 Run",index2) 
		index2 = index2 + 1;
	end
	)
timer2:start(2, true)

--3
timer3 = Timer(function()
    timer2:cancel()
	myprint("Timer3 Run") 
end)
timer3:start(10)



--4
timer4 = Timer()
timer4:delay(5);
timer4:interval(1);
timer4:callback(
    function()
    	myprint("Timer4 Run")
    	timer4 = nil;
    	System:gc();
    end
)
timer4:start()

