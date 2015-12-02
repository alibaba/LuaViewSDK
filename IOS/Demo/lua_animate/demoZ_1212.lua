
SCR_W, SCR_H = System.screenSize();


window.frame(0, 0, SCR_W, SCR_H);
window.backgroundColor(0,0.5);
window.enabled(true);

-------------------------------
function snowCreater(fileName, x0, y0)
	local snow = {};

	snow.times = 0;

	snow.imageView = Image(fileName);
	local snowW = math:random(32,64);

	snow.imageView.adjustSize();
	local imageW,imageH = snow.imageView.size();
	snow.imageView.size(imageW/3, imageH/3);

	snow.imageView.center(x0,y0);

	function snow.move()
		self.imageView.center(self.x,self.y);
		-- local t = Transform3D();
		-- t.scale(self.scale, self.scale, 1 );
		-- self.imageView.transform3D(t);
	end
	function snow.nextXY()
		local dx = -SCR_W*1.1;
		local dy = SCR_H*1.1;
		local x,y = self.imageView.center();
		self.x = x+dx;
		self.y = y+dy;
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
				Animate(function ()
					self.imageView.alpha(0);
				end)
			end
			);
		return self;
	end

	function snow.removeAll()
		self.imageView.removeFromeSuper();
	end

	return snow;
end
-------------------------------------
snowArr = {};
index = 1;
DXY = 100
for i=1, 6 do
	for j=1, 6 do
		local dx = -DXY*3+i*DXY;
		local dy = -DXY*7 + j*DXY;
		if( index%2==0 ) then
			if( math:random(0,10)>6 ) then
				snowArr[index] = snowCreater("line.png", SCR_W + dx, dy ).showSnows();
			end
		else
			snowArr[index] = snowCreater("hongbao.png",SCR_W +dx, dy).showSnows();
		end
		index = index+1;
	end
end


------------
function showEnterButton()
	enterButton = Image();
	enterButton.image("enter.png");
	enterButton.adjustSize();
	local imageW,imageH = enterButton.size();
	enterButton.size(imageW/3, imageH/3);

	enterButton.callback(function ()
		viewController.openUrl("http://taoboa.com");
	end);
	enterButton.center( SCR_W/2, SCR_H/2 );
	enterButton.scale( 0, 0)
	Animate( 3, 0, 0.3,0, 
		function(){
			enterButton.scale(1.2,1.2);
		}, 
		function ()
		end)


	closeButton = Image();
	closeButton.image("close.png");
	closeButton.adjustSize();
	closeButton.bottom(enterButton.top());
	closeButton.right(enterButton.right());
	closeButton.callback(function ()
		closeApp();
	end);
end

snowTimer = Timer(
	function()
		showEnterButton();
	end
);

snowTimer.start(2.5, false);


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


