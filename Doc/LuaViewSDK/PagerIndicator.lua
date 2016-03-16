--- PagerIndicator
-- banner滚动第几页的小圆点
--
-- PagerIndicator的父类是[View](./../ui容器/View.html)
-- @classmodUIItem PagerIndicator


--- 创建一个PagerIndicator对象
-- @treturn PagerIndicator pageControl
-- @usage
-- function createBanner( items , bannerW, bannerH , title)
-- 	local banner = {};
-- 	banner.title = title;
-- 	banner.num = table:getn(items);
-- 	local scrW, scrH = System.screenSize();
--     function banner.removeFromSuper()
--     	if( self.pagerView ) then
-- 	    	self.pagerView.removeFromSuper();
-- 	    	self.pagerView = nil;
-- 	    end
-- 	    if( self.pageIndicator ) then
-- 	    	self.pageIndicator.removeFromSuper();
-- 	    	self.pageIndicator = nil;
-- 	    end
-- 	    if( self.timer ) then
-- 	    	self.timer.stop();
-- 	    end
--     end
-- 	banner.buttons = {};
-- 	banner.num = 0;
-- 	banner.callbackTimes = 0;
-- 	banner.index0 = 0;
-- 	banner.moveRight = true;
-- 	banner.timer = Timer(function()
-- 			if( banner.num<=1 ) then
-- 				return;
-- 			end
-- 			local currentPage = banner.pagerView.currentPage();
-- 			if (  currentPage == 1 ) then
-- 				banner.moveRight = true
-- 			end
-- 			if (  currentPage == banner.num ) then
-- 				banner.moveRight = false
-- 			end
-- 			if( banner.callbackTimes > 1) then
-- 				if( banner.moveRight ) then
-- 					banner.pagerView.currentPage(currentPage+1);
-- 				else
-- 					banner.pagerView.currentPage(currentPage-1);
-- 				end
-- 				banner.callbackTimes = 0;
-- 			else
-- 				banner.callbackTimes = banner.callbackTimes+1;
-- 			end
-- 		end);
-- 	banner.timer.start(1,true);
-- 	function banner.setItems( items )
-- 		self.items = items;
-- 		self.num = table:getn(items);
-- 		-- print(self.num);
-- 		if( self.pagerView== nil ) then 
-- 			self.pagerView = PagerView{
-- 				PageCount =  function ()
-- 					return self.num;
-- 				end,
-- 				Pages = {
-- 					Init = function (page,pos)
-- 						local i = pos
-- 						local button = Image();
-- 						local item = items[i];
-- 						local imageUrl = item.img;
-- 						button.image( urlAddHttpWithSize(imageUrl) );
-- 						button.callback( function()
-- 							print(item.href);
-- 			                -- 埋点
-- 				            local dic = {
-- 				            	type="JU_PPT",
-- 				                pos = i-1,
-- 				                url = item.href,
-- 				                title = self.title};
-- 				            native_ut("PPT_BTN_BANNER" , dic );
-- 				            local url = item.href;
-- 						    local index = url.find("http");
-- 						    if( index~=1 ) then
-- 						        url = "https:" .. url;
-- 						    end
-- 			                native_openUrl(url);
-- 						end);
-- 						page.button = button;
-- 					end,
-- 					Layout = function (page, pos)
-- 						page.button.frame(0,0,scrW,bannerH);
-- 					end
-- 				},
-- 				Callback = {
-- 					Scrolling = function()
-- 						banner.callbackTimes = 0;
-- 					end,
-- 					ScrllEnd = function()
-- 					end
-- 				}
-- 			};
-- 			self.pagerView.backgroundColor(0xffFFFF);
-- 			self.pagerView.frame(0, 0, scrW, bannerH );
-- 			self.pageIndicator = PagerIndicator();
-- 			self.pageIndicator.frame(0,bannerH - 10, scrW, 10);
-- 			self.pagerView.indicator( self.pageIndicator );
-- 		else
-- 			self.pagerView.reload();
-- 			self.pageIndicator.frame(0,bannerH - 10, scrW, 10);
-- 		end
-- 	end
-- 	if ( items ) then
-- 		banner.setItems(items);
-- 	end
-- 	return banner;
-- end
function PagerIndicator()
end


-- --- 设置总页数
-- -- @int num
-- function pageCount()
-- end
-- --- 获取总页数
-- -- @treturn int num
-- function pageCount()
-- end

--- 设置当前选中页
-- @int page 
function currentPage()
end

--- 获取当前选中页
-- @treturn int page
function currentPage()
end

--- 设置未选中页的颜色
-- @int color
-- @number alpha
function pageColor()
end

--- 获取未选中页的颜色
-- @treturn int color
-- @treturn number alpha
function pageColor()
end


--- 设置选中页的颜色
-- @int color
-- @number alpha
function currentPageColor()
end

--- 获取选中页的颜色
-- @treturn int color
-- @treturn number alpha
function currentPageColor()
end


