--- Button
-- 按钮控件
--
-- Button的父类是[View](View.html)
-- @classmod Button


--- 通过这个方法可以创建一个Button对象
-- @treturn button 按钮
function Button()
end


--- 设置按钮title
 -- @string title 标题
function title ( )
end
--- 获取按钮title
function title ()
end
--- 设置按钮title
 -- @string title 标题
function text()
end
--- 获取按钮title
function text ( )
end

--- 设置按钮图片
 -- @string normalImage normal图片样式
 -- @string hightLightImage 高亮图片 (可选)
 -- @string disableImage disable的图片 (可选)
 -- @usage local btn = Button();
function image ()
end

--- 设置按钮字体
 -- @string fontName 可选参数
 -- @number fontSize
 -- @usage local btn = Button();
function font ()
end

--- 设置按钮字体大小
 -- @string fontName 可选参数
 -- @number fontSize
 -- @usage local btn = Button();
function fontSize()
end

--- 设置按钮字体大小
 -- @string fontName 可选参数
 -- @number fontSize
 -- @usage local btn = Button();
function textSize()
end

--- 获取按钮title字体
 -- @treturn string fontName
 -- @treturn number fontSize
 -- @usage local btn = Button();
function font()
end

--- 设置按钮title颜色
 -- @int titleColor 颜色值
 -- @number alpha 透明度
 -- @usage local btn = Button();
function titleColor ()
end

--- 获取按钮title颜色
 -- @treturn int color 颜色值
 -- @treturn number alpha 透明度
 -- @usage local btn = Button();
function titleColor()
end

--- 设置按钮title颜色
 -- @int titleColor 颜色值
 -- @number alpha 透明度
 -- @usage local btn = Button();
function textColor ()
end

--- 获取按钮title颜色
 -- @treturn int color 颜色值
 -- @treturn number alpha 透明度
 -- @usage local btn = Button();
function textColor()
end

--- 设置按钮是否enabled
 -- @bool enabled 是否是能接受触摸事件
function enabled ()
end

--- 获取按钮是否enabled
 -- @treturn bool enable
function enabled()
end

--- 设置callback
 -- @tparam function callback 代理方法
 -- @usage button.callback( function(){
 --     -- code
 -- } );
--
function callback()
end


