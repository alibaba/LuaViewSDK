
scrW, scrH = System.screenSize();

function createButton( text , x,y,w,h, callback) {
	local button = Button();
	button.frame(x,y,w,h);
	button.text(text);
	button.backgroundColor(0xeeeeee);
	button.delegate = ^(){
		print("ok",button);
		if( callback ) {
			callback();
		}
	};
	return button;
}

buttonH = scrH /10;

-----------------

button1 = createButton("设置导航栏 标题",0,buttonH*1,scrW,buttonH, ^(){
			-- 设置导航栏 标题
			titleButton = Button()
			titleButton.text("测试view")
			Navigation.title(titleButton)
	});

button2 = createButton("设置导航栏 左菜单",0,buttonH*2.5,scrW,buttonH, ^(){
			-- 	设置导航栏 左菜单
			leftButton = Button()
			leftButton.text("左")
			leftButton.sizeToFit();
			Navigation.leftBarButton (leftButton)
	});

button3 = createButton("设置导航栏 右菜单",0,buttonH*4,scrW,buttonH, ^(){
			-- 设置导航栏 标题
			rightButton = Button()
			rightButton.text("右")
			rightButton.sizeToFit();
			Navigation.rightBarButton(rightButton)
	});


button4 = createButton("设置导航栏 左菜单2",0,buttonH*5.5,scrW,buttonH, ^(){
			-- 	设置导航栏 左菜单
			leftButton = Button()
			leftButton.text("左1")
			leftButton.sizeToFit();
			leftButton2 = Button()
			leftButton2.text("左2")
			leftButton2.sizeToFit();
			Navigation.leftBarButton (leftButton, leftButton2)
	});

button5 = createButton("设置导航栏 右菜单2",0,buttonH*7,scrW,buttonH, ^(){
			-- 设置导航栏 标题
			rightButton = Button()
			rightButton.text("右1")
			rightButton.sizeToFit();
			rightButton2 = Button()
			rightButton2.text("右2")
			rightButton2.sizeToFit();
			Navigation.rightBarButton(rightButton,rightButton2)
	});

