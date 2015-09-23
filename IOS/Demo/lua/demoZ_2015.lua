imageUrl = "http://ico.ooopic.com/ajax/iconpng/?id=96776.png";



imageRander0 = UIImageView();
imageRander0.setFrame(0,100,100,100);
imageRander1 = UIImageView();
imageRander1.setFrame(0,100,90,90);
imageRander0.setImage(imageUrl,^(){
    imageRander1.render(imageRander0,0);
    imageRander1.setAlpha(0.98)
});

w,h = System.screenSize();--获取屏幕尺寸


--字符串分割测试
s = "中文2344中文2234中文";
while s.len()>0 do
    i,j = s.find("%d+") ;
	if( i and j ) then
        print(s.sub(1,i-1));
		print(s.sub(i,j));
		s = s.sub(j+1,s.len());
	else
		print(s);
		s = "";
    end
end


demoLabel = UILabel();
demoLabel.setFrame(0,20,w,40);
demoLabel.setText("点击屏幕一次执行一个测试demo");

label = UILabel();-- 创建一个文本框
label.setFrame(160,h-80,w,80);


att1 = AttributedString( "12te",  { fontSize=12, fontWeight="bold", foregroundColor=0xff, foregroundColorAlpha=0.9 , charpace=4} );
att2 = AttributedString( "测试",  { fontSize=30, foregroundColor=0xff0000, backgroundColor=0xff, charpace=10} );
att3 = AttributedString( "289",  { fontSize=13, foregroundColor=0x777777, strikethroughStyle=1} );
att4 = AttributedString( "下划", { fontSize=9, foregroundColor=0x777777, underlineStyle=1} );

label2 = UILabel();
label2.setFrame(20,30,w-40, 60);
label2.setText(att1 + att2 + att3 + att4);

label2.setBackgroundColor(0xff00FF)
label2.setMasksToBounds(false);
label2.setCornerRadius(4);
--label2.setClipsToBounds(true);
--label2.setShadowPath();
label2.setShadowOffset(1,1);
label2.setShadowRadius(2);
label2.setShadowOpacity(8);
label2.setShadowColor(0xff0000);

detailInfo = UILabel();
detailInfo.setFrame(0, h-40, w, 40);

function detail( info )
    detailInfo.setText(info);
    detailInfo.sizeToFit();
end
--------------------------
demoArray = {
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
	imageView = UIImageView();
	imageView.setImage(imageUrl);
	imageView.setFrame(200,0,64,64);
    detail("UIImageView");
    imageView.setCenter(w/2,h/2);
},
^(){
	--[[
	UIGestureRecognizerStatePossible,
    UIGestureRecognizerStateBegan,// 手势开始
    UIGestureRecognizerStateChanged,
    UIGestureRecognizerStateEnded,// 手势结束
    UIGestureRecognizerStateCancelled,
    UIGestureRecognizerStateFailed,
	]]
	local g = UIPinchGestureRecognizer(^(gesture){
			imageView.transformRoteAndScale( 0, gesture.scale());
		});
	window.addGestureRecognizer(g);
},
^(){
	local g = UIRotationGestureRecognizer(^(gesture){
			imageView.transformRoteAndScale( gesture.rotation(), 1);
		});
	window.addGestureRecognizer(g);
},
^(){
	local g = UISwipeGestureRecognizer(^(gesture){
			if( gesture.state()==UIGestureRecognizerStateEnded ) then
				print( "两个手势向左滑动" );
			end
		});
    --[[UISwipeGestureRecognizerDirectionRight = 1,
    UISwipeGestureRecognizerDirectionLeft  = 2,
    UISwipeGestureRecognizerDirectionUp    = 4,
    UISwipeGestureRecognizerDirectionDown  = 8]]
	g.setTouchCount(2);
	g.setDirection(UISwipeGestureRecognizerDirectionLeft);
	window.addGestureRecognizer(g);
},
^(){
	local g = UILongPressGestureRecognizer(^(gesture){
			if( gesture.state()==UIGestureRecognizerStateEnded )  then
				print( "长按手势检测到了" );
			end
		});
	g.setTouchCount(2);
	window.addGestureRecognizer(g);
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
	print(IsMethod(UIAlertView));	
},

^(){
	UIAlertView("title","msg", "ok", "cancel",
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
	imageView.setTransform3D(transform1);
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
	imageView.setTransform3D(transform2);
},
^(){
	transform3 = Transform3D();
	transform3.translation(20,20,20);
	transform3.rotate(3.14/3,0,1,0)    
	imageView.setTransform3D(transform3);
},
^(){
	transform4 = transform1.concat(transform2).concat(transform3);
	imageView.setTransform3D(transform4);
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
	imageView.setTransform3D(Transform3D());
},

^(){
	transform4 = transform1.reset().scale(2,2,2) * transform2 * transform3;
	
		print("动画开始");
	UIAnimate(3,0,
		^(){
			imageView.setTransform3D(transform4);
		}, 
		^(){
			print("动画结束了");
		}
		);	
},
^(){
	transform4 = transform1.reset().scale(2,2,2) * transform2 * transform3;
	imageView.setTransform3D(transform4);
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
	imageView.removeFromSuperView();
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
	scrollView = UIScrollView();
	scrollView.setBackgroundColor(0xff00);
	scrollView.setFrame(10,10,100,100);
	scrollView.setContentSize(600,300);
	print(scrollView.contentSize());
},
^(){
	scrollView.setContentOffset(150,0,false);
	print( scrollView.contentOffset() );
},
^(){
	scrollView.setPageEnable(true);
	print(scrollView.pageEnable());
},
^(){
	window.setBackgroundColor(0,0.5);
 	pageControl = UIPageControl();
 	pageControl.setCenter(150,10);
 	pageControl.setPageCount(10);
 	print(pageControl.numberOfPages() );
 	scrollView.delegate = ^(){
 		local  x,y = scrollView.contentOffset();
 		pageControl.setCurrentPage(x/100);
 		print(pageControl.currentPage());
 	};
},
^(){
	scrollView.removeFromSuperView();
	scrollView = nil;
},

^(){
	textFiled = UITextField();
	textFiled.setPlaceholder("我是输入框");
	textFiled.setBackgroundColor(0xffff00);
	textFiled.setFrame(10,20,300,40);
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
	textFiled.setText("GameOver");
	print(textFiled.text());
},
^(){
	textFiled.removeFromSuperView();
	textFiled = nil;
},

^(){

	view = UIButton();
	view.setFrame(10,60,100,200);
	print( view.frame() );
	view.setBackgroundColor(0xff0000);
	print(view);
	view.delegate = ^(){
		print("button call back");
	}

	title = UILabel();
	title.setText("测试测试测试测试测试测试测试测试测试");
	title.setBackgroundColor(0xff00ff);
	title.setFrame(50,50,500,30);
	title.sizeToFit();
	view.addSubView(title);
},


^(){
	urlA = "http://img2.3lian.com/img2007/13/68/20080405154401434.png";
	urlB = "http://img2.3lian.com/img2007/13/68/20080405154401440.png";
	urlC = "http://img2.3lian.com/img2007/13/68/20080405154401926.png";

	button = UIButton();
	button.setFrame(150,50,100,100);
	button.setImage(urlA ,urlB,urlC )
	button.delegate = ^(){
		print("button call back");
	};
	button.setEnabled(false);
    --button.setSelected(true);
},

^(){
	textButton = UIButton();
	textButton.setFrame(150,10,100,40);
	textButton.setTitle("A","B","C","D");
	textButton.delegate = ^(){
		print("textButton call back");
	};
},

^(){
	view.setHidden(true);
	print( view.hidden() );
},
^(){
	view.setHidden(false);
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
	view.setBackgroundColor(0xff);
	print( view.backgroundColor() );
},
^(){
	print( view.alpha() );
	view.setAlpha(0.5);
	print( view.alpha() );
},
^(){
	view.setCornerRadius(8);
	print( view.cornerRadius() );
},
^(){
	view.setBorderWidth(2);
	print( view.borderWidth() );
},
^(){
	view.setBorderColor(0xff0000);
	print( view.borderColor() );
},
^(){
	view.setClipsToBounds(true);
	print( view.clipsToBounds() );
},
^(){
	view.setClipsToBounds(false);
	print( view.clipsToBounds() );
},
^(){
	print( view.center() );
	view.setCenter(160,240);
	print( view.center() );
},
^(){
	title.removeFromSuperView();
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
	title.setAnchorPoint( 0.0, 0.5 );
},
^(){
	title.transformRoteAndScale( 3.14/2,  2);
},

^(){
	dragGesture = UIPanGestureRecognizer(
		^( g ){
			print("drag");
		}
	);

	window.addGestureRecognizer(dragGesture);
},

^(){
	window.removeGestureRecognizer(dragGesture);
},

^(){
	print("------end------");
	window.release();
},
}
index = 1;


function setLabel( i )
		label.setText(""..i);
		if ( i%2==1 ) then
			label.setColor(0xff,1);
		else 
			label.setColor(0,0.2);
		end
		label.setFont(64);
		label.sizeToFit();
		label.setCenter(w/2,h-60);
end

setLabel(1);

gesture = UITapGestureRecognizer(
	^( g ){
		print("Test"..index .. " : ");
		func = demoArray[index];
		func();
		index = index + 1;
		setLabel(index);
	}
);

window.addGestureRecognizer(gesture);

window.setFrame(0,0,w,h);


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


