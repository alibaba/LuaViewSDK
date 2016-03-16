----
-- Mtop请求接口
-- @classmodNet Mtop


--- 创建Mtop对象
-- @table params Mtop请求参数 (可选)
-- @tparam function callback (可选)
-- @usage 
-- function net.updateOptionBanner()
-- 	local mtop  = Mtop( 
-- 		  {
-- 				method = "mtop.options.get",
-- 				version= "1.0",
-- 				params = {
-- 							platformId = "7101",
-- 							page = 1,
-- 							pageSize = 1000,
-- 						 },
-- 		  },
-- 		  function( data, error) 
-- 		  		if ( data ) then
-- 			  			print("updateOptionBanner", data);
-- 		  		end
-- 		  end
-- 	);
-- 	mtop.request();
-- end
-- 
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

