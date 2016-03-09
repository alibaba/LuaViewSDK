--- PagerIndicator
-- banner滚动第几页的小圆点
--
-- PagerIndicator的父类是[View](View.html)
-- @classmod PagerIndicator


--- 创建一个PagerIndicator对象
-- @treturn PagerIndicator pageControl
-- @usage local pageControl = PagerIndicator();
function PagerIndicator()
end



-- --- 设置总页数
-- -- @int num
-- function pageCount()
-- end
-- --- 获取总页数
-- -- @treturn int num
-- function pageCount()
-- end

--- 设置当前选中页
-- @int page 
function currentPage()
end

--- 获取当前选中页
-- @treturn int page
function currentPage()
end

--- 设置未选中页的颜色
-- @int color
-- @number alpha
function pageColor()
end

--- 获取未选中页的颜色
-- @treturn int color
-- @treturn number alpha
function pageColor()
end


--- 设置选中页的颜色
-- @int color
-- @number alpha
function currentPageColor()
end

--- 获取选中页的颜色
-- @treturn int color
-- @treturn number alpha
function currentPageColor()
end


