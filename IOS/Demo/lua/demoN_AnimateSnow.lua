
imageUrl = "snow.png";

w,h = System.screenSize();


window.rrame(0, 0, w, h);
window.backgroundColor(0,0);
window.enabled(true);

-------------------------------
function snowCreater() {
	local snow = {};

	snow.times = 0;

	snow.imageView = Image(imageUrl);
	local snowW = math:random(32,64);

	snow.imageView.frame( 0, 0, snowW, snowW);
	local x0 = math:random(0,w);
	local y0 = -math:random(0,60);
	snow.imageView.center(x0,y0);

	function snow.move() {
		self.imageView.center(self.x,self.y);
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
		Animate(time,
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


