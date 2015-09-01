
CATEGORY_BUTTON_DX = 24;
RED_POINT_DH = 2;
BAR_H = 40;

function createCategoryBar( optionArr ) {
	local bar = { selectedIndex = 0 , visibleIndex = 0};
	local scrW, scrH = System.screenSize();
	bar.background = UIView();
	bar.background.setFrame(0, 0, scrW, BAR_H);

	bar.searchButton = UIButton();
	bar.searchButton.setFrame(scrW-BAR_H,0,BAR_H,BAR_H);
	bar.searchButton.setFont("JHSIconfont",18);
	bar.searchButton.setText( Unicode(0xe607) ); 
	bar.searchButton.setBackgroundColor(0xffffff,0.99);

	bar.buttonShardow = UIImageView();
	bar.buttonShardow.setFrame(scrW-BAR_H-7.5, 0, 7.5,BAR_H);
	bar.buttonShardow.setImage("bg_home_category_right_shadow");

	bar.bg = UIScrollView();
	bar.bg.setFrame( 0, 0, scrW, BAR_H);
	bar.bg.setBackgroundColor(0xffffff,1);
	bar.background.addSubview(bar.bg);

	bar.buttons = {};
	bar.buttons2 = {};

	bar.redPoint = UIView();
	bar.redPoint.setFrame(0,0,20,RED_POINT_DH);
	bar.redPoint.setBackgroundColor(RED_COLOR);
	bar.bg.addSubview(bar.redPoint);

	function bar.callbackWhenPressedAtIndex( index )
		
	end

	function bar.callbackOfSearchButton()
		
	end

	bar.searchButton.delegate = ^(){
		bar.callbackOfSearchButton();
	};

	function bar.selectedOption(){

	};
	function bar.selectedButtonCenter()
		local  index = self.selectedIndex;
		if ( index>=1 and index<=table:getn(self.buttons) and bar.visibleIndex~=index ) {
			local btn = self.buttons[index];
			local x,y,buttonW,buttonH = btn.frame();
			local dx = (scrW - buttonW)/2;
			bar.bg.scrollRectToVisible( x-dx, y, scrW, buttonH, true);
			bar.visibleIndex = index;
		}
	end
	function bar.selectedButtonVisible()
		local  index = self.selectedIndex;
		if ( index>=1 and index<=table:getn(self.buttons) and bar.visibleIndex~=index ) {
			local btn = self.buttons[index];
			local x,y,buttonW,buttonH = btn.frame();
			x = x - BAR_H;
			buttonW = buttonW + BAR_H + BAR_H;
			bar.bg.scrollRectToVisible( x, y, buttonW, buttonH, true);
			bar.visibleIndex = index;
		}
	end
	function bar.setSelectIndex( index )
		if ( self.selectedIndex != index ) {
			self.selectedIndex = index;
		}
	end

	function bar.getCenterXbyIndex( index ){
		local num = table:getn(self.buttons);
	    if( num<=0 ){
	        return 0;
	    }
	    local dx = 40;
	    if( index<1 ){
	        local button = self.buttons[1];
	        if ( button ) {
	        	local x,y = button.center();
	        	return x - dx;
	    	} else {
	    		return 0;
	    	}
	    }
	    if( index>=1 and index<=num ){
	        local button = self.buttons[index];
	        if ( button ) {
	        	local x,y = button.center();
	        	return x;
	        }
	    }
	    local button = self.buttons[num];
	    local x,y = button.center();
	    return x + dx;
	}

	function bar.getButtonWidthbyIndex( index ){
		local num = table:getn(self.buttons);
	    if( num<=0 ){
	        return 0;
	    }
	    local dx = 40;
	    if( index<1 ){
	        local button = self.buttons[1];
	        local w,h = button.size();
	        return w-CATEGORY_BUTTON_DX;
	    }
	    if( index>=1 and index<=num ){
	        local button = self.buttons[index];
	        if ( button ) {
	        	local w,h = button.size();
	        	return w-CATEGORY_BUTTON_DX;
	        }
	    }
	    local button = self.buttons[num];
		local w,h = button.size();
	    return w-CATEGORY_BUTTON_DX;
	}

	function bar.renewContentScrollOffset( x, y ){
		-- print("x = ",x,", y= ", y);
		local scrW, scrH = window.size();
		local step = scrW;

        local floatIndex = x/step + 1;
        local indexIntPart, indexFloatPart = math.modf( floatIndex );
        {
            for( i=1 ;table:getn(self.buttons);1 ){
            	local btn = self.buttons[i];
            	local btn2 = self.buttons2[i];
                local a = math.abs( i - floatIndex);
                if ( a>1 ) {
                	a = 1;
                }
                btn.alpha = a;
                btn2.alpha = math.abs(1-a);
            }
        }
        self.setSelectIndex( math.modf( floatIndex +0.5) );
        {-- 红色毛毛虫 如果接近左右就需要滚动
        	local x,y,w,h = self.redPoint.frame();
        	local offsetX, offsetY = self.bg.contentOffset();
        	if( (x<offsetX+BAR_H)  or (x+w > offsetX+scrW - BAR_H*1.5) ){
        		self.selectedButtonCenter();
        	}
    	}
	    {   
	        local center0X = self.getCenterXbyIndex(indexIntPart);
	        local center1X = self.getCenterXbyIndex(indexIntPart+1);
	        local centerX = center0X + (center1X-center0X)*indexFloatPart;
	        
	        local w0 = self.getButtonWidthbyIndex(indexIntPart);
	        local w1 = self.getButtonWidthbyIndex(indexIntPart+1);
	        local w = w0 + (w1-w0)*indexFloatPart;
	        
	        local dw = math.abs(indexFloatPart);
	        if( dw <=0.5 ){
	        } else {
	            dw = 1-dw;
	        }
	        dw =  dw/0.5 * CATEGORY_BUTTON_DX*1.5;
	        
	        w = w + dw;
	        {
	            {
	                local x,y,redPointW,redPointH = self.redPoint.frame();
	                redPointW = w;
	                self.redPoint.setFrame( x, y, redPointW, redPointH);
	            }
	            self.redPoint.center = CGPoint(centerX, BAR_H-RED_POINT_DH/2);
	            self.redPoint.alpha = 1;
	        }
	    }
	};
	
	local x0 = 0;
	for( i=1; table:getn(optionArr) ; 1 ) {
		//选中按钮
		local option = optionArr[i];
		print( option.displayName );
		local button = UIButton();
		button.setText( option.displayName );

		-- size
		button.sizeToFit();
		local x,y,buttonW, buttonH = button.frame();
		button.setFrame(x,y,buttonW+ CATEGORY_BUTTON_DX, buttonH);
		x,y,buttonW, buttonH = button.frame();

		button.setCenter( x0+buttonW/2, BAR_H/2 );
		bar.buttons[i] = button;
		bar.bg.addSubview(button);
		x0 = x0 + buttonW;
		button.setBackgroundColor(0xFFFFFF);
		button.setTitleColor(TITLE_COLOR);

		-- 按钮2
		local button2 = UIButton();
		button2.setFrame( button.frame() );
		button2.setText(option.displayName);
		button2.setBackgroundColor(0xFFFFFF);
		bar.buttons2[i] = button2;
		bar.bg.addSubview(button2);
		

		x,y,buttonW, buttonH = button.frame();
		button.delegate = ^(){
			bar.setSelectIndex(i);
			bar.selectedButtonCenter();
			bar.callbackWhenPressedAtIndex(i);
			-- local dx = (scrW - buttonW)/2;
			-- bar.bg.scrollRectToVisible( x-dx, y, scrW, buttonH, true);
		};
		button2.delegate = button.delegate();
		button2.setTitleColor(RED_COLOR);
	}
	bar.bg.setContentSize( x0 + BAR_H + 10, BAR_H );
	bar.bg.setAlwaysBounce(false, true);
	bar.bg.showScrollIndicator(false, false);
	return bar;
}


