
scrW, scrH = System.screenSize();
window.backgroundColor(0x777777,1);

w = scrW/3;
X0 = scrW/10;
Y0 = scrH/4;




imageView = Image();
imageView.frame(X0, Y0, w, w);
imageView.backgroundColor(0xff0000,1);
names = {};
for i=1,16 do
	names[i] = "" .. i .. ".tiff"
end
imageView.image("16.tiff");
imageView.startAnimating( names, 3.0, 1);



imageView2 = Image();
imageView2.frame(X0+w+X0, Y0, w, w);
imageView2.image("http://g.alicdn.com/ju/lua/2.0.24/doc/icon.png",
    function()
		print("图片加载成功!");
	end);
