
w,h = System.screenSize();

window.backgroundColor(0,1);

cellHeight = 100
imageUrl1 = "http://gju2.alicdn.com/bao/uploaded/i1/10000073270926575/TB2fpg0cXXXXXb6XpXXXXXXXXXX_!!0-0-juitemmedia.jpg"
imageUrl2 = "http://img4.duitang.com/uploads/item/201306/25/20130625045508_sairr.thumb.600_0.jpeg"

tableView = RefreshTableView{
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
			end,
			Layout = function(cell, section, row)
				cell.icon.frame(0, 0, cellHeight, cellHeight);
				cell.icon.image(imageUrl1);

				cell.title.frame(cellHeight, 0, w-cellHeight, cellHeight);
				cell.title.text("测试"..section .."--" .. row);
			end,
			Callback = function( cell, section, row )
				print(section, row);
				tableView.stopRefreshing();
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
			Callback = function(cell, section, row )
				print(section, row);
				tableView.stopRefreshing();
				System.gc();
			end
		}
	},
	Callback = {
		Scrolling = function()
			print("scrolling");
		end,
		ScrollBegin = function()
			print("scrolling begin");
		end,
		ScrollEnd = function()
			print("scrolling end");
		end,
	    PullDownRefresh = function()
	    	print("PullDown");
		end,
	    PullUpRefresh = function()
	    	print("PullUp");
		end
	}
};

loading = false;

tableView.frame(0,0,w,h-64);
tableView.backgroundColor(0xffFFFF);



header = Button();
header.title("我是头");
header.frame(0,0,w,100);
header.backgroundColor(0xff);

tableView.header(header);


footer = Button();
footer.title("我是结尾");
footer.frame(0,0,w,100);
footer.backgroundColor(0xff0000);

tableView.footer(footer);



