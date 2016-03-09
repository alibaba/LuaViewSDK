--- 动画API
-- @classmod Animate


--- 通过这个方法 设置动画的时间,延时,最终状态,结束回调
-- @tparam function animations 动画过程回调 --- 该参数是必填参数
-- @usage --例子五
-- local button = Button();
-- button.center(0,0);
-- Animate(
--     function (){
--         button.center(100,100);
--     }
-- );
function Animate()
end


--- 通过这个方法 设置动画的时间,延时,最终状态,结束回调
-- @tparam function animations 动画过程回调 --- 该参数是必填参数
-- @tparam function completion 动画结束回调 --- 该参数是可选的 默认值是nil
-- @usage --例子四
-- local button = Button();
-- button.center(0,0);
-- Animate( 
--     function (){
--         button.center(100,100);
--     },
--     function (){
--         print("动画结束了");
--     }
-- );
function Animate()
end


--- 通过这个方法 设置动画的时间,延时,最终状态,结束回调
-- @number duration 动画耗费时间(单位秒) --- 该参数是可选的 默认值是0.3
-- @tparam function animations 动画过程回调 --- 该参数是必填参数
-- @tparam function completion 动画结束回调 --- 该参数是可选的 默认值是nil
-- @usage --2. 例子三
-- local button = Button();
-- button.center(0,0);
-- Animate( 
--     1.5, -- 动画时间
--     function (){
--         button.center(100,100);
--     },
--     function (){
--         print("动画结束了");
--     }
-- );
function Animate()
end


 

--- 通过这个方法 设置动画的时间,延时,最终状态,结束回调
-- @number duration 动画耗费时间(单位秒) --- 该参数是可选的 默认值是0.3
-- @number delay 动画延时多少时间后启动(单位秒) --- 该参数是可选的 默认值是0
-- @tparam function animations 动画过程回调 --- 该参数是必填参数
-- @tparam function completion 动画结束回调 --- 该参数是可选的 默认值是nil
-- @usage --2. 例子二(更多参数)
-- local button = Button();
-- button.center(0,0);
-- Animate( 
--     1.5, -- 动画时间
--     2, -- 动画延时
--     function (){
--         button.center(100,100);
--     },
--     function (){
--         print("动画结束了");
--     }
-- );
function Animate()
end

--- 通过这个方法 设置动画的时间,延时,最终状态,结束回调
-- @number duration 动画耗费时间(单位秒) --- 该参数是可选的 默认值是0.3
-- @number delay 动画延时多少时间后启动(单位秒) --- 该参数是可选的 默认值是0
-- @number option AnimationOptions参数 --- 该参数是可选的 默认值是0
-- @tparam function animations 动画过程回调 --- 该参数是必填参数
-- @tparam function completion 动画结束回调 --- 该参数是可选的 默认值是nil
-- @usage -- 例子一
-- local button = Button();
-- button.center(0,0);
-- Animate( 
--     1.5, -- 动画时间
--     2, -- 动画延时
--     0, -- 动画参数
--     function (){
--         button.center(100,100);
--     },
--     function (){
--         print("动画结束了");
--     }
-- );
--
-- @usage 
-- options 对应的 AnimationOptions 参数列表
-- UIViewAnimationOptionLayoutSubviews            = 1,
-- UIViewAnimationOptionAllowUserInteraction      = 2, -- turn on user interaction while animating
-- UIViewAnimationOptionBeginFromCurrentState     = 4, -- start all views from current value, not initial value
-- UIViewAnimationOptionRepeat                    = 8, -- repeat animation indefinitely
-- UIViewAnimationOptionAutoreverse               = 16, -- if repeat, run animation back and forth
-- UIViewAnimationOptionOverrideInheritedDuration = 32, -- ignore nested duration
-- UIViewAnimationOptionOverrideInheritedCurve    = 64, -- ignore nested curve
-- UIViewAnimationOptionAllowAnimatedContent      = 128, -- animate contents (applies to transitions only)
-- UIViewAnimationOptionShowHideTransitionViews   = 256, -- flip to/from hidden state instead of adding/removing
-- UIViewAnimationOptionOverrideInheritedOptions  = 512, -- do not inherit any options or animation type
-- UIViewAnimationOptionCurveEaseInOut            = 0, -- default
-- UIViewAnimationOptionCurveEaseIn               = 65536,
-- UIViewAnimationOptionCurveEaseOut              = 131072,
-- UIViewAnimationOptionCurveLinear               = 196608,
-- UIViewAnimationOptionTransitionNone            = 0, -- default
-- UIViewAnimationOptionTransitionFlipFromLeft    = 1048576; -- 1 << 20
-- UIViewAnimationOptionTransitionFlipFromRight   = 2097152; -- 2 << 20
-- UIViewAnimationOptionTransitionCurlUp          = 3145728; -- 3 << 20
-- UIViewAnimationOptionTransitionCurlDown        = 4194304; -- 4 << 20
-- UIViewAnimationOptionTransitionCrossDissolve   = 5242880; -- 5 << 20
-- UIViewAnimationOptionTransitionFlipFromTop     = 6291456; -- 6 << 20
-- UIViewAnimationOptionTransitionFlipFromBottom  = 7340032; -- 7 << 20
-- 

function Animate()
end

