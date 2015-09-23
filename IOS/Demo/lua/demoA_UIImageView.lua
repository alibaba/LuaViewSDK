

print("初始化窗口大小, 默认窗口大小是(0,0)");
w,h = System.screenSize();
window.setFrame(0, 0, w, h);
window.setUserInteractionEnabled(true);
window.setBackgroundColor(0x777777,1);

Y0 = 100

imageView = UIImageView();
imageView.setFrame(30, Y0, 100, 100);
imageView.setBackgroundColor(0xff0000,1);
names = {};
for i=1,16 do
	names[i] = "" .. i .. ".tiff"
end
imageView.setImage("16.tiff");
imageView.startAnimating( names, 3.0, 1);



imageView2 = UIImageView();
imageView2.setFrame(160, Y0, 100, 100);
imageView2.setImage("http://g.alicdn.com/ju/lua/2.0.24/doc/icon.png");

