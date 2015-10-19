
function createButton( text , x,y,w,h, callback) {
	local button = Button();
	button.frame(x,y,w,h);
	button.text(text);
	button.backgroundColor(0xeeeeee);
	button.click( function()
		print("ok",button);
		if( callback ) then
			callback();
		end
	end);
	return button;
}

button1 = createButton("测试",10,10,300,60);



button2 = Button();
button2.frame(10,100+80,100,100);
button2.image("http://g.alicdn.com/ju/lua/2.0.25/doc/icon.png","http://g.alicdn.com/ju/lua/2.0.25/doc/icon2.png");


button2.click(function()
	print("我是图片按钮");
    button2.sizeToFit();
end);


	-- print("clear");
	-- System.gc();
	-- collectgarbage("collect");
	-- local memory = collectgarbage("count");
	-- print("memory: ", memory );