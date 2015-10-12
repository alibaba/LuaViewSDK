
w,h = System.screenSize();

window.setBackgroundColor(0,1);

cellHeight = 100
imageUrl1 = "http://gju2.alicdn.com/bao/uploaded/i1/10000073270926575/TB2fpg0cXXXXXb6XpXXXXXXXXXX_!!0-0-juitemmedia.jpg"
imageUrl2 = "http://img4.duitang.com/uploads/item/201306/25/20130625045508_sairr.thumb.600_0.jpeg"

collectionView = UICollectionView();
collectionView.delegate = 
{
	Section = {
		SectionCount = 2,

		RowCount = ^(section){
			return 4;
		},
		-- Spacing = ^(section ){
		-- 	return 0;
		-- },
		-- EdgeInsets = ^(section ){
		-- 	return 20,0,0,0;
		-- }

	},
	Cell = { 
		Id = function ( section, row ){
			if( section%2==0 ) {
				return "图片+文字";
			} else {
				return "图片+文字2";
			}
		},
		"图片+文字" = {
			Size = ^(section, row){
				return (w-10)/2 ,200;
			},
			Init = ^(cell){
				local cellWidth ,cellHeight = cell.window.size();
				cellHeight = cellHeight / 2;
				cell.icon = UIImage();
				cell.icon.setFrame(0, 0, cellHeight, cellHeight);

				cell.title = UILabel();
				cell.title.setFrame(0, cellHeight+2, cellHeight, 40);
				cell.title.setTextColor(0xffFFFF);
				cell.title.setBackgroundColor(0xff00ff);
				print("构造Cell--2");
			},
			Layout = ^(cell , section, row){
				cell.icon.setImage(imageUrl1, ^(){
					local x,y,w,h = cell.icon.frame();
					print("dongxicheng----",x,y,w,h);
					});

				cell.title.setText("测试"..section .."--" .. row);
				print("布局Cell--" , section, "--", row);

				cell.window.setBackgroundColor( section*0x770000 +  (row%3)*0x33 );
			},
			Delegate = ^(section, row){
				print(section, row);
				System.gc();
			}
		},
		"图片+文字2" = {
			Size = ^(section, row){
				return w ,200;
			},
			Init = ^(cell){
				cell.icon = UIImage();
				cell.icon.setFrame(w*0.05, 10, cellHeight, cellHeight);

				cell.button = UIButton();
				cell.button.setFrame(0,0,100,60);
				cell.button.setBackgroundColor(0x777777);
				cell.button.delegate = ^(){
						UIAnimate( ^(){
										cell.icon2.setCenter(160,100);
									}
								 );
				};


				cell.icon2 = UIImage();
				cell.icon2.setFrame(160, 0, cellHeight, cellHeight);

				print("构造Cell--2");
			},
			Layout = ^(cell , section, row){
				cell.icon.setImage(imageUrl1, ^(){
					local x,y,w,h = cell.icon.frame();
					print("dongxicheng----",x,y,w,h);
					});

				cell.icon2.setImage(imageUrl1)
				print("布局Cell--" , section, "--", row);

				cell.window.setBackgroundColor( section*0x770000 +  (row%3)*0x33 );
			},
			Delegate = ^(section, row){
				print(section, row);
				System.gc();
			}
		}
	}
};

collectionView.setFrame(0,0,w,h-64);
collectionView.setBackgroundColor(0xffFFFF);
collectionView.miniSpacing(10);



