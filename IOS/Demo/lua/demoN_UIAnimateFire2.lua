
scrW, scrH = System.screenSize();

window.setFrame(0, 0, scrW, scrH);
window.setBackgroundColor(0xffffff,1);
window.enabled(true);



function createLaZhu(x,y){
	local lazhu = {};

	local percent = 1;
	local r = 40*percent;

	local bodyX0 = x;
	local bodyY0 = y;
	local fireArr = {};

	lazhu.lazhuBody = UIImage();
	lazhu.lazhuBody.setFrame( bodyX0 - 64*0.45, bodyY0 + r*0.5, 64, 104);
	lazhu.lazhuBody.setImage("lazhu.png");
	lazhu.lazhuBody.enabled(true);

	lazhu.onOff = true;
	lazhu.button = UIButton();
	lazhu.button.setTitle("开/关");
	lazhu.button.setBackgroundColor(0xff0000,1);
	lazhu.button.setFrame(0, 50, 64, 60);
	lazhu.button.delegate = ^(){
		if( lazhu.onOff ) {
			lazhu.onOff = false;
		} else {
			lazhu.onOff = true;
			for( index = 1, 20 ) {
				fireArr[index].showfires();
			}
		}
	};
	lazhu.lazhuBody.addView(lazhu.button);

	function lazhu.move( dx,dy )
	 	bodyX0 = bodyX0 + dx;
	 	bodyY0 = bodyY0 + dy;
		self.lazhuBody.setFrame( bodyX0 - 64*0.45, bodyY0 + r*0.5, 64, 104);
	 end 


	-------------------------------
	function lazhu.fireCreater() {
		local fire = {};
		fire.times = 0;

		fire.imageView1 = UIImage();
		fire.imageView2 = UIImage();
		fire.imageView1.setImage("color1.png");
		fire.imageView2.setImage("color2.png");
		fire.imageView1.setFrame(0,0,r*2,r*2);
		fire.imageView2.setFrame(0,0,r*2,r*2);

		fire.bg = UIImage();
		fire.bg.setFrame(0,0,r*2,r*2);
		fire.bg.addView(fire.imageView1);
		fire.bg.addView(fire.imageView2);

		function fire.initX0Y0()
			self.bg.scale( 1, 1);
			self.bg.size( r*2, r*2);
			self.bg.alpha = 0.5;

			local x0 = math:random(bodyX0, bodyX0 + r*0.1);
			local y0 = math:random(bodyY0, bodyY0 + r*0.3);

			self.bg.setCenter(x0,y0);
			self.x = x0;
			self.y = y0;

			self.imageView1.alpha = 1;
			self.imageView2.alpha = 0;
		end

		function fire.move() {
			self.bg.center( self.x, self.y );
			self.bg.scale( 0.2, 0.4);
			self.imageView1.alpha(0);
			self.imageView2.alpha(1);
			self.bg.alpha = 0;
		}

		function fire.nextXYAndColor() {
			local len = 30*percent;
			local dx = math:random(-len,len);
			local maxDy = math:sqrt( (len*len*2 - dx*dx) )*2;
			local dy = math:random( -maxDy, 0 );
			local x,y = self.bg.center();
			self.x = x+dx;
			self.y = y+dy;
		}
		function fire.showfires() {
			self.initX0Y0();
			self.nextXYAndColor();

			local time = math:random(7,10)/10.0;
			UIAnimate(time,
				function (){
					self.move();
				}
				,
				function (){
					if( lazhu.onOff ) {
						self.showfires();
					}
				}
				);
		}

		return fire;
	}
	-------------------------------------

	local index = 1;
	lazhu.fireTimer = Timer(
		^(){
			if (index<=20 ) {
			   	fireArr[index] = lazhu.fireCreater();
				fireArr[index].showfires();
				index = index+1;
			} else {
				lazhu.fireTimer.cancel();
			}
		}
	);

	lazhu.fireTimer.start(0.1, true);
	return lazhu;
}

lazhu1 = createLaZhu(50,200);
lazhu2 = createLaZhu(160,200);
lazhu3 = createLaZhu(260,200);



	dragGesture = UIPanGestureRecognizer(
		^( g ){
			local state = g.state();
			if( state == UIGestureRecognizerStateBegan ) {
				 gestureX, gestureY = g.location();
			} else  if( state == UIGestureRecognizerStateChanged ) {
				 local x, y = g.location();
				 local dx = x- gestureX;
				 local dy = y- gestureY;
				 gestureX = x;
				 gestureY = y;
				 lazhu2.move(dx,dy);
			}
		}
	);

	window.addGestureRecognizer(dragGesture);

