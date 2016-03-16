----
-- Http请求接口
-- @classmodNet Http


--- 创建Http对象
-- @table params Http请求参数
-- @tparam function callback
-- @usage 
-- function ajax(params0, callback)
--     local url = params0.url;
--     local data = params0.data;
--     local params = nil;
--     for key, value in pairs(data) do
--         if (params) then
--             params = params .. "&" .. key .. "=" .. tostring(value);
--         else
--             params = "" .. key .. "=" .. tostring(value);
--         end
--     end
--     params = encodeURI(params)
--     url = urlAddHttp(url);
--     url = url .. "?" .. params;
--     print("ajax: ", url);
--     local http = Http();
--     http.get(url,
--         function(response)
--             local str = tostring(response.data());
--             local object = Json.toTable(str);
--             if (callback) then
--                 callback(object);
--             end
--         end)
-- end
-- 
function Http()
end


--- 发起请求
function get()
end
--- 发起请求
function post()
end

--- 取消mtop请求
function cancel()
end


--- 获取得到的数据
function data()
end

--- 获取得到的头信息
function header()
end
--- 获取Http返回码
function code()
end

