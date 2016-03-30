--- ScaleType
-- 图片的缩放方式
-- @module ScaleType

--- ScaleType.CENTER 按图片的原来size居中显示，当图片长/宽超过View的长/宽，则截取图片的居中部分显示
ScaleType.CENTER = 0


--- ScaleType.CENTER_CROP 按比例扩大图片的size居中显示，使得图片长 (宽)等于或大于View的长(宽)
-- @usage 
-- image = Image();
-- image.image("temp.png")
-- image.scaleType(ScaleType.CENTER_CROP) -- 图片等比缩放, 且填充满整个ImageView, 可能被裁剪
--  
ScaleType.CENTER_CROP = 0

--- ScaleType.CENTER_INSIDE 将图片的内容完整居中显示，通过按比例缩小或原来的size使得图片长(宽)等于或小于View的长(宽)
-- @usage 
-- image = Image();
-- image.image("temp.png")
-- image.scaleType(ScaleType.CENTER_INSIDE) -- 图片等比缩放且全部可见, 图片尽可能大且不被裁剪
--  
ScaleType.CENTER_INSIDE = 0

--- ScaleType.FIT_XY 把图片按照指定的大小在View中显示(拉伸填充View模式)
-- @usage 
-- image = Image();
-- image.image("temp.png")
-- image.scaleType(ScaleType.FIT_XY) -- 图片填充满整个ImageView, 图片不被裁剪, 很可能被拉伸
--  
ScaleType.FIT_XY = 0


