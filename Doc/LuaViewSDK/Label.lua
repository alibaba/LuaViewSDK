---- Label
-- 放文本的View, 类似安卓的TextView
--
-- Label的父类是[View](./../ui容器/View.html)
-- @classmodUIItem Label

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

--- 设置Label字体大小
-- @number fontSize
function fontSize ()
end

--- 获取Label字体大小
-- @treturn number fontSize
function fontSize()
end

--- 设置Label字体大小
-- @number fontSize
function textSize ()
end

--- 获取Label字体大小
-- @treturn number fontSize
function textSize()
end


--- 设置Label文本对齐方式
-- @int align 可选参数
-- @usage TextAlignment
-- TextAlign.LEFT -- 左对齐,上下居中
-- TextAlign.RIGHT -- 右对齐,上下居中
-- TextAlign.CENTER -- 左右居中,上下居中
-- cell.nameLabel = Label()
-- cell.nameLabel.lineCount(2)
-- cell.nameLabel.textAlign(TextAlign.LEFT)
-- cell.nameLabel.backgroundColor(self.colors.white, 0)
-- 
function textAlign()
end

--- 获取Label文本对齐方式
-- @treturn int align对齐方式
function textAlign()
end


--- 设置Label最多行数
-- @int num 行数
-- @usage TextAlignment
-- TextAlign.LEFT -- 左对齐,上下居中
-- TextAlign.RIGHT -- 右对齐,上下居中
-- TextAlign.CENTER -- 左右居中,上下居中
-- cell.nameLabel = Label()
-- cell.nameLabel.lineCount(2)
-- cell.nameLabel.textAlign(TextAlign.LEFT)
-- cell.nameLabel.backgroundColor(self.colors.white, 0)
-- 
function lineCount ()
end

--- 获取Label最多行数
-- @treturn int num 行数
function lineCount()
end


--- 让Label字体大小适应宽度, 设置的字体为最大字体, 如果文字超出会缩小字体.
-- @bool yes 是否自适应字体大小(Label的边框固定,如果字数太多字体会缩小保证显示完整)
-- @usage 
-- cell.infoRight = Label();
-- cell.infoRight.frame( cellW-labelW-dx, y0, labelW, barH);
-- cell.infoRight.adjustFontSize(true);
-- cell.infoBackground.backgroundColor(0xfff1d1);
-- cell.infoRight.textAlign(TextAlign.RIGHT);
function adjustFontSize()
end

