--
-- Copyright 2017 Alibaba Group
-- License: MIT
-- Website: https://alibaba.github.io/LuaViewSDK
-- User: tuoli
-- Date: 17/3/30
--

Navigation:title("WebView.lua")

local meta = object:new()

function meta:onCreate(args)
    self.views = pica:getInstance():render("widget/webview.xml")
    self.web = self.views["web"]

    self:handle()
end

function meta:handle()
    self.web:callback({
        onPageStarted = function()
            print("started")
        end,
        onPageFinished = function()
            print("finished")
        end,
        onReceiveError = function(errorCode, description, failedUrl)
            print("error", errorCode, description, failedUrl)
        end
    })
end

return meta

