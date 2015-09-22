
print("初始化窗口大小, 默认窗口大小是(0,0)");
w,h = System.screenSize();
window.setFrame(0, 0, w, h);
window.setUserInteractionEnabled(true);
window.setBackgroundColor(0,1);

cellHeight = 100
imageUrl1 = "http://gju2.alicdn.com/bao/uploaded/i1/10000073270926575/TB2fpg0cXXXXXb6XpXXXXXXXXXX_!!0-0-juitemmedia.jpg"
imageUrl2 = "http://img4.duitang.com/uploads/item/201306/25/20130625045508_sairr.thumb.600_0.jpeg"

tableView = UITableView();
tableView.delegate = {
	Section = {
		NumberOfSections = 2,

		NumberOfRows = ^(section){
			return 10;
		}
	},

	Cell = {
		Id = ^( section, row ){
			return  "图片+文字";
		},
		"图片+文字" = {
			Height = ^( section, row ){
				return cellHeight;
			},
			Init = ^(cell, section, row){
				cell.icon = UIImageView();
				cell.title = UILabel();
				print("构造Cell");
			},
			Layout = ^(cell, section, row){
				cell.icon.setFrame(0, 0, cellHeight, cellHeight);
				cell.icon.setImage(imageUrl1);

				cell.title.setFrame(cellHeight, 0, w-cellHeight, cellHeight);
				cell.title.setText("测试"..section .."--" .. row);
			},
			Delegate = ^( section, row ){
				print(section, row);
				row = row %4;
				if( row ==1 ) {
					tableView.headerEndRefreshing();
				} else if (row ==2 ) {
					tableView.footerNoticeNoMoreData();
				} else if ( row == 3 ) {
					tableView.hiddenRefreshFooter(true);
				}
				-- tableView.reloadData();
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
		local head = UIButton();
		head.setTitle("我是头");
		head.setFrame(0,0,w,100);
		head.setBackgroundColor(0xff);
		return head;
	},
	Footer = ^(){
		local foot = UIButton();
		foot.setTitle("我是结尾");
		foot.setFrame(0,0,w,100);
		foot.setBackgroundColor(0xff0000);
		return foot;
	},
	Callback = {
		Scrolling = ^(){
			print("scrolling");
		},
		ScrollBegin = ^(){
			print("scrolling begin");
		},
		ScrollEnd = ^(){
			print("scrolling end");
		}
	}
};

loading = false;

tableView.setFrame(0,50,w,h-100);
tableView.setBackgroundColor(0xffFFFF);

tableView.initRefreshHeader();
tableView.initRefreshFooter();


