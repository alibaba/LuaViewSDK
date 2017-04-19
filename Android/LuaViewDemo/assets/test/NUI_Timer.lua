-- Created by LuaView.
-- Copyright (c) 2017, Alibaba Group. All rights reserved.
--
-- This source code is licensed under the MIT.
-- For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.

scrW,scrH = System.screenSize();
label = Label();
label.frame(0,0,scrW,scrH-64);
label.lineCount(100);

function myprint( s , s2 )
	s = s .. " " .. tostring(s2)
	print(s);
	local text = label.text();
	if( text ) then 
		label.text( text .. "\n" .. s);
	else
		label.text( s);
	end
end

---- 1
--timer1 = Timer(function()
--    myprint("Timer1 Run")
--end);
--timer1 = timer1.start(0.1, false);

--2
index2 = 1;
timer2 = Timer()
timer2.callback(
	function() 
		myprint("Timer2 Run",index2) 
		index2 = index2 + 1;

		if(index2 > 10) then
			timer2.cancel()
		end
	end
	)
timer2.start(0.2, true)
