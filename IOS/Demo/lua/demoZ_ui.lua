imageUrl = "http://ico.ooopic.com/ajax/iconpng/?id=96776.png";
imageUrl = "snow.png";

w,h = System.screenSize();


window.frame(0, 0, w, h);
window.backgroundColor(0,0);
window.enabled(true);

System.gc();


textField = TextField();
textField.frame((w-300)/2,(h-40)/2-40,300,40);
textField.backgroundColor = 0xff0000;

textField.delegate =    function()
							print("测试ok");
						end;



button = Button();
button.frame( (w-300)/2,(h-40)/2,300,40);
button.title("关闭");

print(button);

DBKey = "keyvalue";

button.callback( function()
			            times = DB:get(DBKey,"0");
			            AlertView("提示信息".. times,
			            			"确定退出",
			            			"确定","取消",
			            			 function(index)
							            DB:set( DBKey, (times+1.2) )
						                if ( index==0 ) then
						                    window.release();
						                end
							         end
						          );
					end
			 );

window.callback{
					KeyboardWillShow=function()
						print("window.keyboardWillShow");
					end,
					KeyboardWillHide=function()
						print("window.keyboardWillHide");
					end,
					ViewDidAppear=function()
						print("viewDidAppear.test");
						textField.becomeFirstResponder();
					end
			   };


print(System.platform());
print(System.device());

-------------------------------
function snowCreater()
	local snow = {};

	snow.times = 0;

	snow.imageView = Image(imageUrl);
	local snowW = math:random(32,64);

	snow.imageView.frame( 0, 0, snowW, snowW);
	local x0 = math:random(0,w);
	local y0 = -math:random(0,60);
	snow.imageView.center(x0,y0);

	function snow.move()
		self.imageView.center(self.x,self.y);
		self.imageView.transformRoteAndScale(self.rote, self.scale, self.scale);
	end
	function snow.nextXY()
		local dx = math:random(-5,5);
		local dy = math:random(30,60);
		local x,y = self.imageView.center();
		self.x = x+dx;
		self.y = y+dy*1.5;
		self.rote = math:random(10,90)/100.0*3.14;
		self.scale = math:random(8,10)/10;
	end
	function snow.showSnows()
		if ( self.times>20 ) then
			return ;
		end
		self.times = self.times + 1;

		self.nextXY();
		local time = math:random(20,30)/10.0;
		Animate(time,
				function()
					self.move();
				end,
				function()
				  self.showSnows();
				end
			);
	end

	return snow;
end
-------------------------------------
snowArr = {};

index = 1;
snowTimer = Timer(
	function()
		if (index<50 ) then
		   	snowArr[index] = snowCreater();
			snowArr[index].showSnows();
		else
			snowTimer.cancel();
		end
		index = index+1;
	end
);

snowTimer.start(0.2, true);



date1 = Date("2015年01月13 20时25分");
date2 = Date();

print(date2 - date1)

shake = Button();
shake.frame( (w-300)/2,(h-40)/2+40,300,40);
shake.title("震动");
shake.callback( 
	function()
	    Vibrate();
	end
);

gesture = PanGesture(
        function( g )
            x,y = g.location();
			print("PanGesture: ".. x .. "," ..y);
		end
);



window.addGesture(gesture);

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


Http:get(
	"http://www.baidu.com", 
	function(request)
		print(request );
	end
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
	function(request)
	  print(request.data() );
	end
);

table={a=4,b=5,c=6,d=7}

for key,value in pairs(table) do
	print(key,value)
end


data = Data(1024);
data[2] = 3;
print(data[2]);
