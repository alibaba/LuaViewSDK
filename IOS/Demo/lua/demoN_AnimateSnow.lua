
imageUrl = "snow.png";

w,h = System.screenSize();


window.frame(0, 0, w, h);
window.backgroundColor(0,0);
window.enabled(true);

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
		self.imageView.rotation(self.rote);
		self.imageView.scale(self.scale, self.scale);
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


