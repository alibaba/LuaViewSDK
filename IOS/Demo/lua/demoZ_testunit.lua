imageUrl = "http://ico.ooopic.com/ajax/iconpng/?id=96776.png";

w,h = System.screenSize();--获取屏幕尺寸

demoLabel = Label();
demoLabel.frame(0,20,w-20,40);
demoLabel.text("点击屏幕一次执行一个测试demo");
demoLabel.backgroundColor(0xddeeee);

label = Label();-- 创建一个文本框
label.frame(160,h-80,w,80);



detailInfo = Label();
detailInfo.frame(0, h-40, w, 40);

function detail( info )
    detailInfo.text(info);
    detailInfo.sizeToFit();
end
--------------------------
demoArray = {
^(){
	demoLabel.x = 2;
},^(){
	demoLabel.y = 2;
},^(){
	demoLabel.right = w-2;
},^(){
	demoLabel.left = 2;
},^(){
	demoLabel.bottom = 2;
},^(){
	demoLabel.top = 2;
},^(){
	demoLabel.width = 200;
},^(){
	demoLabel.height = 80;
},^(){
	demoLabel.x = 10;
},
^(){
	Http:get("http://www.baidu.com", --http url
		^(request){-- http call back
			print(request );
			print(request.responseStatusCode() );--http请求是否成功 返回码

			local heads =  request.responseHeaderFields();
			for key,value in pairs(heads) do
				print(key .. " : " .. value)
			end
		}
	);	
},

^(){
	Http:post(
	"https://login-test.alibaba-inc.com/authorize/login.do",--url
	nil,--http头信息
	{--http post数据
		appcode="123456",
		name="taobao-hz\xicheng.dxc",
		password="dxc123",
		authtype="system"
	},
		^(request){--http 返回的数据
		  print(request.data() );
		}
	);	
},
^(){
	imageView = Image();
	imageView.image(imageUrl);
	imageView.frame(200,0,64,64);
    detail("Image");
    imageView.center(w/2,h/2);
},
^(){
	local g = PinchGesture(^(gesture){
			imageView.transformRoteAndScale( 0, gesture.scale());
		});
	window.addGesture(g);
},
^(){
	local g = RotationGesture(^(gesture){
			imageView.transformRoteAndScale( gesture.rotation(), 1);
		});
	window.addGesture(g);
},
^(){
	local g = SwipeGesture(^(gesture){
			if( gesture.state()==GestureState.END ) then
				print( "两个手势向左滑动" );
			end
		});

	g.touchCount(2);
	g.direction( GestureDirection.LEFT);
	window.addGesture(g);
},
^(){
	local g = LongPressGesture (^(gesture){
			if( gesture.state()== GestureState.END )  then
				print( "长按手势检测到了" );
			end
		});
	g.touchCount(2);
	window.addGesture(g);
},
^(){
	Vibrate();
},
^(){
	temp = LoadJson("{test=\"haha\"}");
	print(temp.test)	
},
^(){
	print( DB:get("key",-1) );	
},
^(){
	DB:set("key",88);
	print( DB:get("key",-1) );	
},
^(){
	print(System.utdid());
	print(System.version());
	print(System.platform());
	print(System.device());
	print(System.screenSize());
	print(System.gc());
},
^(){
},
^(){
	print(IsMethod(AlertView));
},

^(){
	AlertView("title","msg", "ok", "cancel",
	         ^(buttonID){--提示框回调
	         	print(buttonID);
	         }
	 )	
},

^(){
	date1 = Date();
	print(date1);
	timeInterval1 = date1.timeInterval();
	print(timeInterval1);
	date2 = Date(timeInterval1-60*60*3);
	date3 = Date("2014-12-02 12:02:40");
	print( date2 );
	print( date3 );
	if date2== date3 then
		print("date2 ==date3");
	else
		print("date2 !=date3");
	end
	print( date1 - date2 );
},


^(){
	transform1 = Transform3D();
	if transform1.isIdentity()  then
		print("transform1.isIdentiy()==true")
	else
		print("transform1.isIdentiy()==false")
	end
	transform1.scale(2,2,2);
	imageView.transform3D(transform1);
	if transform1.isIdentity()  then
		print("transform1.isIdentiy()==true")
	else
		print("transform1.isIdentiy()==false")
	end
	transform1.set(Transform3D());
	if transform1.isIdentity()  then
		print("transform1.isIdentiy()==true")
	else
		print("transform1.isIdentiy()==false")
	end
},
^(){
	transform2 = Transform3D();
	transform2.translation(20,20,20);
	imageView.transform3D(transform2);
},
^(){
	transform3 = Transform3D();
	transform3.translation(20,20,20);
	transform3.rotate(3.14/3,0,1,0)    
	imageView.transform3D(transform3);
},
^(){
	transform4 = transform1.concat(transform2).concat(transform3);
	imageView.transform3D(transform4);
	if transform1==transform2  then
		print("transform1==transform2")
	else
		print("transform1!=transform2")
	end
	transform1.set(Transform3D());
	if transform1==Transform3D()  then
		print("transform1==transform2")
	else
		print("transform1!=transform2")
	end
},
^(){
	imageView.transform3D(Transform3D());
},

^(){
	transform4 = transform1.reset().scale(2,2,2) * transform2 * transform3;
	
		print("动画开始");
	Animate(3,0,
		^(){
			imageView.transform3D(transform4);
		}, 
		^(){
			print("动画结束了");
		}
		);	
},
^(){
	transform4 = transform1.reset().scale(2,2,2) * transform2 * transform3;
	imageView.transform3D(transform4);
	if transform1==transform2  then
		print("transform1==transform2")
	else
		print("transform1!=transform2")
	end
	transform1.set(Transform3D());
	if transform1==Transform3D()  then
		print("transform1==transform2")
	else
		print("transform1!=transform2")
	end
},
^(){
	imageView.removeFromSuper();
	imageView = nil;
	System.gc();	
},

^(){
	timer = Timer(^(){
		print("test timer");
		});
	timer.start(0.1,false);
},
^(){
	timer.start(0.1,true);
},
^(){
	timer.stop();
},

^(){
	scrollView = ScrollView();
	scrollView.backgroundColor(0xff00);
	scrollView.frame(10,10,100,100);
	scrollView.contentSize(600,300);
	print(scrollView.contentSize());
},
^(){
	scrollView.offset(150,0,false);
	print( scrollView.offset() );
},
^(){

},
^(){
	window.backgroundColor(0,0.5);
 	pageControl = PageControl();
 	pageControl.center(150,10);
 	pageControl.pageCount(10);
 	print(pageControl.numberOfPages() );
 	scrollView.delegate = ^(){
 		local  x,y = scrollView.contentOffset();
 		pageControl.currentPage(x/100);
 		print(pageControl.currentPage());
 	};
},
^(){
	scrollView.removeFromSuper();
	scrollView = nil;
},

^(){
	textFiled = TextField();
	textFiled.placeholder("我是输入框");
	textFiled.backgroundColor(0xffff00);
	textFiled.frame(10,20,300,40);
	textFiled.delegate =  {
		                          开始编辑=^(){
							            print("开始编辑")
							      },
		                          结束编辑=^(){
							            print("结束编辑")
							      },
		                          清理=^(){
							            print("清理")
							      },
						  };
	print(textFiled.placeholder());
},

^(){
	textFiled.resignFirstResponder();
	textFiled.delegate =  ^(){
					            print("开始编辑")
					      };
},
^(){
	textFiled.text("GameOver");
	print(textFiled.text());
},
^(){
	textFiled.removeFromSuper();
	textFiled = nil;
},

^(){

	view = Button();
	view.frame(10,60,100,200);
	print( view.frame() );
	view.backgroundColor(0xff0000);
	print(view);
	view.delegate = ^(){
		print("button call back");
	}

	title = Label();
	title.text("测试测试测试测试测试测试测试测试测试");
	title.backgroundColor(0xff00ff);
	title.frame(50,50,500,30);
	title.sizeToFit();
	view.addSubView(title);
},


^(){
	urlA = "http://img2.3lian.com/img2007/13/68/20080405154401434.png";
	urlB = "http://img2.3lian.com/img2007/13/68/20080405154401440.png";
	urlC = "http://img2.3lian.com/img2007/13/68/20080405154401926.png";

	button = Button();
	button.frame(150,50,100,100);
	button.image(urlA ,urlB,urlC )
	button.delegate = ^(){
		print("button call back");
	};
	button.enabled(false);
},

^(){
	textButton = Button();
	textButton.frame(150,10,100,40);
	textButton.title("A","B","C","D");
	textButton.delegate = ^(){
		print("textButton call back");
	};
},

^(){
	view.hidden(true);
	print( view.hidden() );
},
^(){
	view.hidden(false);
	print( view.hidden() );
},
^(){
	view.userInteractionEnabled = false;
	print( view.userInteractionEnabled() );
},
^(){
	view.userInteractionEnabled = true;
	print( view.userInteractionEnabled() );
},
^(){
	print( view.backgroundColor() );
	view.backgroundColor(0xff);
	print( view.backgroundColor() );
},
^(){
	print( view.alpha() );
	view.alpha(0.5);
	print( view.alpha() );
},
^(){
	view.cornerRadius(8);
	print( view.cornerRadius() );
},
^(){
	view.borderWidth(2);
	print( view.borderWidth() );
},
^(){
	view.borderColor(0xff0000);
	print( view.borderColor() );
},
^(){
	view.clipsToBounds(true);
	print( view.clipsToBounds() );
},
^(){
	view.clipsToBounds(false);
	print( view.clipsToBounds() );
},
^(){
	print( view.center() );
	view.center(160,240);
	print( view.center() );
},
^(){
	title.removeFromSuper();
},
^(){
	view.addSubView(title);
},
^(){
	title.transformRoteAndScale( 3.14/2,  2);
},
^(){
	title.transformRoteAndScale( 0, 1);
},
^(){
	title.anchorPoint( 0.0, 0.5 );
},
^(){
	title.transformRoteAndScale( 3.14/2,  2);
},

^(){
	dragGesture = PanGesture(
		^( g ){
			print("drag");
		}
	);

	window.addGesture(dragGesture);
},

^(){
	window.removeGesture(dragGesture);
},

^(){
	print("------end------");
	window.release();
},
}
index = 1;


function setLabel( i )
		label.text(""..i);
		if ( i%2==1 ) then
			label.textColor(0xff,1);
		else 
			label.textColor(0,0.2);
		end
		label.font(64);
		label.sizeToFit();
		label.center(w/2,h-120);
end

setLabel(1);

gesture = TapGestureRecognizer(
	^( g ){
		print("Test"..index .. " : ");
		func = demoArray[index];
		index = index + 1;
		setLabel(index);
		func();
	}
);

window.addGesture(gesture);

window.frame(0,0,w,h);


window.delegate = {
viewWillAppear = ^(){
	print("viewWillAppear");
},
viewDidAppear = ^(){
	print("viewDidAppear");
},
viewWillDisAppear = ^(){
	print("viewWillDisAppear");
},
viewDidDisAppear = ^(){
	print("viewDidDisAppear");
},
keyboardWillShow = ^(){
	print("keyboardWillShow");
},
keyboardDidShow = ^(){
	print("keyboardDidShow");
},
keyboardWillHide = ^(){
	print("keyboardWillHide");
},
keyboardDidHide = ^(){
	print("keyboardDidHide");
},
layoutSubviews = ^(){
	print("layoutSubviews");
},

};


