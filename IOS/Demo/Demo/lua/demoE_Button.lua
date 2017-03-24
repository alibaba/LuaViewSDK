
function createButton( text , x,y,w,h, callback)
	local button = Button();
	button:frame(x,y,w,h);
	button:text(text);
	button:backgroundColor(0xeeeeee);
	button:callback( function()
		print("ok",button);
		if( callback ) then
			callback();
		end
	end);
	return button;
end

button1 = createButton("测试",10,10,300,60);



button2 = Button();
button2:frame(10,150,100,100);
button2:image("http://g.alicdn.com/ju/lua/2.0.25/doc/icon.png","http://g.alicdn.com/ju/lua/2.0.25/doc/icon2.png");


button2:callback(function()
	print("我是图片按钮1");
end);


button3 = Button();
button3:frame(10,250,100,100);
button3:image("button0.png","button1.png");


button3:callback(function()
	print("我是图片按钮2");
end);
