--- 错误提示框样式
-- native可定制
--
-- CustomPanel的父类是[View](View.html)
-- @classmod CustomPanel



--- 通过这个方法创建一个CustomPanel对象
-- @treturn CustomPanel view
 -- @usage local errorView = CustomPanel();
function CustomPanel()
end




--- 设置callback
-- @tparam function callback 代理方法
-- @usage 
-- scrW,scrH = System.screenSize();
-- x0 = scrW/6;
-- y0 = scrH/10;
-- w = scrW - x0*2;
-- h = scrH / 3;
-- customError = CustomPanel( "CustomError", x0,y0,w,h );
-- customError.backgroundColor(0xff0000);
-- customError.callback( function(tag)
--     print("customError", tag);
-- end);
-- customError2 = CustomError(x0,y0*2 + h,w,h );
-- customError2.backgroundColor(0xff0000);
-- customError2.callback( function(tag)
--     print("customError2", tag);
-- end)
function callback()
end
