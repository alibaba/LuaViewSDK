
imageUrl = "http://img.hb.aicdn.com/e7639de7054d813670c82f3f2bd5e02adec0b81129204-5B4zXs_fw580";

w,h = System.screenSize();


window.frame(0, 0, w, h);
window.backgroundColor(0,0.5);
window.enabled(true);

-------------------------------
function snowCreater()
	local snow = {};

	snow.times = 0;

	snow.imageView = Image(imageUrl);
	local snowW = math:random(32,64);
	snow.imageView.callback(function()
        viewController.openUrl("jhs://go/luaview?source=ppt.lv&package=ppt");
	end);

	snow.imageView.frame( 0, 0, snowW, snowW);
	local x0 = math:random(0,w);
	local y0 = -math:random(0,60);
	snow.imageView.center(x0,y0);

	function snow.move()
		self.imageView.center(self.x,self.y);
		local t = Transform3D();
		--t.rotate(self.rote, 1, 0,0);
		--t.rotate(self.rote/2, 0, 1,0);
		t.scale(self.scale, self.scale, 1 );
		self.imageView.transform3D(t);
	end
	function snow.nextXY()
		local dx = math:random(-5,5);
		local dy = math:random(30,60);
		local x,y = self.imageView.center();
		self.x = x+dx;
		self.y = y+dy*1.5;
		self.rote = math:random(10,90)/100.0*3.14;
		self.scale = math:random(2,10)/10;
	end
	function snow.showSnows()
		if ( self.times>5 ) then
			diedNum = diedNum - 1;
			print(diedNum);
			if( diedNum == 1 ) then
				closeApp();
			end
			return ;
		end
		self.times = self.times + 1;

		self.nextXY();
		local time = math:random(20,30)/10.0;
		Animate(time,0,0,0,3,
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

totoalNum = 50;
diedNum = 50;

index = 1;
snowTimer = Timer(
	function()
		if (index<totoalNum ) then
		   	snowArr[index] = snowCreater();
			snowArr[index].showSnows();
		else
			snowTimer.cancel();
		end
		index = index+1;
	end
);

snowTimer.start(0.2, true);


function closeApp()
	Animate(
		function ()
			window.alpha(0)
		end,
		function ()
			window.release();
		end
		)
end

