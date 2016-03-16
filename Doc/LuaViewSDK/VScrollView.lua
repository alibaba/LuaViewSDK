--- VScrollView 
-- 垂直滚动的View 
--
-- VScrollView的父类是[View](./View.html), 子类有[TableView](./TableView.html), [CollectionView](./CollectionView.html)
-- @classmodUIView VScrollView


--- 通过这个方法创建一个VScrollView对象
-- @treturn VScrollView view
-- @usage 
-- Cell
-- local scrW, scrH = System.screenSize();
-- local cellW = scrW;
-- local cellH = 130;
-- CellJinrishouxuan = {};
-- CellJinrishouxuan.width = cellW;
-- CellJinrishouxuan.height = cellH;
-- function CellJinrishouxuan.create(originCell)
--     local cell = {};
--     cell.cellInited = false;
--     function cell.init()
--         if (self.cellInited == false ) then
--             self.cellInited = true;
--             self.scrollView = VScrollView();
--             self.scrollView.frame( 0, 0, cellW, cellH );
--             self.items = {};
--             for i=1,3 do
--             	local item = CellProductSmall.create(nil, cellW / 3, cellH );
--             	self.scrollView.addView(item.view);
--             	item.xy(cellH*(i-1),0 );
--             	self.items[i] = item;
--             	item.bizIconUrl = "https://gw.alicdn.com/tps/TB1Q6oxKpXXXXbcXVXXXXXXXXXX-86-96.png";
--             end
--             self.scrollView.contentSize(3*cellH, cellH);
--             self.scrollView.showScrollIndicator(true,false)
--             self.scrollView.backgroundColor(0xffffff);
--         end
--     end 
--     function cell.layout(model,option)
-- 		if(not model) then return end
--     	local itemList = model.itemList;
--     	local num = table:getn(itemList);
--     	for i=1, num do
--     		local model = itemList[i];
--     		local item  = self.items[i];
--     		item.layout(model,option);
--     	end
--     end
--     cell.init();
--     return cell;
-- end
function VScrollView()
end

--- 设置ContentSize大小
-- @number w 宽
-- @number h 高
function contentSize()
end

--- 获取ContentSize大小
-- @treturn number w 宽
-- @treturn number h 高
function contentSize()
end

--- 设置ContentSize大小
-- @number x x滚动偏移位置
-- @number y y滚动偏移位置
-- @bool animation 是否动画 参数可以省略
function offset()
end

--- 获取contentOffset大小
-- @treturn number x 
-- @treturn number y 
function offset()
end

--- 设置contentInset(只对IOS有效)
-- @number top
-- @number left
-- @number bottom
-- @number right
function contentInset()
end

--- 获取contentInset(只对IOS有效)
-- @treturn number top
-- @treturn number left
-- @treturn number bottom
-- @treturn number right
function contentInset()
end

--- 设置是否显示滚动条
-- @bool showsHorizontal 显示水平滚动条
function showScrollIndicator()
end

--- 获取是否显示滚动条信息
-- @treturn bool showsHorizontal 显示水平滚动条
function showScrollIndicator()
end

-- --- 代码控制 开始下拉刷新动画
-- function startRefreshing()
-- end

-- --- 停止下拉刷新动画(数据已经加载完成后调用)
-- function stopRefreshing()
-- end

-- --- 是否正在下拉刷新动画中
-- function isRefreshing()
-- end

