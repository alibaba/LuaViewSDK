--- 菊花, 活动状体指示器
--
-- LoadingIndicator的父类是[View](./../ui容器/View.html)
-- @classmodUIItem LoadingIndicator


--- 创建一个LoadingIndicator对象
-- @treturn LoadingIndicator view
 -- @usage local view = LoadingIndicator();
function LoadingIndicator()
end

--- 菊花开始转动
-- @usage TextAlignment
-- Cell_JZGD = {
--     --加载更多的Cell
--     Init = function(cell)
--         cell.loadingmore = LoadingIndicator();
--         cell.window.backgroundColor(BG_COLOR);
--     end,
--     Layout = function(cell, section, row)
--         local cellW, cellH = cell.window.size();
--         cell.loadingmore.size(cellH, cellH);
--         cell.loadingmore.center(cellW / 2, cellH / 2);
--         if (option.nomore) then
--             cell.loadingmore.stop();
--         else
--             cell.loadingmore.start();
--             option.requestMoreItems(function()
--                 local index = option.index;
--                 local tv = tableViewArray[index];
--                 tv.reload();
--                 tv.stopRefreshing();
--                 cell.loadingmore.stop();
--             end);
--         end
--     end,
--     Size = function(section, row)
--         return scrW, 30;
--     end
-- }
function start()
end

--- 菊花停止转动
function stop()
end

--- 显示菊花并开始转动
function show()
end

--- 菊花停止转动, 并隐藏
function hide()
end

--- 菊花是否转动中
function isAnimating()
end

--- 设置菊花颜色
-- @int color
-- @number alpha 默认值是1
function color()
end

--- 获取菊花颜色
-- @treturn int color
-- @treturn number alpha
function color()
end

