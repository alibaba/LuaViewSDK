--
-- Copyright 2017 Alibaba Group
-- License: MIT
-- Website: https://alibaba.github.io/LuaViewSDK
-- User: tuoli
-- Date: 17/4/5
--

Navigation:title("豆瓣电影详情")

local meta = object:new()

function meta:onCreate(args)
    self.web = WebView()
    self.web:frame(0, 0, sys.contW, sys.contH)
    self.web:loadUrl(args.url)
end

function meta:handle()

end

return meta

