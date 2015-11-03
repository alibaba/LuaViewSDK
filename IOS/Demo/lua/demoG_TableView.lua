
w,h = System.screenSize();

window.backgroundColor(0,1);

cellHeight = 100
imageUrl1 = "http://gju2.alicdn.com/bao/uploaded/i1/10000073270926575/TB2fpg0cXXXXXb6XpXXXXXXXXXX_!!0-0-juitemmedia.jpg"
imageUrl2 = "http://img4.duitang.com/uploads/item/201306/25/20130625045508_sairr.thumb.600_0.jpeg"
function createTableView( )
	local tv = nil;
	tv = TableView {
		Section = {
			SectionCount = 2,

			RowCount = function(section)
				return 10;
			end
		},

		Cell = {
			Id = function( section, row )
				if ( section==1  ) then
					return  "ImageAndLabel";
				else
					return "ImageAndImage";
				end
			end,
			ImageAndLabel = {
				Size = function( section, row )
					return cellHeight;
				end,
				Init = function(cell, section, row)
					cell.icon = Image();
					cell.title = Label();
					print("构造Cell");
					print(cell.window);
				end,
				Layout = function(cell, section, row)
					cell.icon.frame(0, 0, cellHeight, cellHeight);
					cell.icon.image(imageUrl1);

					cell.title.frame(cellHeight, 0, w-cellHeight, cellHeight);
					cell.title.text("测试"..section .."--" .. row);
				end,
				Callback = function(cell, section, row )
					print(section, row);
					tv.stopRefreshing();
					System.gc();
				end
			},
			ImageAndImage = {
				Size = function( section, row )
					return cellHeight;
				end,
				Init = function(cell, section, row)
					cell.icon = Image();
					cell.icon2 = Image();
				end,
				Layout = function(cell, section, row)
					print(window);
					local cellWdith, cellHeight = cell.window.size();
					cell.icon.frame(0, 0, cellHeight, cellHeight);
					cell.icon.image(imageUrl1);


					cell.icon2.frame(cellHeight+10, 0, cellHeight, cellHeight);
					cell.icon2.image(imageUrl1);
				end,
				Callback = function( cell, section, row )
					print(tv, section, row);
					tv.stopRefreshing();
					System.gc();
				end

			}
		},
		Header = function()
			local head = Button();
			head.title("我是头");
			head.frame(0,0,w,100);
			head.backgroundColor(0xff);
			return head;
		end,
		Footer = function()
			local foot = Button();
			foot.title("我是结尾");
			foot.frame(0,0,w,100);
			foot.backgroundColor(0xff0000);
			return foot;
		end,
		Callback = {
			Scrolling = function( firstVisibleSection, firstVisibleRow, visibleCellCount )
				print("scrolling", firstVisibleSection,"---" ,firstVisibleRow, "---", visibleCellCount);
			end,
			ScrollBegin = function()
				print("scrolling begin");
			end,
			ScrollEnd = function()
				print("scrolling end");
			end
		}
	};
	return tv;
end

tableView = createTableView();
tableView.dividerHeight(20);

loading = false;

tableView.frame(0,0,w,h-64);
tableView.backgroundColor(0xffFFFF);



