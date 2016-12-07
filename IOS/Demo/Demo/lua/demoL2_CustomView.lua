
window.backgroundColor(0xeeddee);

local scrW, scrH = System.screenSize();

customView = CustomView();
customView.frame(0,0,scrW,scrH);
local x = 100;
local y = 0;
customView.onDraw(function (canvas)
	canvas.drawRect(x,y,20,20);
	canvas.drawRoundRect(x+100,y,50,50,10,10);
	y = y + 1;
end)


timer = Timer(function ()
	customView.invalidate();
end);

timer.start(0.01,true);


