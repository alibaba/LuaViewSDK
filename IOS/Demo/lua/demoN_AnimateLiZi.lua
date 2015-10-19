
w,h = System.screenSize();

window.frame(0, 0, w, h);
window.backgroundColor(0,0);
window.enabled(true);


-------------------------------
function snowCreater() {
	local snow = {};

	snow.times = 0;

	snow.imageView = View();
	snow.imageView.cornerRadius(5);
	snow.imageView.clipsToBounds = true;
	-- snow.imageView.backgroundColor(0xff);

	snow.imageView.size( 10, 10);



	function snow.initX0Y0( )
		self.imageView.alpha = 0.85;
		local x0 = math:random(160,160+5);
		local y0 = math:random(160,160+5);
		self.imageView.center(x0,y0);
		self.x = x0;
		self.y = y0;

		self.imageView.backgroundColor( self.randomColor() );
	end

	function snow.move() {
		self.imageView.center(self.x,self.y);
		self.imageView.alpha(0);
		self.imageView.backgroundColor(self.color);
	}
	function snow.randomColor()
		return 0xff;
	
		-- local r = math:abs( math:random()*256 ) % 256 ;
		-- local g = math:abs( math:random()*256 ) % 256 ;
		-- local b = math:abs( math:random()*256 ) % 256 ;
		-- return r*256*256 + g*256 + b;
	end
	function snow.nextXYAndColor() {
		local len = 160;
		local dx = math:random(-len,len);
		local dy = math:random(-len,len);
		local x,y = self.imageView.center();
		self.x = x+dx;
		self.y = y+dy*1.5;
		self.color = self.randomColor();
	}
	function snow.showSnows() {
		self.initX0Y0();
		self.nextXYAndColor();

		local time = math:random(15,20)/10.0;
		Animate(time,
			^(){
				self.move();
			}
			,
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
		if (index<100 ) {
			index = index+1;
		   	snowArr[index] = snowCreater();
			snowArr[index].showSnows();
		} else {
			snowTimer.cancel();
		}
	}
);

snowTimer.start(0.1, true);


