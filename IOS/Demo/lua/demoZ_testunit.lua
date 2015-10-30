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
    detailInfo.adjustSize();
end
--------------------------
demoArray = {
	function()
		demoLabel.x = 2;
	end,
	function()
		demoLabel.y = 2;
	end,
	function()
		demoLabel.right = w-2;
	end,
	function()
		demoLabel.left = 2;
	end,
	function()
		demoLabel.bottom = 2;
	end,
	function()
		demoLabel.top = 2;
	end,
	function()
		demoLabel.width = 200;
	end,
	function()
		demoLabel.height = 80;
	end,
	function()
		demoLabel.x = 10;
	end,
	function()
		Http:get("http://www.baidu.com", --http url
			function(request)-- http call back
				print(request );
				print(request.responseStatusCode() );--http请求是否成功 返回码

				local heads =  request.responseHeaderFields();
				for key,value in pairs(heads) do
					print(key .. " : " .. value)
				end
			end
		);	
	end ,
	function()
		Http:post(
			"https://login-test.alibaba-inc.com/authorize/login.do",--url
			nil,--http头信息
			{--http post数据
				appcode="123456",
				name="taobao-hz\xicheng.dxc",
				password="dxc123",
				authtype="system"
			},
			function(request)--http 返回的数据
			  print(request.data() );
			end
		);	
	end,
	function()
		imageView = Image();
		imageView.image(imageUrl);
		imageView.frame(200,0,64,64);
	    detail("Image");
	    imageView.center(w/2,h/2);
	end,
	function()
		local g = PinchGesture(
			function(gesture)
				imageView.transformRoteAndScale( 0, gesture.scale());
			end);
		window.addGesture(g);
	end,
	function()
		local g = RotationGesture(
			function(gesture)
				imageView.transformRoteAndScale( gesture.rotation(), 1);
			end);
		window.addGesture(g);
	end,
	function()
		local g = SwipeGesture(
			function(gesture)
				if( gesture.state()==GestureState.END ) then
					print( "两个手势向左滑动" );
				end
			end);

		g.touchCount(2);
		g.direction( GestureDirection.LEFT);
		window.addGesture(g);
	end,
	function()
		local g = LongPressGesture (
			function(gesture)
				if( gesture.state()== GestureState.END )  then
					print( "长按手势检测到了" );
				end
			end);
		g.touchCount(2);
		window.addGesture(g);
	end,
	function()
		Vibrate();
	end,
	function()
		temp = loadJson("{test=\"haha\"}");
		print(temp.test)	
	end,
	function()
		print( DB:get("key",-1) );	
	end,
	function()
		DB:set("key",88);
		print( DB:get("key",-1) );	
	end,
	function()
		print(System.utdid());
		print(System.version());
		print(System.platform());
		print(System.device());
		print(System.screenSize());
		print(System.gc());
	end,
	function()
	end,
	function()
		print(IsMethod(AlertView));
	end,

	function()
		AlertView("title","msg", "ok", "cancel",
		         function(buttonID)--提示框回调
		         	print(buttonID);
		         end
		 )	
	end,

	function()
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
	end,


	function()
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
	end,
	function()
		transform2 = Transform3D();
		transform2.translation(20,20,20);
		imageView.transform3D(transform2);
	end,
	function()
		transform3 = Transform3D();
		transform3.translation(20,20,20);
		transform3.rotate(3.14/3,0,1,0)    
		imageView.transform3D(transform3);
	end,
	function()
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
	end,
	function()
		imageView.transform3D(Transform3D());
	end,

	function()
		transform4 = transform1.reset().scale(2,2,2) * transform2 * transform3;
		
			print("动画开始");
		Animate(3,0,
			function()
				imageView.transform3D(transform4);
			end, 
			function()
				print("动画结束了");
			end
			);	
	end,
	function()
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
	end,
	function()
		imageView.removeFromSuper();
		imageView = nil;
		System.gc();	
	end,

	function()
		timer = Timer(function()
			print("test timer");
			end);
		timer.start(0.1,false);
	end,
	function()
		timer.start(0.1,true);
	end,
	function()
		timer.stop();
	end,

	function()
		scrollView = ScrollView();
		scrollView.backgroundColor(0xff00);
		scrollView.frame(10,10,100,100);
		scrollView.contentSize(600,300);
		print(scrollView.contentSize());
	end,
	function()
		scrollView.offset(150,0,false);
		print( scrollView.offset() );
	end,
	function()

	end,
	function()
		window.backgroundColor(0,0.5);
	 	pageControl = PageControl();
	 	pageControl.center(150,10);
	 	pageControl.pageCount(10);
	 	print(pageControl.numberOfPages() );
	 	scrollView.delegate = function()
	 		local  x,y = scrollView.contentOffset();
	 		pageControl.currentPage(x/100);
	 		print(pageControl.currentPage());
	 	end;
	end,
	function()
		scrollView.removeFromSuper();
		scrollView = nil;
	end,

	function()
		textFiled = TextField();
		textFiled.placeholder("我是输入框");
		textFiled.backgroundColor(0xffff00);
		textFiled.frame(10,20,300,40);
		textFiled.callback{
	                          BeginEidting=function()
						            print("开始编辑")
						      end,
	                          EndEditing=function()
						            print("结束编辑")
						      end
						  };
		print( textFiled.placeholder() );
	end,

	function()
		textFiled.focus();
	end,
	function()
		textFiled.text("GameOver");
		print(textFiled.text());
	end,
	function()
		textFiled.removeFromSuper();
		textFiled = nil;
	end,

	function()

		view = Button();
		view.frame(10,60,100,200);
		print( view.frame() );
		view.backgroundColor(0xff0000);
		print(view);
		view.delegate = function()
			print("button call back");
		end

		title = Label();
		title.text("测试测试测试测试测试测试测试测试测试");
		title.backgroundColor(0xff00ff);
		title.frame(50,50,500,30);
		title.adjustSize();
		view.addSubView(title);
	end,


	function()
		urlA = "http://img2.3lian.com/img2007/13/68/20080405154401434.png";
		urlB = "http://img2.3lian.com/img2007/13/68/20080405154401440.png";
		urlC = "http://img2.3lian.com/img2007/13/68/20080405154401926.png";

		button = Button();
		button.frame(150,50,100,100);
		button.image(urlA ,urlB,urlC )
		button.delegate = function()
			print("button call back");
		end;
		button.enabled(false);
	end,

	function()
		textButton = Button();
		textButton.frame(150,10,100,40);
		textButton.title("A","B","C","D");
		textButton.delegate = function()
			print("textButton call back");
		end;
	end,

	function()
		view.hidden(true);
		print( view.hidden() );
	end,
	function()
		view.hidden(false);
		print( view.hidden() );
	end,
	function()
		view.userInteractionEnabled = false;
		print( view.userInteractionEnabled() );
	end,
	function()
		view.userInteractionEnabled = true;
		print( view.userInteractionEnabled() );
	end,
	function()
		print( view.backgroundColor() );
		view.backgroundColor(0xff);
		print( view.backgroundColor() );
	end,
	function()
		print( view.alpha() );
		view.alpha(0.5);
		print( view.alpha() );
	end,
	function()
		view.cornerRadius(8);
		print( view.cornerRadius() );
	end,
	function()
		view.borderWidth(2);
		print( view.borderWidth() );
	end,
	function()
		view.borderColor(0xff0000);
		print( view.borderColor() );
	end,
	function()
		view.clipsToBounds(true);
		print( view.clipsToBounds() );
	end,
	function()
		view.clipsToBounds(false);
		print( view.clipsToBounds() );
	end,
	function()
		print( view.center() );
		view.center(160,240);
		print( view.center() );
	end,
	function()
		title.removeFromSuper();
	end,
	function()
		view.addSubView(title);
	end,
	function()
		title.transformRoteAndScale( 3.14/2,  2);
	end,
	function()
		title.transformRoteAndScale( 0, 1);
	end,
	function()
		title.anchorPoint( 0.0, 0.5 );
	end,
	function()
		title.transformRoteAndScale( 3.14/2,  2);
	end,

	function()
		dragGesture = PanGesture(
			function( g )
				print("drag");
			end
		);

		window.addGesture(dragGesture);
	end,

	function()
		window.removeGesture(dragGesture);
	end,

	function()
		print("------end------");
		window.release();
	end,
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
		label.adjustSize();
		label.center(w/2,h-120);
end

setLabel(1);

gesture = TapGestureRecognizer(
	function( g )
		print("Test"..index .. " : ");
		func = demoArray[index];
		index = index + 1;
		setLabel(index);
		func();
	end
);

window.addGesture(gesture);

window.frame(0,0,w,h);


window.callback = {
	viewWillAppear = function()
		print("viewWillAppear");
	end,
	viewDidAppear = function()
		print("viewDidAppear");
	end,
	viewWillDisAppear = function()
		print("viewWillDisAppear");
	end,
	viewDidDisAppear = function()
		print("viewDidDisAppear");
	end,
	keyboardWillShow = function()
		print("keyboardWillShow");
	end,
	keyboardDidShow = function()
		print("keyboardDidShow");
	end,
	keyboardWillHide = function()
		print("keyboardWillHide");
	end,
	keyboardDidHide = function()
		print("keyboardDidHide");
	end,
	layoutSubviews = function()
		print("layoutSubviews");
	end,
};


