


print("初始化窗口大小, 默认窗口大小是(0,0)");
w,h = System.screenSize();
window.setFrame(0, 0, w, h);
window.setUserInteractionEnabled(true);
window.setBackgroundColor(0xffffff,1);





function createBanner( items )
	local banner = {};
	local imageH = 100;
	banner.num = table:getn(items);
	local scrW, scrH = System.screenSize();
	banner.scrollView = UIScrollView();
	banner.scrollView.setBackgroundColor(0xffFFFF);
	banner.scrollView.setFrame(0, 0, scrW, imageH );
	banner.scrollView.setPageEnable(true);

	banner.buttons = {};
	banner.num = 0;

	banner.pageControl = UIPageControl();
	-- banner.pageControl.currentPageIndicatorTintColor(0xff0000,0.8);
	banner.pageControl.center( scrW/2, imageH - 5 );
	banner.callbackTimes = 0;
	banner.index0 = 0;
	banner.timer = Timer(^(){
			if( banner.num<=1 ) {
				return;
			}
			if( banner.callbackTimes > 1) {
				local x = banner.scrollView.contentOffset();
				-- local contentW = banner.scrollView.contentSize();
				banner.scrollView.contentOffset( x+scrW, 0, true);
			} else {
				banner.callbackTimes = banner.callbackTimes+1;
			}
		});
	banner.timer.start(1,true);

	function banner.setItems( items )
		self.scrollView.removeAllSubviews();
		self.num = table:getn(items);
		banner.pageControl.numberOfPages(self.num);
		local contentNum = self.num;
		if( contentNum >= 2 ) {
			contentNum = 3;
		}
		self.scrollView.setContentSize( contentNum*scrW, imageH);
		for i = 1 , self.num do  
			local button = UIButton();
			local item = items[i];
			button.setImage(item.image);
			button.setFrame( (i-1)*scrW,0,scrW,imageH);
			button.delegate = ^(){
				print(item.url);
			};
			self.buttons[i] = button;
			self.scrollView.addSubview(button);
		end  
	end

	banner.scrollView.delegate = {
		Scrolling = ^(){
			local x = banner.scrollView.contentOffset();
			if( x<=0 ) {
				x = x + scrW;
				banner.scrollView.contentOffset(x,0);
				banner.index0 = banner.index0 - 1;
			}
			if( x>= scrW*2 ) {
				x = x - scrW;
				banner.scrollView.contentOffset(x,0);
				banner.index0 = banner.index0 + 1;
			}

			banner.callbackTimes = 0;
			if ( banner.num==2 and x>scrW ) {
				for i = 1 , banner.num do 
					local index = i + banner.index0;
					index = index + banner.num *10000;
					index = index%banner.num;
					if ( index ==0 ){
						index = banner.num;
					}
					local  button = banner.buttons[index];
					button.setFrame( (3-i)*scrW,0,scrW,imageH );
				end
			} else {
				for i = 1 , banner.num do 
					local index = i + banner.index0;
					index = index + banner.num *10000;
					index = index%banner.num;
					if ( index ==0 ){
						index = banner.num;
					}
					local  button = banner.buttons[index];
					button.setFrame( (i-1)*scrW,0,scrW,imageH );
				end
			}
		},
		EndScrolling = ^(){
			local x = banner.scrollView.contentOffset();
			local index = x / scrW ;

			index = index + banner.index0;
			index = index + banner.num *10000;
			index = index%banner.num;

			banner.pageControl.currentPage(index);
			banner.callbackTimes = 0;
		}
	};
	if ( items ) {
		banner.setItems(items);
	}
	return banner;
end



Mtop( 
	  {
			method="mtop.ju.resource.banner.all",
			version="1.0",
			params = {
					    areaType = "TODAY",
					    clientType = "1",
					    clientVersion = "280",
					 },
	  },
	  ^( data, error ){
	  		local model = data.model;
	  		banner = createBanner(model)
	  }
);


