----
-- Mtop请求接口
-- @classmod Mtop


--- 创建Mtop对象
-- @table params Mtop请求参数 (可选)
-- @tparam function callback (可选)
-- @usage 
-- Mtop( 
--     {
--         method = "mtop.ju.block.optionminisite.get",
--         version= "1.0",
--         params = {
--             platformId = "8001",
--             page = 1,
--             pageSize = 10000,
--             optStr = self.optStr
--            },
--     },
--     ^( data, error){
--         self.isLoading = false;
--         if( data ) {
--             local model = data.model;
--             if( self.extend==nil )
--                 self.extend = {};
--             self.extend.syncOutput = model;
--             self.haveItems = true;
--             print(self.displayName, self.optStr, "loaded: ");
--         } else {
--             print(self.displayName, self.optStr, "error: ", error);
--         }
--         if( callback ) {
--            callback(option);
--         }
--         self.hiddenLoadingView();
--     }
-- );
function Mtop()
end


--- 发起请求
function request()
end

--- 取消mtop请求
function cancel()
end


--- 获取得到的数据
function responseData()
end

--- 获取得到的头信息
function responseHeaders()
end

