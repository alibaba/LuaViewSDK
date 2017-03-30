--
-- Copyright 2017 Alibaba Group
-- License: MIT
-- Website: https://alibaba.github.io/LuaViewSDK
-- User: tuoli
-- Date: 17/3/30
--

require("kit.pica")

local function start()
    webObjs = Pica:getInstance():render("widget/webview.xml")

    local web = webObjs["web"]
    web:callback({
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

Navigation:title("WebView.lua")
start()

