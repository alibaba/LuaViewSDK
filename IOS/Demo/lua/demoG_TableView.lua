
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

			RowCount = function(section){
				return 10;
			}
		},

		Cell = {
			Id = function( section, row ){
				if ( section==1  ) then
					return  "图片+文字";
				else
					return "图片+图片";
				end
			},
			"图片+文字" = {
				Height = function( section, row ){
					return cellHeight;
				},
				Init = function(cell, section, row){
					cell.icon = Image();
					cell.title = Label();
					print("构造Cell");
					print(cell.window);
				},
				Layout = function(cell, section, row){
					cell.icon.frame(0, 0, cellHeight, cellHeight);
					cell.icon.image(imageUrl1);

					cell.title.frame(cellHeight, 0, w-cellHeight, cellHeight);
					cell.title.text("测试"..section .."--" .. row);
				},
				Callback = function( section, row ){
					print(section, row);
					tv.stopRefreshing();
					System.gc();
				}
			},
			"图片+图片" = {
				Height = function( section, row ){
					return cellHeight;
				},
				Init = function(cell, section, row){
					cell.icon = Image();
					cell.icon2 = Image();
				},
				Layout = function(cell, section, row){
					print(window);
					local cellWdith, cellHeight = cell.window.size();
					cell.icon.frame(0, 0, cellHeight, cellHeight);
					cell.icon.image(imageUrl1);


					cell.icon2.frame(cellHeight+10, 0, cellHeight, cellHeight);
					cell.icon2.image(imageUrl1);
				},
				Callback = function( section, row ){
					print(tv, section, row);
					tv.stopRefreshing();
					System.gc();
				}
			}
		},
		Header = function(){
			local head = Button();
			head.title("我是头");
			head.frame(0,0,w,100);
			head.backgroundColor(0xff);
			return head;
		},
		Footer = function(){
			local foot = Button();
			foot.title("我是结尾");
			foot.frame(0,0,w,100);
			foot.backgroundColor(0xff0000);
			return foot;
		},
		Callback = {
			Scrolling = function(){
				print("scrolling");
			},
			ScrollBegin = function(){
				print("scrolling begin");
			},
			ScrollEnd = function(){
				print("scrolling end");
			}
		}
	};
	return tv;
end

tableView = createTableView();

loading = false;

tableView.frame(0,0,w,h-64);
tableView.backgroundColor(0xffFFFF);



