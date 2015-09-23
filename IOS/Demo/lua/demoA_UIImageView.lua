

print("初始化窗口大小, 默认窗口大小是(0,0)");
w,h = System.screenSize();
window.setFrame(0, 0, w, h);
window.setUserInteractionEnabled(true);
window.setBackgroundColor(0xffffff,1);

imageView = UIImageView();
imageView.setFrame(0,30,100,100);
imageView.setBackgroundColor(0xff0000,1);
names = {};
for i=1,16 do
	names[i] = "" .. i .. ".tiff"
end
imageView.setImage("16.tiff");
imageView.startAnimating( names, 3.0, 1);