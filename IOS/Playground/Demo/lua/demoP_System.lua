scrW,scrH = System:screenSize();
label = Label();
label:frame(0,0,scrW,scrH-64);
label:lineCount(100);

function myprint( s , s2 )
	s = s .. tostring(s2)
	print(s);
	local text = label:text();
	if( text ) then 
		label:text ( text .. "\n" .. s);
	else
		label:text( s);
	end
end




myprint("System.ios = " , System:ios() )
myprint("System.android = ", System:android() )
myprint("System.vmVersion = ", System:vmVersion() )
myprint("System.osVersion = ", System:osVersion() )	--获取手机操作系统版本号
myprint("System.platform = ", System:platform() )	--获取平台信息
myprint("System.scale = ", System:scale() )	--获取屏幕分辨率
myprint("System.device = ", System:device() )	--获取设备信息
myprint("System:screenSize() = ", System:screenSize() );
myprint("---end---",nil);

window:callback({
        ViewDidAppear = function ()
            print("ViewDidAppear");
            System:keepScreenOn(true);-- 屏幕常亮
        end,
        ViewDidDisAppear = function ()
            print("ViewDidDisAppear");
            System:keepScreenOn(false);-- 屏幕不常亮
        end,
        onShow = function ()
            print("onShow");
            System:keepScreenOn(true);-- 屏幕常亮
        end,
        onHide = function ()
            print("onHide");
            System:keepScreenOn(false);-- 屏幕不常亮
        end})
t = {1,2,3,a = 1 , b = 2}



print(  math.abs(-1) )




local t = os.date( "*t" );
t.hour = 10;
t.min = 0;
t.sec = 0;
local ostime = os.time(t);
print(ostime)




