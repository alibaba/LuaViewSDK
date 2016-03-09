--- ScrollView 
-- 可滚动的View 
--
-- ScrollView的父类是[View](./View.html), 子类有[TableView](./TableView.html), [CollectionView](./CollectionView.html)
-- @classmod ScrollView


--- 通过这个方法创建一个ScrollView对象
-- @treturn ScrollView view
function ScrollView()
end


--- 设置事件回调callback
-- @tparam table callback 代理方法
-- @usage 
-- --例子二:
-- local scrW, scrH = System.screenSize();
-- scrollView = ScrollView();
-- scrollView.backgroundColor(0xffFFFF);
-- scrollView.frame(0, 0, scrW, imageH );
-- scrollView.callback{
--         PullDown=^(){
--             -- 开始下拉刷新回调
--         },
--         PullUp = ^(){
--             -- 开始上拉刷新回调
--         },
--         Scrolling=^(){
--             -- 滚动中
--         },
--         Begin = ^(){
--             -- 开始滚动
--         },
--         End=^(){
--             -- 结束滚动
--         }
--  };
function callback()
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

--- 设置contentInset
-- @number top
-- @number left
-- @number bottom
-- @number right
function contentInset()
end

--- 获取contentInset
-- @treturn number top
-- @treturn number left
-- @treturn number bottom
-- @treturn number right
function contentInset()
end

--- 设置是否显示滚动条
-- @bool showsHorizontal 显示水平滚动条
-- @bool showsVertical 显示纵向滚动条
function showScrollIndicator()
end

--- 获取是否显示滚动条信息
-- @treturn bool showsHorizontal 显示水平滚动条
-- @treturn bool showsVertical 显示纵向滚动条
function showScrollIndicator()
end

--- 代码控制 开始下拉刷新动画
function startRefreshing()
end

--- 停止下拉刷新动画(数据已经加载完成后调用)
function stopRefreshing()
end

--- 是否正在下拉刷新动画中
function isRefreshing()
end

