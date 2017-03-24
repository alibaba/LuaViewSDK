
w,h = System:screenSize();

player = AudioPlayer("http://fm111.img.xiaonei.com/tribe/20070613/10/52/A314269027058MUS.mp3");
--player = AudioPlayer("http://g.tbcdn.cn/ju/lua/1.2.0/shake.js");

button = Button();
button:title("播放音效");
button:frame(0,100,w,40);
button:backgroundColor(0xff,1);
button:callback( function()
	if( player ) then
		player:play();
	end
end)

player:play();
