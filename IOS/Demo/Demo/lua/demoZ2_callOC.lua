
viewController:externalApiDemo_number("dongxicheng",123);
viewController:externalApiDemo_number("dongxicheng",123);
if( viewController.externalApiDemo ) then
	viewController:externalApiDemo();
else
	print("viewController.externalApiDemo not exist !!!");
end


function testNativeObjectArgs(view, vc, str, intvalue)
    print(str,intvalue);
	vc:externalApiDemo_number("测试",8787);
	local s = vc:string("可以嘛");
	print(s .. "结果");
	s = vc:string_block("整的",function(a,b)
			return (a .. "中文" .. b ), "小学";
		end)
	print("结果2: " .. s );
end