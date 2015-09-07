print("下载测试脚本")

print("初始化窗口大小, 默认窗口大小是(0,0)");
w,h = System.screenSize();
window.setFrame(0, 0, w, h);
window.setUserInteractionEnabled(true);
window.setBackgroundColor(0xffffff,1);

imageUrl = "https://ss0.bdstatic.com/5a21bjqh_Q23odCf/static/superplus/img/logo_white_ee663702.png"


Download( imageUrl,"baidu.png",^(data){
		print( data );
		--数据流写到文件中
		File:save("demo.png",data);
        print(PathOfResource("demo.png"));
        --文件创建图片
		imageView = UIImageView();
		imageView.setImage("demo.png");
		imageView.setFrame(0,0,128,128);

		--内容流创建图片
		imageView2 = UIImageView();
		imageView2.setImage(data);
		imageView2.setFrame(128,0,128,128);

		--读取文件流创建文件
		imageView3 = UIImageView();
		imageView3.setImage(File:read("demo.png"));
		imageView3.setFrame(128*2,0,128,128);
	});

--[[
Download("http://g.tbcdn.cn/ju/lua/1.2.0/shake.js","shake.wav",^(data){
	print( data );
	player = AudioPlayer("shake.wav");
});
]]

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


