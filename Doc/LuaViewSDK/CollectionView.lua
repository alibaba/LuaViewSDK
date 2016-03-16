--- CollectionView
-- 类似于Cell大小任意的TableView
--
-- CollectionView的父类是[HScrollView](./HScrollView.html)
-- @classmod CollectionView


--- 通过这个方法创建一个CollectionView对象
-- 设置Collection代理
-- @tparam table dataSource 数据源
-- @usage  -- 例子二:
-- w,h = System.screenSize();
-- window.backgroundColor(0x000000,1);
-- cellHeight = 100
-- imageUrl1 = "http://gju2.alicdn.com/bao/uploaded/i1/10000073270926575/TB2fpg0cXXXXXb6XpXXXXXXXXXX_!!0-0-juitemmedia.jpg"
-- imageUrl2 = "http://img4.duitang.com/uploads/item/201306/25/20130625045508_sairr.thumb.600_0.jpeg"
-- collectionView = nil;
-- collectionView = RefreshCollectionView {
-- 	Section = {
-- 		SectionCount = 2,
-- 		RowCount = function(section)
-- 			return 10;
-- 		end
-- 	},
-- 	Cell = {
-- 		Id = function ( section, row )
-- 			if( section%2==1 ) then
-- 				return "ImageAndLabel";
-- 			else
-- 				return "ImageAndLabel2";
-- 			end
-- 		end,
-- 		ImageAndLabel = {
-- 			Size = function(section, row)
-- 				return (w-10)/2 ,200;
-- 			end,
-- 			Init = function(cell)
-- 				local cellWidth ,cellHeight = cell.window.size();
-- 				cellHeight = cellHeight / 2;
-- 				cell.icon = Image();
-- 				cell.icon.frame(0, 0, cellHeight, cellHeight);
-- 				cell.title = Label();
-- 				cell.title.frame(0, cellHeight+2, cellHeight, 40);
-- 				cell.title.textColor(0xffFFFF);
-- 				cell.title.backgroundColor(0xff00ff);
-- 				print("构造Cell--2");
-- 			end,
-- 			Layout = function(cell , section, row)
-- 				cell.icon.image(imageUrl1, function()
-- 						local x,y,w,h = cell.icon.frame();
-- 						print("dongxicheng----",x,y,w,h);
-- 					end);
-- 				cell.title.text("测试"..section .."--" .. row);
-- 				print("布局Cell--" , section, "--", row);
-- 				cell.window.backgroundColor( section*0x770000 +  (row%3)*0x33 );
-- 			end,
-- 			Callback = function(cell, section, row)
--                 print(section, row);
--                 collectionView.stopRefreshing();
-- 				System.gc();
-- 				collectionView.scrollToCell(section, row);
-- 			end
-- 		},
-- 		ImageAndLabel2 = {
-- 			Size = function(section, row)
-- 				return w ,200;
-- 			end,
-- 			Init = function(cell)
-- 				cell.icon = Image();
-- 				cell.icon.frame(w*0.05, 10, cellHeight, cellHeight);
-- 				cell.button = Button();
-- 				cell.button.frame(0,0,100,60);
-- 				cell.button.backgroundColor(0x777777);
-- 				cell.button.callback( 
-- 					function()
-- 							Animate( function()
-- 											cell.icon2.center(160,100);
-- 									 end
-- 								    ) ;
-- 					end);
-- 				cell.icon2 = Image();
-- 				cell.icon2.frame(160, 0, cellHeight, cellHeight);
-- 				print("构造Cell--2");
-- 			end,
-- 			Layout = function(cell , section, row)
-- 				cell.icon.image(
-- 					imageUrl1, 
-- 					function() 
-- 						local x,y,w,h = cell.icon.frame();
-- 						print("dongxicheng----",x,y,w,h);
-- 					end);
-- 				cell.icon2.image(imageUrl1)
-- 				print("布局Cell--" , section, "--", row);
-- 				cell.window.backgroundColor( section*0x770000 +  (row%3)*0x33 );
-- 			end,
-- 			Callback = function(cell, section, row)
-- 				print(section, row);
--                 collectionView.stopRefreshing();
-- 				System.gc();
-- 				collectionView.scrollToCell(section, row);
-- 			end
-- 		}
-- 	},
-- 	Callback = {
-- 			Scrolling = function( firstVisibleSection, firstVisibleRow, visibleCellCount )
-- 				print("scrolling", firstVisibleSection,"---" ,firstVisibleRow, "---", visibleCellCount);
-- 			end,
-- 			ScrollBegin = function(firstVisibleSection, firstVisibleRow, visibleCellCount )
-- 				print("scrolling begin", firstVisibleSection,"---" ,firstVisibleRow, "---", visibleCellCount);
-- 			end,
-- 			ScrollEnd = function(firstVisibleSection, firstVisibleRow, visibleCellCount )
-- 				print("scrolling end", firstVisibleSection,"---" ,firstVisibleRow, "---", visibleCellCount);
-- 			end
-- 	}
-- };
-- collectionView.frame(0,0,w,h-64);
-- collectionView.backgroundColor(0xffFFFF);
-- collectionView.miniSpacing(10);
--   
function CollectionView()
end

--    
--- 重新加载刷新CollectionView
function reload()
end

--- 设置最小间距
-- @number miniSpacing 最小间距
function miniSpacing()
end

--- 获取最小间距
-- @treturn number miniSpacing 最小间距
function miniSpacing()
end

--- 滚动到指定的cell
-- @number section section
-- @number row row
-- @number offset 距离顶部的offset
-- @bool animation 是否开启滚动动画
function scrollToCell()
end

--- 滚动到顶部
function scrollToTop()
end




