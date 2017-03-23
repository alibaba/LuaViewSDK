--
-- Created by IntelliJ IDEA.
-- User: tuoli
-- Date: 17/2/28
-- Time: 16:53
-- To change this template use File | Settings | File Templates.
--

Navigation:title("WebView.lua")

local function start()
    local pica = require("kit.pica")
    local xml = File:read("widget/webview.xml")
    pica:parseXml(xml)

    local web = pica:getViewByName("web")
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

start()

