--- Alert
-- 弹出系统自带的提示框
-- @classmod Alert


--- 弹出一个提示框
-- @treturn Alert view
-- @usage     
-- Alert("标题", "内容",
--                "确定","取消", -- 按钮个数支持0~10个
--                 ^(index){
--                        print("按钮ID:",index)
--                        if index==0 then
--                            window.release();
--                        end
--                 }
--            );
function Alert()
end




