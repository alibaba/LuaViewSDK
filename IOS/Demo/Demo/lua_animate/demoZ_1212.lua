
if( LuaViewInited ) then
	return;
end

LuaViewInited = true;


SCR_W, SCR_H = System.screenSize();


window.frame(0, 0, SCR_W, SCR_H);
window.backgroundColor(0,0.5);
window.enabled(true);

-- bg = Image();
-- bg.image("jhs.png");
-- bg.frame(0,0,SCR_W, SCR_H);
-- bg.alpha(0.5)
window.alpha(0);
Animate(
	function()
		window.alpha(1)
	end,
	function ()
		window.alpha(1)
	end
	);
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
DXY = 70
NUM = 10
for i=1, NUM do
	for j=1, NUM do
		local dx = -SCR_W/2+i*DXY + DXY/2;
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

function ut_jump(title, url)
    local dic = {
        title = title,
        url = url
    };
    viewController.utName_dic("LUAVIEW_BTN_JUMP", dic);
end

function ut_close(title)
    local dic = {
        title = title
    };
    viewController.utName_dic("LUAVIEW_BTN", dic);
end

------------
function showEnterButton()
	view = View();
	view.size(SCR_W,SCR_H);
	local small = SCR_W/375;
	enterButton = Image();
	view.addView(enterButton)
	local date  = Date();
	local dateString = date.format("yyyy-MM-dd");
	print("time", dateString );
	if( dataString=="2015-12-11") then
		enterButton.image("enter11.png");
	else
		enterButton.image("enter10.png");
	end
	enterButton.adjustSize();
	local imageW,imageH = enterButton.size();
	enterButton.size(imageW/3*small, imageH/3*small);

	enterButton.callback(function ()
		local url = "https://ju.taobao.com/m/jusp/my/main/mtp.htm";--12.8
		-- local url = "https://ju.taobao.com/m/jusp/nv/juczmd/mtp.htm?hongbao=true";--12.10
		ut_jump("RED_BOX_RAIN_ENTER", url)
		viewController.openUrl(url);
		window.release();
	end);
	enterButton.center( SCR_W*0.5, SCR_H*0.5 );
	view.center( SCR_W*0.5, SCR_H*0.5 );


	closeButton = Image();
	view.addView(closeButton)
	closeButton.image("close.png");
	closeButton.adjustSize();
	local imageW,imageH = closeButton.size();
	closeButton.size(imageW/3*small, imageH/3*small);
	closeButton.bottom(enterButton.top());
	closeButton.right(enterButton.right());
	closeButton.callback(function ()
		ut_close("RED_BOX_RAIN_BT_CLOSE");
		closeApp();
	end);

	view.scale(0,0)

	Animate( 1, 0, 0.5,0,
		function(){
			view.scale(1,1);
		}, 
		function ()
			view.callback(function ()
				ut_close("RED_BOX_RAIN_BG_CLOSE");
				closeApp();
			end);
		end)
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


