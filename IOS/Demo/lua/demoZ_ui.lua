imageUrl = "http://ico.ooopic.com/ajax/iconpng/?id=96776.png";
imageUrl = "snow.png";

w,h = System.screenSize();


window.setFrame(0, 0, w, h);
window.setBackgroundColor(0,0);
window.enabled(true);

System.gc();


textField = UITextField();
textField.setFrame((w-300)/2,(h-40)/2-40,300,40);
textField.backgroundColor = 0xff0000;

textField.delegate =    ^(){
							print("测试ok");
						};



button = UIButton();
button.setFrame( (w-300)/2,(h-40)/2,300,40);
button.setTitle("关闭");

print(button);

DBKey = "keyvalue";

button.delegate =  ^(){
			            times = DB:get(DBKey,"0");
			            UIAlertView("提示信息".. times,
			            			"确定退出",
			            			"确定","取消",
			            			 ^(index){
							            DB:set(DBKey, (times+1.2) )
							                if ( index==0 ) {
							                    window.release();
							                }
							            }
						            );
					};

window.delegate =   {
						keyboardWillShow=^(){
							print("window.keyboardWillShow");
						},
						keyboardWillHide=^(){
							print("window.keyboardWillHide");
						},
						viewDidAppear=^(){
							print("viewDidAppear.test");
							textField.becomeFirstResponder();
						}
					};


print(System.platform());
print(System.device());

-------------------------------
function snowCreater() {
	local snow = {};

	snow.times = 0;

	snow.imageView = UIImage(imageUrl);
	local snowW = math:random(32,64);

	snow.imageView.setFrame( 0, 0, snowW, snowW);
	local x0 = math:random(0,w);
	local y0 = -math:random(0,60);
	snow.imageView.setCenter(x0,y0);

	function snow.move() {
		self.imageView.setCenter(self.x,self.y);
		self.imageView.transformRoteAndScale(self.rote, self.scale, self.scale);
	}
	function snow.nextXY() {
		local dx = math:random(-5,5);
		local dy = math:random(30,60);
		local x,y = self.imageView.center();
		self.x = x+dx;
		self.y = y+dy*1.5;
		self.rote = math:random(10,90)/100.0*3.14;
		self.scale = math:random(8,10)/10;
	}
	function snow.showSnows() {
		if ( self.times>20 ) {
			return ;
		}
		self.times = self.times + 1;

		self.nextXY();
		local time = math:random(20,30)/10.0;
		UIAnimate(time,
			^(){
				self.move();
			},
			^(){
			  self.showSnows();
			}
			);
	}

	return snow;
}
-------------------------------------
snowArr = {};

index = 1;
snowTimer = Timer(
	^(){
		if (index<50 ) {
		   	snowArr[index] = snowCreater();
			snowArr[index].showSnows();
		} else {
			snowTimer.cancel();
		}
		index = index+1;
	}
);

snowTimer.start(0.2, true);



date1 = Date("2015年01月13 20时25分");
date2 = Date();

print(date2 - date1)

shake = UIButton();
shake.setFrame( (w-300)/2,(h-40)/2+40,300,40);
shake.setTitle("震动");
shake.setDelegate( ^(){
    Vibrate();
}

);

gesture = UIPanGestureRecognizer(
        ^( g ){
            x,y = g.location();
			print("UIPanGestureRecognizer: ".. x .. "," ..y);
		}
);



window.addGestureRecognizer(gesture);


--r = loadstring("local function test (){ print(1234) } r = test");
--r();

local fun = loadstring(" return 345;")
print( fun() )


mode = LoadJson(" {\"key\":\"可以嘛\"}");

print(mode.key);

page= [[
　　　　<HTML>
　　　　　　<HEAD>
　　　　　　　　<TITLE>An HTML Page</TITLE>
　　　　　　</HEAD>
　　　　　　<BODY>
　　　　　　　　<A HREF="http://www.lua.org">Lua</A>
　　　　　　</BODY>
　　　　</HTML>
　　　　]]


print(page);


Http:get("http://www.baidu.com", ^(request){
print(request );
}
);


Http:post(
"https://login-test.alibaba-inc.com/authorize/login.do",
nil,
{
	appcode="123456",
	name="taobao-hz\xicheng.dxc",
	password="dxc123",
	authtype="system"
},
	^(request){
	  print(request.data() );
	}
);

table={a=4,b=5,c=6,d=7}

for key,value in pairs(table) do

print(key,value)

end


data = Data(1024);
data[2] = 3;
print(data[2]);
