
SCR_W, SCR_H = System.screenSize();


window.frame(0, 0, SCR_W, SCR_H);
window.backgroundColor(0);
window.enabled(true);

bg = Image();
bg.image("jhs.png");
bg.frame(0,0,SCR_W, SCR_H);
bg.alpha(0.5)

-------------------------------
function snowCreater(fileName, x0, y0)
	local snow = {};

	snow.times = 0;

	snow.imageView = Image(fileName);
	local snowW = math:random(32,64);

	snow.imageView.adjustSize();
	local imageW,imageH = snow.imageView.size();
	snow.imageView.size(imageW/3, imageH/3);

	snow.imageView.center( SCR_W + x0, y0 );

	function snow.move()
		self.imageView.center(self.x,self.y);
		-- local t = Transform3D();
		-- t.scale(self.scale, self.scale, 1 );
		-- self.imageView.transform3D(t);
	end
	function snow.nextXY()
		local x,y = self.imageView.center();
		local dx = -x-60;
		local dy = (x + 60) * SCR_H/SCR_W;

		self.x = x+dx;
		self.y = y+dy;
		self.time = math:sqrt(dx*dx + dy*dy)/ math:sqrt(SCR_W*SCR_W+SCR_H*SCR_H) * 2.5;
		-- self.rote = math:random(10,90)/100.0*3.14;
		-- self.scale = math:random(2,10)/10;
	end
	function snow.showSnows()
		self.nextXY();
		local time = math:random(0.8,1)*self.time;
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
DXY = 80
NUM = 9
for i=1, NUM do
	for j=1, NUM do
		local dx = -DXY*NUM/2+i*DXY;
		local dy = -DXY*(NUM+1) + j*DXY;
		if( index%2==0 ) then
			if( math:random(0,10)>4 ) then
				snowArr[index] = snowCreater("line.png", dx, dy ).showSnows();
			end
		else
			snowArr[index] = snowCreater("hongbao.png",dx, dy).showSnows();
		end
		index = index+1;
	end
end


------------
function showEnterButton()
	local small = SCR_W/375;
	enterButton = Image();
	enterButton.image("enter.png");
	enterButton.adjustSize();
	local imageW,imageH = enterButton.size();
	enterButton.size(imageW/3*small, imageH/3*small);

	enterButton.callback(function ()
		viewController.openUrl("http://taoboa.com");
	end);
	enterButton.center( SCR_W*0.5, SCR_H*0.4 );
	enterButton.scale( 0, 0)
	Animate( 1, 0, 0.5,0,
		function(){
			enterButton.scale(1,1);
		}, 
		function ()
		end)


	closeButton = Image();
	closeButton.image("close.png");
	closeButton.adjustSize();
	local imageW,imageH = closeButton.size();
	closeButton.size(imageW/3*small, imageH/3*small);
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


