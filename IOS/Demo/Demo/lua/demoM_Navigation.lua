
scrW, scrH = System:screenSize();

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
	end );
	return button;
end

buttonH = scrH /10;

-----------------

button1 = createButton("设置导航栏 标题",0,buttonH*1,scrW,buttonH, function()
			-- 设置导航栏 标题
			titleButton = Button()
			titleButton:text("测试view")
			Navigation:title(titleButton)
	end);

button2 = createButton("设置导航栏 左菜单",0,buttonH*2.5,scrW,buttonH, function()
			-- 	设置导航栏 左菜单
			leftButton = Button()
			leftButton:text("左")
			leftButton:adjustSize();
			Navigation:left(leftButton)
			leftButton:callback(function ()
				viewController:gotoHistory();
			end)
	end);

button3 = createButton("设置导航栏 右菜单",0,buttonH*4,scrW,buttonH, function()
			-- 设置导航栏 标题
			rightButton = Button()
			rightButton:text("右")
			rightButton:adjustSize();
			Navigation:right(rightButton)
	end);


button4 = createButton("设置导航栏 左菜单2",0,buttonH*5.5,scrW,buttonH, function()
			-- 	设置导航栏 左菜单
			leftButton = Button()
			leftButton:text("左1")
			leftButton:adjustSize();
			leftButton2 = Button()
			leftButton2:text("左2")
			leftButton2:adjustSize();
			Navigation:left(leftButton, leftButton2)
			leftButton:callback(function ()
				viewController:gotoHistory();
			end)
			leftButton2:callback(function ()
				viewController:gotoHistory();
			end)
	end);

button5 = createButton("设置导航栏 右菜单2",0,buttonH*7,scrW,buttonH, function()
			-- 设置导航栏 标题
			rightButton = Button()
			rightButton:text("右1")
			rightButton:adjustSize();
			rightButton2 = Button()
			rightButton2:text("右2")
			rightButton2:adjustSize();
			Navigation:right(rightButton,rightButton2)
	end);

img = Image();
img:image("http://gtms02.alicdn.com/tps/i2/TB1qmXnHpXXXXcuaXXXQG.m0FXX-640-128.jpg",function()
	Navigation:background(img,0xff0000)
end);




