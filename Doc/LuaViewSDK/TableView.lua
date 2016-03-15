--- TableView
-- 每行高度样式可定制和重复利用的列表
--
-- TableView的父类是[ScrollView](./ScrollView.html)
-- @classmod TableView


--- 设置TableView代理
-- @tparam table source 数据源
-- @usage 
-- w,h = System.screenSize();
-- window.backgroundColor(0,1);
-- cellHeight = 100
-- imageUrl1 = "http://gju2.alicdn.com/bao/uploaded/i1/10000073270926575/TB2fpg0cXXXXXb6XpXXXXXXXXXX_!!0-0-juitemmedia.jpg"
-- imageUrl2 = "http://img4.duitang.com/uploads/item/201306/25/20130625045508_sairr.thumb.600_0.jpeg"
-- function createTableView( )
-- 	local tv = nil;
-- 	tv = TableView {
-- 		Section = {
-- 			SectionCount = 2,
-- 			RowCount = function(section)
-- 				return 10;
-- 			end
-- 		},
-- 		Cell = {
-- 			Id = function( section, row )
-- 				if ( section==1  ) then
-- 					return  "ImageAndLabel";
-- 				else
-- 					return "ImageAndImage";
-- 				end
-- 			end,
-- 			ImageAndLabel = {
-- 				Size = function( section, row )
-- 					return cellHeight;
-- 				end,
-- 				Init = function(cell, section, row)
-- 					cell.icon = Image();
-- 					cell.title = Label();
-- 					print("构造Cell");
-- 					print(cell.window);
-- 				end,
-- 				Layout = function(cell, section, row)
-- 					cell.icon.frame(0, 0, cellHeight, cellHeight);
-- 					cell.icon.image(imageUrl1);
-- 					cell.title.frame(cellHeight, 0, w-cellHeight, cellHeight);
-- 					cell.title.text("测试"..section .."--" .. row);
-- 				end,
-- 				Callback = function(cell, section, row )
-- 					print(section, row);
-- 					tv.stopRefreshing();
-- 					System.gc();
-- 				end
-- 			},
-- 			ImageAndImage = {
-- 				Size = function( section, row )
-- 					return cellHeight;
-- 				end,
-- 				Init = function(cell, section, row)
-- 					cell.icon = Image();
-- 					cell.icon2 = Image();
-- 				end,
-- 				Layout = function(cell, section, row)
-- 					print(window);
-- 					local cellWdith, cellHeight = cell.window.size();
-- 					cell.icon.frame(0, 0, cellHeight, cellHeight);
-- 					cell.icon.image(imageUrl1);
-- 					cell.icon2.frame(cellHeight+10, 0, cellHeight, cellHeight);
-- 					cell.icon2.image(imageUrl1);
-- 				end,
-- 				Callback = function( cell, section, row )
-- 					print(tv, section, row);
-- 					tv.stopRefreshing();
-- 					System.gc();
-- 					if( row%2==1 ) then
-- 						tv.scrollToCell(1,2);
-- 					else
-- 						tv.scrollToTop();
-- 					end
-- 				end
-- 			}
-- 		},
-- 		Callback = {
-- 			Scrolling = function( firstVisibleSection, firstVisibleRow, visibleCellCount )
-- 				print("scrolling", firstVisibleSection,"---" ,firstVisibleRow, "---", visibleCellCount);
-- 			end,
-- 			ScrollBegin = function(firstVisibleSection, firstVisibleRow, visibleCellCount )
-- 				print("scrolling begin", firstVisibleSection,"---" ,firstVisibleRow, "---", visibleCellCount);
-- 			end,
-- 			ScrollEnd = function(firstVisibleSection, firstVisibleRow, visibleCellCount )
-- 				print("scrolling end", firstVisibleSection,"---" ,firstVisibleRow, "---", visibleCellCount);
-- 			end
-- 		}
-- 	};
-- 	return tv;
-- end
-- tableView = createTableView();
-- tableView.dividerHeight(20);
-- loading = false;
-- tableView.frame(0,0,w,h-64);
-- tableView.backgroundColor(0xffFFFF);
-- header = Button();
-- header.title("我是头");
-- header.frame(0,0,w,100);
-- header.backgroundColor(0xff);
-- tableView.header(header);
-- footer = Button();
-- footer.title("我是结尾");
-- footer.frame(0,0,w,100);
-- footer.backgroundColor(0xff0000);
-- tableView.footer(footer);
--    
function TableView()
end

---重新刷新加载TableView
function reload()
end

---设置TableView的头
-- @View header 头view
function header()
end

---设置TableView的尾
-- @View footer 尾view
function footer()
end

--- 是否正在下拉刷新动画中
function isRefreshing()
end

--- 代码控制 开始下拉刷新动画
function startRefreshing()
end

--- 停止下拉刷新动画(数据已经加载完成后调用)
function stopRefreshing()
end





--    
-- --         HeaderHeight = ^(section){ -- section的头高
-- --             return 30;
-- --         },
-- --         Header = ^(section){ -- section对应的头
-- --             local view = Button();
-- --             view.frame(0,0,w,30);
--                view.backgroundColor(0xffff,1);
-- --             view.title("section 头:"..section);
-- --             return view;
-- --         },
-- --   
-- --         FooterHeight = ^(section){ -- section的脚高
-- --             return 30;
-- --         },
-- --         Footer = ^(section){ -- section脚View
-- --             local view = Button();
-- --             view.frame(0,0,w,30);
-- --             view.backgroundColor(0xffff,1);
-- --             view.title("section 尾:"..section);
-- --             return view;
-- --         }