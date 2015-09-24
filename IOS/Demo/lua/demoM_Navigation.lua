
scrW, scrH = System.screenSize();

function createButton( text , x,y,w,h, callback) {
	local button = UIButton();
	button.setFrame(x,y,w,h);
	button.setText(text);
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
			titleButton = UIButton()
			titleButton.setText("测试view")
			Navigation.setTitle(titleButton)
	});

button2 = createButton("设置导航栏 左菜单",0,buttonH*2.5,scrW,buttonH, ^(){
			-- 	设置导航栏 左菜单
			leftButton = UIButton()
			leftButton.setText("左")
			leftButton.sizeToFit();
			Navigation.setLeftBarButton (leftButton)
	});

button3 = createButton("设置导航栏 右菜单",0,buttonH*4,scrW,buttonH, ^(){
			-- 设置导航栏 标题
			rightButton = UIButton()
			rightButton.setText("右")
			rightButton.sizeToFit();
			Navigation.setRightBarButton(rightButton)
	});


button4 = createButton("设置导航栏 左菜单2",0,buttonH*5.5,scrW,buttonH, ^(){
			-- 	设置导航栏 左菜单
			leftButton = UIButton()
			leftButton.setText("左1")
			leftButton.sizeToFit();
			leftButton2 = UIButton()
			leftButton2.setText("左2")
			leftButton2.sizeToFit();
			Navigation.setLeftBarButton (leftButton, leftButton2)
	});

button5 = createButton("设置导航栏 右菜单2",0,buttonH*7,scrW,buttonH, ^(){
			-- 设置导航栏 标题
			rightButton = UIButton()
			rightButton.setText("右1")
			rightButton.sizeToFit();
			rightButton2 = UIButton()
			rightButton2.setText("右2")
			rightButton2.sizeToFit();
			Navigation.setRightBarButton(rightButton,rightButton2)
	});

