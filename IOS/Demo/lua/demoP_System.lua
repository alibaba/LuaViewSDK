scrW,scrH = System.screenSize();
label = Label();
label.frame(0,0,scrW,scrH-64);
label.lineCount(100);

function myprint( s , s2 )
	s = s .. tostring(s2)
	print(s);
	local text = label.text();
	if( text ) then 
		label.text =  text .. "\n" .. s;
	else
		label.text =  s;
	end
end




myprint("System.ios = " , System.ios )
myprint("System.android = ", System.android )
myprint("System.vmVersion = ", System.vmVersion)
myprint("System.osVersion = ", System.osVersion)	--获取手机操作系统版本号
myprint("System.platform = ", System.platform )	--获取平台信息
myprint("System.scale = ", System.scale )	--获取屏幕分辨率
myprint("System.device = ", System.device )	--获取设备信息
myprint("System.screenSize() = ", System.screenSize() );
myprint("---end---",nil);
