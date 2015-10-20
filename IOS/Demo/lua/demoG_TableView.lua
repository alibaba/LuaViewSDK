
w,h = System.screenSize();

window.backgroundColor(0,1);

cellHeight = 100
imageUrl1 = "http://gju2.alicdn.com/bao/uploaded/i1/10000073270926575/TB2fpg0cXXXXXb6XpXXXXXXXXXX_!!0-0-juitemmedia.jpg"
imageUrl2 = "http://img4.duitang.com/uploads/item/201306/25/20130625045508_sairr.thumb.600_0.jpeg"

tableView = TableView {
	Section = {
		SectionCount = 2,

		RowCount = ^(section){
			return 10;
		}
	},

	Cell = {
		Id = ^( section, row ){
			if ( section==1  ) then
				return  "图片+文字";
			else
				return "图片+图片";
			end
		},
		"图片+文字" = {
			Height = ^( section, row ){
				return cellHeight;
			},
			Init = ^(cell, section, row){
				cell.icon = Image();
				cell.title = Label();
				print("构造Cell");
			},
			Layout = ^(cell, section, row){
				cell.icon.frame(0, 0, cellHeight, cellHeight);
				cell.icon.image(imageUrl1);

				cell.title.frame(cellHeight, 0, w-cellHeight, cellHeight);
				cell.title.text("测试"..section .."--" .. row);
			},
			ClickCallback = ^( section, row ){
				print(section, row);
				tableView.stopRefreshing();
				System.gc();
			}
		},
		"图片+图片" = {
			Height = ^( section, row ){
				return cellHeight;
			},
			Init = ^(cell, section, row){
				cell.icon = Image();
				cell.icon2 = Image();
			},
			Layout = ^(cell, section, row){
				print(window);
				local cellWdith, cellHeight = cell.window.size();
				cell.icon.frame(0, 0, cellHeight, cellHeight);
				cell.icon.image(imageUrl1);


				cell.icon2.frame(cellHeight+10, 0, cellHeight, cellHeight);
				cell.icon2.image(imageUrl1);
			},
			ClickCallback = ^( section, row ){
				print(tableView, section, row);
				tableView.stopRefreshing();
				System.gc();
			}
		}
	},
	Refresh = {
	    PullDown = ^(){
	    	print("PullDown");
		},
	    PullUp = ^(){
	    	print("PullUp");
		}
	},
	Header = ^(){
		local head = Button();
		head.title("我是头");
		head.frame(0,0,w,100);
		head.backgroundColor(0xff);
		return head;
	},
	Footer = ^(){
		local foot = Button();
		foot.title("我是结尾");
		foot.frame(0,0,w,100);
		foot.backgroundColor(0xff0000);
		return foot;
	},
	ScrollingCallback = ^(){
		print("scrolling");
	},
	ScrollBeginCallback = ^(){
		print("scrolling begin");
	},
	ScrollEndCallback = ^(){
		print("scrolling end");
	}
};

loading = false;

tableView.frame(0,0,w,h-64);
tableView.backgroundColor(0xffFFFF);



