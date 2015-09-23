
w,h = System.screenSize();

player = AudioPlayer("http://g.tbcdn.cn/ju/lua/1.2.0/shake.js");

button = UIButton();
button.setTitle("播放音效");
button.setFrame(0,100,w,40);
button.setBackgroundColor(0xff,1);
button.delegate = ^(){
	if( player ) then
		player.play();
	end
}

