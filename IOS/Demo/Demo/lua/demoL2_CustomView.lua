
window.backgroundColor(0xeeddee);

local scrW, scrH = System.screenSize();

customView = CustomView();
customView.frame(0,0,scrW,scrH);
local x = 0;
local y = 0;
customView.onDraw(function (canvas)


	print("onDraw");
end)


timer = Timer(function ()
	customView.invalidate();
end);

timer.start(0.1,true);


