
print("lft页面脚本");
RED_COLOR = 0xf72862;
SOLD_COUNT_COLOR = 0xFF6B14;
BLUE_COLOR = 0x26a96d;
IMAGE_BG_COLOR = 0xF5F5F5;
TITLE_COLOR = 0x333333;
NOTICE_CELL_H = 40;


local scrW,scrH = window.size();
scrH = scrH - 64;

window.setUserInteractionEnabled(true);
window.setBackgroundColor(0xffFFFF,1);

optionItemsArray = {};

-- 获取option对应的坑位model队列数据
function getOptionModel( i, section, row )
    local items  = nil
    local o = options[i];
    if ( o ) {
        local extend = o.extend;
        if ( extend ) {
            items = extend.syncOutput;
        }
    }
    if ( items ) {
        return items[row+1];
    }
    return nil;
end

-- 坑位option对应的model数量
function getOptionItemsNum( i, section )
    local items  = nil
    local o = options[i];
    if ( o ) {
        local extend = o.extend;
        if ( extend ) {
            items = extend.syncOutput;
        }
    }
    if ( items ) {
        return table:getn(items);
    }
    return 0;
end

-- 十点上新提示字符信息
function getOptionTheBrandMind( i)
    local theBrandMind  = nil;
    local o = options[i];
    if ( o ) {
        local extend = o.extend;
        if ( extend ) {
            theBrandMind = extend.theBrandMind;
        }
    }
    return theBrandMind;
end

-- 创建一个collectionView界面
function createCollectionView( option,i )
    local collectionView = UICollectionView();
    collectionView.delegate = {
		Section = {
			NumberOfSections = 3,

			NumberOfRows = ^(section){
                if ( section == 0 ){
                    -- 广告栏
                    return 0;
                } else if( section==1 ) {
                    -- 十点上新提示
                    local theBrandMind = getOptionTheBrandMind(i);
                    if( theBrandMind ) {
                        return 1; 
                    } else {
                        return 0;
                    }
                }
                -- 品牌团坑位
				return getOptionItemsNum(i, section);
			},
			Spacing = ^(section ){
				return 0;
			},
			EdgeInsets = ^(section ){
				return 0,0,0,0;
			}

		},
		Cell = { 
			Identifier = function ( section, row ){
                if( section == 0 ) {
                    return "广告栏";
                } else if( section == 1 ) {
                    return "十点上新";
                } else {
                    return "品牌团";
                }
			},
			Size = ^(section, row){
                if ( section == 0 ) {
                    return scrW , NOTICE_CELL_H;
                }
                if ( section == 1 ) {
                    return scrW , NOTICE_CELL_H;
                }
				return scrW , (178 * scrW / 375);
			},
			Select = ^(section, row){
				print(section, row);
				System.gc();
			}
		},
		"品牌团" = {
			Init = ^(cell){
				BrandCellInit(cell);
				cell.loading = UIActivityIndicatorView();
			},
			Layout = ^(cell , section, row){
				local model = getOptionModel( i, section, row);
				if ( model ) {
					BrandCellLayout(cell, model );
				}
				cell.loading.startAnimating();
				cell.loading.center(160,50);
			}
		},
		"十点上新" = {
			Init = ^(cell){
                local cellW, cellH = window.size();

                cell.line = UILabel();
                cell.line.setFrame(0,0,scrW-20,1);
                cell.line.center(cellW/2, cellH/2);
                cell.line.setBackgroundColor(0x333333);

				cell.label = UILabel();
				cell.label.setFrame(0, 0, cellW, cellH);
                cell.label.setFont(14);
                cell.label.setTextAlignment(TextAlignmentCenter);
                window.setBackgroundColor(0xeeeeee);
                cell.label.setBackgroundColor(0xeeeeee);
			},
			Layout = ^(cell , section, row){
                local cellW, cellH = window.size();
                local theBrandMind = getOptionTheBrandMind(i);
                cell.label.setText(theBrandMind );
                cell.label.sizeToFit();
                local w,h = cell.label.size();
                w = w + 20*2;
                cell.label.size(w,h);
                cell.label.center(cellW/2, cellH/2);
			}
		},
        HeaderBeginRefreshing = ^(){
            print("HeaderRefresh");
            option.requestItems(^(){
                    local collectionView = tableViewArray[i];
                    collectionView.reloadData();
                    collectionView.headerEndRefreshing();
                });
        },
        FooterBeginRefreshing = ^(){
            print("FooterRefresh");
        }
	};
    collectionView.setMiniSpacing(5);
	local barH = 40;
	collectionView.setFrame( (i-1)*scrW, barH, scrW, scrH-barH);
    collectionView.backgroundColor(0xeeeeee);
    collectionView.initRefreshHeader();

    option.loadingView = LoadingView();
    option.loadingView.setFrame(0,0,scrW, scrH);
    option.loadingView.removeFromSuperView();

    -- 显示请稍等界面
    function option.showLoadingView()
        if ( self.loadingView ) {
            collectionView.addSubview(self.loadingView);
        }
    end
    -- 移除请稍等界面
    function option.hiddenLoadingView()
        if( self.loadingView ) {
            self.loadingView.removeFromSuperView();
            if ( self.haveItems ) {
                self.loadingView = nil;
            }
        }
    end


    option.errorView = ErrorView();
    option.errorView.setFrame(0,0,scrW, scrH);
    option.errorView.removeFromSuperView();
    
    option.errorView.delegate = ^(){
        updateOptionIfEmpty(option);
    };
    -- 显示请稍等界面
    function option.showErrorView()
        if ( self.errorView ) {
            collectionView.addSubview(self.errorView);
        }
    end
    -- 移除请稍等界面
    function option.hiddenErrorView()
        if( self.errorView ) {
            self.errorView.removeFromSuperView();
        }
    end
    collectionView.contentInset(0,0,50,0);
    collectionView.scrollIndicatorInsets(0,0,50,0);
	return collectionView;
end

run("categoryBar");
run("categoryNet");


function updateOptionIfEmpty( option )
    if( option ) {
        option.requestIfNoItems( ^(){
                local index = option.index;
                local tv = tableViewArray[index];
                tv.reloadData();
                tv.headerEndRefreshing();

                if ( option.haveItems ) {
                    option.hiddenLoadingView();
                    option.hiddenErrorView();
                } else {
                    option.hiddenLoadingView();
                    option.showErrorView();
                }
            }
        );
    }
end

tableViewArray = {};

-- 量贩团 总得初始化入口
function lftMain()
	window.removeAllSubviews();

	bg = UIScrollView();
	bg.setFrame( 0, 0, scrW, scrH );
	bg.setPageEnable(true);
	bg.delegate = {
		Scrolling = ^(){
			-- print("scrooll");
			local x,y = bg.contentOffset();
			bar.renewContentScrollOffset(x,y);

            --节约内存
            local index = (x+scrW/2) /scrW+1;
            for( i=1; table:getn(tableViewArray); 1) {
                local distance = math.abs(i-index);
                local collView = tableViewArray[i];
                if ( collView ) {
                    if ( distance <=1.5 ) {
                        bg.addSubview(collView);
                    } else {
                        collView.removeFromSuperView();
                    }
                }
            }
		},
		EndScrolling = ^(){
			bar.selectedButtonCenter();

			-- 预加载判断
            local x,y = bg.contentOffset();
            local index = math.modf(x/scrW+1);
            local option = options[index];
            local optionLeft = options[index-1];
            local optionRight = options[index+1];
            updateOptionIfEmpty(option);
            updateOptionIfEmpty(optionLeft);
            updateOptionIfEmpty(optionRight);
		}
	};



	optionNum = table:getn(options);

	for (i=1; optionNum; 1) {
		-- 创建 每个option对应的collectionView
        local option = options[i];
		local collView = createCollectionView(option,i);
		tableViewArray[i] = collView;
		bg.addSubview(collView);
		option.collectionView = collView;
	}

	bar = createCategoryBar(options);
	bar.renewContentScrollOffset(0,0);

	bg.setContentSize(scrW*optionNum, scrH);

	function bar.callbackWhenPressedAtIndex( index ) {
		bg.setContentOffset( scrW*(index-1), 0, true);
	}
    function bar.callbackOfSearchButton()
        print("search button pressed ");
    end
    updateOptionIfEmpty(options[2]);
    bg.showScrollIndicator(false,false);

    hiddenOptionLoadingView();
end

mtopOptionsApiCallback = lftMain;

optionLoadingView = LoadingView();
optionLoadingView.setFrame(0,0,scrW, scrH);

function showOptionLoadingView()
    window.addSubview(optionLoadingView);
end

function hiddenOptionLoadingView()
    optionLoadingView.removeFromSuperView();
end



optionErrorView = ErrorView();
optionErrorView.setFrame( 0, 0, scrW, scrH );
optionErrorView.delegate = ^(){
    hiddenOptionErrorView();
    requestOptionListInfo();
};


function showOptionErrorView()
    hiddenOptionLoadingView();
    window.addSubview(optionErrorView);
end

function hiddenOptionErrorView()
    optionErrorView.removeFromSuperView();
end

hiddenOptionErrorView();

-------------------------


ITEM_STATUS_WILL_BEGIN = "0";
ITEM_STATUS_SELLING = "1";
ITEM_STATUS_SOLDOUT = "3";
ITEM_STATUS_END = "4";


function setShadow( view )
    view.setShadowPath();
    view.setBackgroundColor(0xffffff)
    view.setMasksToBounds(false);
    view.setShadowOffset(0,0.4);
    view.setShadowRadius(0.5);
    view.setShadowOpacity(0.17);
    view.setShadowColor(0x00000);
end

-- 品牌团 窄坑位(商品列表 坑位的品牌团)
function BrandCellInit(cell , identifier, model)
    local x0, y0, cellW, cellH = window.frame();
    ---  构造内容
    if (cell.pic==nil ) then
        cell.bg = UIImageView();
        cell.bg.setFrame(0,0,cellW,cellH);
        setShadow(cell.bg);

        cell.pic = UIImageView();
        cell.pic.setFrame(0,0,cellW,cellH-28);
        cell.pic.setClipsToBounds(true);

        cell.blur = UIImageView();
        cell.blur.setFrame(0,cellH-28,cellW,28);
        cell.blur.setContentMode(UIViewContentModeScaleToFill);
        cell.blur.resizeImage(0,0,4,0);

        cell.promotion = UILabel();
        cell.promotion.setFont(10);
        cell.promotion.setTextColor(0xffffff);
        cell.promotion.setBackgroundColor(0xFF6182);
        cell.promotion.setCornerRadius(2);
        cell.promotion.setClipsToBounds(true);


        cell.soldCount = UILabel();
        cell.soldCount.setFrame(0,0,15,15);


        cell.bizTag = UIImageView();
    end
end

function BrandCellLayout(cell , model)
    local x0, y0, cellW, cellH = window.frame();
    local  baseInfo  =  model.baseInfo;
    local  materials  =  model.materials;
    local  remind  =  model.remind;
    local  price  =  model.price;
    local url = materials.newBrandEnterImgUrl;
    --url = TaobaoTFS.getImageWithPath_withSize(url, SizeInfo(cellW,cellH) );
    cell.pic.setImage( url );

    ---促销信息
    promotion = price.promotion;
    if( IsString(promotion) and promotion.len()>0 ) then
        promotion = "" .. promotion .. " ";
        --cell.promotion0.setText("");--"促"
        local att = AttributedString( promotion,  { fontSize=14, foregroundColor=0xffffff, fontWeight="bold" } );
        cell.promotion.setText(att);
        cell.promotion.sizeToFit();

        local x,y,w,h = cell.promotion.frame();
        h = 35/2;
        cell.promotion.setFrame(8,cellH-6-h,w+4,h+1);
        --cell.promotionAll.setFrame(cellW-w-15-20,cellH-45,w+15,h);
        cell.promotion.setHidden(false);
    else
        cell.promotion.setHidden(true);
    end


    --已经售卖
    local activityStatus = baseInfo.activityStatus;
    if ( activityStatus==ITEM_STATUS_SELLING ) then
        local soldCount = remind.soldCount;
        local timeRemind = remind.timeRemind;
        local att0 = AttributedString( ""..soldCount,  { fontSize=12, foregroundColor=RED_COLOR, fontWeight="bold" } );
        local att1 = AttributedString( "件已售  ",  { fontSize=12, foregroundColor=TITLE_COLOR, fontWeight="bold" } );
        local att2 = AttributedString( timeRemind,  { fontSize=12, foregroundColor=RED_COLOR, fontWeight="bold" } );
        if( soldCount+0 > 0 ) then 
            cell.soldCount.setText(att0 + att1 + att2);
        else
            cell.soldCount.setText(att2);
        end
    else
        local remindNum = remind.remindNum;
        local timeRemind = remind.timeRemind;
        local att0 = AttributedString( ""..remindNum,  { fontSize=12, foregroundColor=BLUE_COLOR, fontWeight="bold" } );
        local att1 = AttributedString( "人想买  ",  { fontSize=12, foregroundColor=TITLE_COLOR, fontWeight="bold" } );
        local att2 = AttributedString( timeRemind,  { fontSize=12, foregroundColor=BLUE_COLOR, fontWeight="bold" } );
        if ( remindNum+0>0 ) {
            cell.soldCount.setText(att0 + att1 + att2);
        } else {
            cell.soldCount.setText(att2);
        }
    end
    cell.soldCount.sizeToFit();

    local x,y,w,h = cell.soldCount.frame();
    cell.soldCount.setFrame(cellW - w - 20 , cellH - 7 - h,w,h);
end


titleView = UILabel();
titleView.setText("品牌团");
titleView.setFont(20);
titleView.setTextColor(RED_COLOR);
titleView.sizeToFit();
Navigation.setTitle(titleView);

requestOptionListInfo();

    -- -- 业务标
    -- local bizTag = extend.bizTag();
    -- --bizTag = "http://gtms03.alicdn.com/tps/i3/TB1L3FiHVXXXXcWXpXX17RX.XXX-120-50.png";
    -- if( IsString(bizTag) and bizTag.len()>0 ) then 
    --     bizTag = TaobaoTFS.getImageWithPath_withSize(bizTag,"200x200");
    --     cell.bizTag.setHidden(true);
    --     cell.bizTag.setImage(bizTag, ^(){
    --                 cell.bizTag.sizeToFit();
    --                 local x,y,w,h = cell.bizTag.frame();
    --                 if( h>0 ) then
    --                     local H0 = 25;
    --                     w = w/h * H0;
    --                     h = H0;
    --                     cell.bizTag.setFrame(0,0,w,h);
    --                     cell.bizTag.setHidden(false);
    --                 else
    --                     cell.bizTag.setHidden(true);
    --                 end
    --         });
    -- else 
    --     cell.bizTag.setHidden(true);
    -- end
