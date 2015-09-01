
屏幕宽, 屏幕高 = 屏幕尺寸()

广告信息 = 
{ 
	{ 
		图片="banner0.jpg", 
		链接="jhs://好货全剧透"
	},
	{ 
		图片="banner1.jpg", 
		链接="jhs://吃货行动起来"
	},
	{ 
		图片="banner0.jpg", 
		链接="jhs://好货全剧透"
	},
	{ 
		图片="banner1.jpg", 
		链接="jhs://吃货行动起来"
	},
	{ 
		图片="banner0.jpg", 
		链接="jhs://好货全剧透"
	},
	{ 
		图片="banner1.jpg", 
		链接="jhs://吃货行动起来"
	}
};

boxsys信息 = 
{ 
	{
		模式="口日式",
		数据=[
				{ 
					图片="left.jpg", 
					链接="jhs://男女鞋"
				},
				{ 
					图片="right.jpg", 
					链接="jhs://箱包"
				},
				{
					图片="right.jpg", 
					链接="jhs://箱包"
				}
			]
	},
	{
		模式="左中右式",
		数据=[
				{ 
					图片="left.jpg", 
					链接="jhs://男女鞋"
				},
				{ 
					图片="right.jpg", 
					链接="jhs://箱包"
				},
				{ 
					图片="left.jpg", 
					链接="jhs://箱包"
				}
			]
	},
	{
		模式="左右式",
		数据=[
				{ 
					图片="left.jpg", 
					链接="jhs://男女鞋"
				},
				{ 
					图片="right.jpg", 
					链接="jhs://箱包"
				}
			]
	},
	{
		模式="口日式",
		数据=[
				{ 
					图片="left.jpg", 
					链接="jhs://男女鞋"
				},
				{ 
					图片="right.jpg", 
					链接="jhs://箱包"
				},
				{
					图片="right.jpg", 
					链接="jhs://箱包"
				}
			]
	},
	{
		模式="左中右式",
		数据=[
				{ 
					图片="left.jpg", 
					链接="jhs://男女鞋"
				},
				{ 
					图片="right.jpg", 
					链接="jhs://箱包"
				},
				{ 
					图片="left.jpg", 
					链接="jhs://箱包"
				}
			]
	},
};

广告栏高度 = 150


广告位窗口 = UIScrollView( 0, 0, 屏幕宽, 广告栏高度,
	{
		滚动=function ()
			--print("scroolling");
		end,

		开始滚动=function ()
			--print("scrooll begin");
		end,

		滚动结束=function ()
			x,y = 广告位窗口:contentOffset();
			页面数量指示器:setCurrentPage( x/屏幕宽 )
			当前广告ID = x/屏幕宽
		end
	}
);

当前广告ID = 0;
timer = Timer(  function ()
						广告位窗口:setContentOffset( 当前广告ID*屏幕宽 , 0, true);
						当前广告ID = 当前广告ID + 1;
						当前广告ID = 当前广告ID%广告位数量;
				    end);

timer:start(3,true);

---
页面数量指示器 = UIPageControl();

广告位窗口:setPageEnable(true);
广告位窗口:showScrollIndicator(false,false);

广告位数量 = table.getn(广告信息);
广告位窗口:setContentSize( 屏幕宽*广告位数量, 广告栏高度 )
页面数量指示器:setNumberOfPages(广告位数量);
页面数量指示器:setCenter(屏幕宽/2, 广告栏高度-20);

for i=1,广告位数量 do
	local 广告位 = 广告信息[i];
	local x = (i-1) * 屏幕宽;
	local 按钮 = UIButton( x, 0, 屏幕宽, 广告栏高度, nil, 广告位.图片, function ()
																		  openUrl(广告位.链接);
																	   end);
	广告位窗口:addSubView(按钮)
end




数量 = table.getn(boxsys信息);
行高 = 120
间距 = 4
起始位置 = 广告栏高度 + 间距

for i=1,数量 do
	一行 = boxsys信息[i]
	模式 = 一行.模式;
	数据 = 一行.数据;
	if 模式=="左右式" then

		local 左按钮 = 数据[1]
		UIButton( 0 , 起始位置, 屏幕宽/2-间距, 行高, nil, 左按钮.图片, function ()
			openUrl(左按钮.链接);
		end);

		local 右按钮 = 数据[2]
		UIButton( 屏幕宽/2 , 起始位置, 屏幕宽/2-间距, 行高, nil, 右按钮.图片, function ()
			openUrl(右按钮.链接);
		end);

	elseif 模式=="左中右式" then

		local 左按钮 = 数据[1]
		UIButton( 0 , 起始位置, 屏幕宽/3-间距, 行高, nil, 左按钮.图片, function ()
			openUrl(左按钮.链接);
		end);

		local 中按钮 = 数据[2]
		UIButton( 屏幕宽/3 , 起始位置, 屏幕宽/3-间距, 行高, nil, 中按钮.图片, function ()
			openUrl(中按钮.链接);
		end);

		local 右按钮 = 数据[3]
		UIButton( 屏幕宽/3*2 , 起始位置, 屏幕宽/3, 行高, nil, 右按钮.图片, function ()
			openUrl(右按钮.链接);
		end);

	elseif 模式=="口日式"  then

		local 左按钮 = 数据[1]
		UIButton( 0 , 起始位置, 屏幕宽/2-间距, 行高, nil, 左按钮.图片, function ()
			openUrl(左按钮.链接);
		end);

		local 上按钮 = 数据[2]
		UIButton( 屏幕宽/2 , 起始位置, 屏幕宽/2, 行高/2-间距, nil, 上按钮.图片, function ()
																				   openUrl(上按钮.链接);
																			   end);

		local 下按钮 = 数据[3]
		UIButton( 屏幕宽/2 , 起始位置+ 行高/2, 屏幕宽/2, 行高/2, nil, 下按钮.图片, function ()
			openUrl(下按钮.链接);
		end);

	end
	起始位置 = 起始位置 + 行高+间距;
end

System.setContentSize(nil, 屏幕宽, 起始位置 );
System.setFrame(nil, 0, 20, 屏幕宽, 屏幕高*0.8 );

--------------------
计数器 = UILabel("开始");
计数器:setFrame(160,10,80,30);
计数器:setColor(0xff0000);
计数值 = 0;

定时器 = Timer(  function ()
						计数器:setText(""..计数值);
						计数值 = 计数值 + 1;
				    end);

定时器:start(0.1,true);






