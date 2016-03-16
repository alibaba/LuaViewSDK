--- PagerView
-- 横向分页滚动的View
--
-- PagerView的父类是[HScrollView](./HScrollView.html)
-- @classmod PagerView



--- 创建一个PagerView对象
-- @tparam table delegate 参数
-- @treturn PagerView pageView
-- @usage local pageView = PagerView();
-- require "lv_kit"
-- 
-- local width, height= System.screenSize()
-- 
-- pageView = PagerView({
--     PageCount = 12,
--     Pages = {
--         Init = function(page, pos)
--             page.icon = Button()
--         end,
--         Layout = function(page, pos)
--             page.icon.text("测试按钮"..pos)
--             page.icon.frame(0, 0, width, 200)
--         end
--     },
--     Callback = {
-- 
--         Scrolling = function( pageIndex, percent , offset )
--             print("Scrolling", pageIndex, percent, offset)
--         end,
-- 
--         ScrollEnd = function( pageIndex )
--             print("ScrollEnd", pageIndex )
--         end
--         
--     }
-- })
-- 
-- windowWidth , windowHeight = window.size();
-- pageView.frame(0,0,windowWidth,windowHeight-64)
function PagerView()
end

--- 设置当前页面
-- @number currentPage 当前页面id
function currentPage( )
end

--- 获取当前页面
function currentPage( )
end

--- 重新加载PagerView
function reload()
end

--- 设置PagerView的indicator
-- @number indicator PagerIndicator
function indicator( )
end

--- 获取PagerView的indicator
function indicator( )
end

--- 设置是否显示滚动条
-- @bool show 是否显示滚动条
function showScrollBar( )
end

--- 获取是否显示滚动条
function showScrollBar( )
end

