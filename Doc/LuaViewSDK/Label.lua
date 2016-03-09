---- Label
-- 放文本的View, 类似安卓的TextView
--
-- Label的父类是[View](./View.html)
-- @classmod Label

--- 通过这个全局静态函数 构建一个新的Label对象
-- @treturn Label label
function Label()
end

--- 设置Label文本信息
-- @string text 文本
-- @usage local label = Label();
-- label.text("测试");
--      
function text()
end

--- 获取Label文本
-- @treturn string text 文本
function text()
end

--- 设置Label文本颜色
-- @int color 色值
-- @number alpha 透明度
function textColor()
end

--- 获取Label文本颜色
-- @treturn int color 色值
-- @treturn number alpha 透明度
function textColor()
end

--- 设置Label字体
-- @string fontName 可选参数
-- @number fontSize
function font ()
end

--- 获取Label字体
-- @treturn string fontName
-- @treturn number fontSize
function font()
end


--- 设置Label文本对齐方式
-- @int align 可选参数
-- @usage TextAlignment
-- NSTextAlignmentLeft      = 0,    -- Visually left aligned
-- NSTextAlignmentCenter    = 1,    -- Visually centered
-- NSTextAlignmentRight     = 2,    -- Visually right aligned
--      
function textAlign()
end

--- 获取Label文本对齐方式
-- @treturn int align对齐方式
function textAlign()
end


--- 设置Label最多行数
-- @int num 行数
function lineCount ()
end

--- 获取Label最多行数
-- @treturn int num 行数
function lineCount()
end


--- 让Label字体大小适应宽度, 设置的字体为最大字体, 如果文字超出会缩小字体.
function adjustFontSize()
end

