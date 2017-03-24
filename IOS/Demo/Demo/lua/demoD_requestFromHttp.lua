
function requestHttp( url)
    http = Http();
    http:get(url,
        function ( response )
            local str = tostring( response:data() );
            local f = loadstring(str, url);
            f();
        end)
end


requestHttp("http://g.tbcdn.cn/ju/lua-ppt/1.0.12/test.js");