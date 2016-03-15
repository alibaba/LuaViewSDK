
scrW, scrH = System.screenSize();

function createButton( text , x,y,w,h, callback) 
	local button = Button();
	button.frame(x,y,w,h);
	button.text(text);
	button.backgroundColor(0xeeeeee);
	button.callback( function()
		print("ok",button);
		if( callback ) then
			callback();
		end
	end );
	return button;
end

buttonH = scrH /10;

-----------------

button1 = createButton("设置导航栏 标题",0,buttonH*1,scrW,buttonH, function()
			Navigation.title("测试view")
	end);


img = Image();
img.image("http://gtms02.alicdn.com/tps/i2/TB1qmXnHpXXXXcuaXXXQG.m0FXX-640-128.jpg",function()
	Navigation.background(img, 0xff0000)
end);




