
scrW, scrH = System.screenSize();
window.backgroundColor(0x777777,1);

w = scrW/3;
X0 = scrW/10;
Y0 = scrH/5;




imageView = Image();
imageView.frame(X0, Y0, w, w);
imageView.backgroundColor(0xff0000,1);
names = {};

for i=1,16 do
	names[i] = "img" .. i .. ".tiff"
end
imageView.image("img16.tiff");
imageView.startAnimationImages( names, 3.0, 1000);


label = Label();

imageView2 = Image();
imageView2.frame(X0+w+X0, Y0, w, w);
imageView2.image("http://g.alicdn.com/ju/lua/2.0.24/doc/icon.png",
    function(info)
		print("图片加载成功!", info);
        label.text("图片加载成功!" )
        label.adjustSize();
        label.center(scrW/2,scrH/2);
	end);

imageView3 = Image();
imageView3.frame(0,Y0+w+Y0,w,w);
imageView3.image("bt_catebrand_more.9.png");

imageView4 = Image();
imageView4.backgroundColor(0xff00FF)
imageView4.frame(150,300,200,200);
imageView4.image("jhs.png");
local saleType = 0;
imageView4.callback(function ()
	imageView4.scaleType(saleType)
	saleType = saleType + 1;
end)

label.backgroundColor(0xFF);



view = View();
view.size(100,100);

view.backgroundColor(1)
print(view.backgroundColor())

view.backgroundColor(0x88000001)
print(view.backgroundColor())

view.backgroundColor(0x000001, 0.5)
print(view.backgroundColor())

view.backgroundColor(0xff000001, 0.5)
print(view.backgroundColor())

closeButton = Button();
closeButton.callback(function ()
window.release();
end);

closeButton.title("关闭");
closeButton.frame(0,0,100,100);

