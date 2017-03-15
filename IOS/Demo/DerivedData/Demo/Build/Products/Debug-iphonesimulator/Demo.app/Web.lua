--
-- Created by IntelliJ IDEA.
-- User: tuoli
-- Date: 17/2/28
-- Time: 16:53
-- To change this template use File | Settings | File Templates.
--

Navigation:title("Web.lua")

local _screenWidth, _screenHeight = System:screenSize()

-- 减掉ActionBar和StatusBar的高度
if (System:android()) then
    _screenHeight = _screenHeight - 80 -- Android, 不同机型, 高度不定, 比较蛋疼
else
    _screenHeight = _screenHeight - 64      -- iOS, 稳定在这个值
end

local function start()
    local root = View()
    root:frame(0, 0, _screenWidth, _screenHeight)
    root:flexCss("flex-direction: row")

    local web = WebView()
    web:flexCss("flex: 1")
    web:loadUrl("http://m.taobao.com")
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

    root:flexChildren(web)
    root:flxLayout(true)     -- iOS
end

start()

