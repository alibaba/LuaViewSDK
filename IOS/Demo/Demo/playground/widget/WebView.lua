--
-- Created by IntelliJ IDEA.
-- User: tuoli
-- Date: 17/2/28
-- Time: 16:53
-- To change this template use File | Settings | File Templates.
--

local _pica = require("kit.pica")

local function start()
    _pica:parseXml("widget/webview.xml")

    local web = _pica:getViewByName("web")
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

