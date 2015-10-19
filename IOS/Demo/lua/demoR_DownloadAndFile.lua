
w,h = System.screenSize();


imageUrl = "http://g.alicdn.com/ju/lua/2.0.24/doc/icon.png"


Download( imageUrl,"baidu.png",^(data){
		print( data );
		--数据流写到文件中
		File:save("demo.png",data);
        print(PathOfResource("demo.png"));
        --文件创建图片
		imageView = Image();
		imageView.image("demo.png");
		imageView.frame(0,0,128,128);

		--内容流创建图片
		imageView2 = Image();
		imageView2.image(data);
		imageView2.frame(128,0,128,128);

		--读取文件流创建文件
		imageView3 = Image();
		imageView3.image(File:read("demo.png"));
		imageView3.frame(128*2,0,128,128);
	});
