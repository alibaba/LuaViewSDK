--- 复合文本
-- 可以设置字符串的字体/颜色/背景色/前景色
-- @classmod StyledString

--- summary.
-- Description; this can extend over
-- several lines


--- 通过这个方法创建一个StyledString对象
-- @string string 文本
-- @table  table 属性表
-- @usage local view = StyledString();
-- w,h = System.screenSize();
-- window.frame(0, 0, w, h);
-- 
-- att1 = StyledString( "123test",  { fontSize=12, fontWeight="bold", fontColor=0xff } );
-- att2 = StyledString( "测试测试",  { fontSize=30, fontColor=0xff0000, backgroundColor=0xff} );
-- att3 = StyledString( "2897348",  { fontSize=13, fontColor=0x777777, strikethrough=true} );
-- att4 = StyledString( "下划线测试", { fontSize=9, fontColor=0x777777, underline=true} );
--
-- label = Label();
-- label.frame(0,30,w, 60);
-- label.text(att1 + att2 + att3 + att4);
--      
function StyledString()
end


--- 连接两个StyledString
-- @StyledString attrString 富文本
-- @usage local 
-- local att1 = StyledString( "123test",  { fontSize=12, fontWeight="bold", fontColor=0xff } );
-- local att2 = StyledString( "测试测试",  { fontSize=30, fontColor=0xff0000, backgroundColor=0xff} );
-- att1.append(att2);
--    
function append()
end

--- 连接两个StyledString
-- @StyledString attrString 富文本
-- @usage local 
-- local att1 = StyledString( "123test",  { fontSize=12, fontWeight="bold", fontColor=0xff } );
-- local att2 = StyledString( "测试测试",  { fontSize=30, fontColor=0xff0000, backgroundColor=0xff} );
-- local att3 = att1 + att2;
--      
function __add()
end


