--- 动画API
-- @classmodAnimation Animation


--- 通过这个方法 设置动画的时间,延时,最终状态,结束回调
-- @tparam function animations 动画过程回调 --- 该参数是必填参数
-- @usage --例子五
-- 简单动画
-- btn = Button();
-- btn.size(60, 120)
-- btn.text("按钮");
-- btn.callback({
--     onShow = function()
--         print("show")
--     end,
--     onHide = function()
--         print("hide")
--     end
-- })
-- btn2 = Button();
-- btn2.text("按钮2")
-- btn2.size(60, 120)
-- btn2.xy(100, 300)
-- animator1 = Animation().alpha(0.1).duration(3);
-- animator2 = Animation().translationX(100).duration(2).delay(1);
-- animator3 = Animation().rotation().value(100).duration(2);
-- btn.startAnimation(animator1, animator2, animator3)
-- btn2.startAnimation(animator1, animator2, animator3)
function Animation()
end


--- 克隆动画对象
function clone()
end

--- 动画绑定的View
function with()
end

--- 开始动画
function start()
end

--- 动画播放时间
function duration()
end

--- 动画延时
function delay()
end

--- 动画重复次数
function repeatCount()
end

--- 动画结束后是否回到初始状态
function reverses()
end

--- 设置动画回调
-- @table table 启动回调
-- @usage 
-- function bar.animateSixCenter( fx, fy, cx, cy , w0, w2, callback , time)
--     local x,y = self.six.center();
--     local scaleAni =  Animation();
--     scaleAni.scaleX((w2 - CATEGORY_BUTTON_DX/2)/Six_W);
--     ani =  Animation();
--     if( callback ) then
--         ani.callback( {
--                 onEnd = function ()
--                     callback();
--                 end,
--                 OnEnd = function ()
--                     callback();
--                 end
--             } );
--     end
--     -- 如果已经是目的地时间变短
--     if( time ) then
--         ani.duration(time);
--         scaleAni.duration(time);
--     else
--         ani.duration(0.25);
--         scaleAni.duration(0.25);
--     end
--     ani.translationX( cx-x);
--     self.six.startAnimation(ani, scaleAni);
-- end
function callback()
end

--- 设置动画启动回调
-- @function func 启动回调
function onStart()
end

--- 设置动画结束回调
-- @function func 结束回调
function onEnd()
end

--- 设置动画被取消回调
-- @function func 取消回调
function onCancel()
end

--- alpha值
-- @number alpha 透明度
function alpha()
end

--- 动画旋转
-- @number rotation 旋转角度(0~360)
function rotation()
end

--- scale设置()
-- @number scaleX 缩放比例
-- @number scaleY 缩放比例
function scale()
end

--- X轴缩放比例
-- @number scaleX 缩放比例
function scaleX()
end

--- Y轴缩放比例
-- @number scaleY 缩放比例
function scaleY()
end

--- 位移
-- @number translationX x位移
-- @number translationY y位移
function translation()
end

--- X坐标位移
-- @number translationX x位移
function translationX()
end

--- Y坐标位移
-- @number translationX x位移
-- @number translationY y位移
function translationY()
end

-- --- 克隆动画对象
-- function value()
-- end



