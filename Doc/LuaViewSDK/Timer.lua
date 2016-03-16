--- Timer
-- 定时器接口
-- @classmod Timer


--- 创建定时器Timer
function Timer()
end

--- 创建定时器Timer
-- @tparam function callback 定时器回调
-- @usage 
-- banner.callbackTimes = 0;
-- banner.index0 = 0;
-- banner.moveRight = true;
-- banner.timer = Timer(function()
-- 		if( banner.num<=1 ) then
-- 			return;
-- 		end
-- 		local currentPage = banner.pagerView.currentPage();
-- 		if (  currentPage == 1 ) then
-- 			banner.moveRight = true
-- 		end
-- 		if (  currentPage == banner.num ) then
-- 			banner.moveRight = false
-- 		end
-- 		if( banner.callbackTimes > 1) then
-- 			if( banner.moveRight ) then
-- 				banner.pagerView.currentPage(currentPage+1);
-- 			else
-- 				banner.pagerView.currentPage(currentPage-1);
-- 			end
-- 			banner.callbackTimes = 0;
-- 		else
-- 			banner.callbackTimes = banner.callbackTimes+1;
-- 		end
-- 	end);
-- banner.timer.start(2.1,true);
function Timer()
end

--- 启动定时器
-- @number time 间隔时间
-- @bool repeat 是否重复
function start()
end


--- 取消定时器
function cancel()
end

--- 设置回调
-- @tparam function callback 定时器回调
-- @usage 
-- timer = Timer();
-- timer.callback( function()
-- 		print("timer")
-- 	end);
-- timer.start(2.1,true);
function callback()
end


