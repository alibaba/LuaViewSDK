
bitmap = Bitmap("http://gw.alicdn.com/tps/TB1qMx0KFXXXXXSXXXXXXXXXXXX-1065-765.png",
	function ()
		view = Image();
		view:image(bitmap);
		view:frame(64,64,100,100);

		local bp = bitmap:sprite(10,10,200,200,function (bit)
			
			view3 = Image();
			view3:image(bit);
			view3:frame(64,270,100,100);
		end);
		view2 = Image();
		view2:image(bp);
		view2:frame(64,170,100,100);
	end)

