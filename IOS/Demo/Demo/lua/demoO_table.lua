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



local t = {"alpha", "beta", "gamma"}
myprint(table.concat(t,":"))

myprint(table.concat(t, nil, 1, 2))

myprint(table.concat(t, "\n", 2, 3))

myprint(#t);