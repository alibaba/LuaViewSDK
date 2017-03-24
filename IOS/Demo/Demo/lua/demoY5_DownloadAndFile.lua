
scrW,scrH = System:screenSize();


imageUrl = "http://g.alicdn.com/ju/lua/2.0.24/doc/icon.png"

IMG_W = scrW/3;
Download( imageUrl,"baidu.png",
	function (data)
		print( data );
		--数据流写到文件中
		File.save("demo.png",data);
        print(PathOfResource("demo.png"));
        --文件创建图片
		imageView = Image();
		imageView:image("demo.png");
		imageView:frame(0,0,IMG_W,IMG_W);

		--内容流创建图片
		imageView2 = Image();
		imageView2:image(data);
		imageView2:frame(IMG_W,0,IMG_W,IMG_W);

		--读取文件流创建文件
		imageView3 = Image();
		imageView3:image(File.read("demo.png"));
		imageView3:frame(IMG_W*2,0,IMG_W,IMG_W);
	end);
