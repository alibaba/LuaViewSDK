

local scrW, scrH = System.screenSize()

local view = View()
view.frame(0, 0, scrW, scrH)

local webview = WebView()
webview.frame(0, 0, scrW, scrH - 350)
webview.loadUrl("http://m.taobao.com")

webview.callback({
    onPageStarted = function()
        print("start loading")
    end,
    onPageFinished = function()
        print("end loading")
    end,
    onReceiveError = function(errorCode, description, failedUrl)
        print("error", errorCode, description, failedUrl)
    end
})

print("[webview] url: ", webview.url())
print("[webview] title: ", webview.title())

webview.removeFromSuper()
view.addView(webview)

--local tf = TextField()
--tf.frame(0, 350, scrW - 200, 30)
--tf.hint("输入url")
--tf.removeFromSuper()
--view.addView(tf)
--
--local loadButton = Button()
--loadButton.frame(scrW - 150, 350, 100, 40)
--loadButton.text("load")
--loadButton.callback(function()
--    print("button click")
--    if webview.canGoBack() then
--        webview.loadUrl(tf.text())
--    end
--end)

--loadButton.removeFromSuper()
--view.addView(loadButton)

local backButton = Button()
backButton.frame(0, 400, 80, 80)
backButton.text("back")
backButton.callback(function()
    print("button click")
    if webview.canGoBack() then
        webview.goBack()
    end
end)

local forwardButton = Button()
forwardButton.frame(100, 400, 80, 80)
forwardButton.text("forward")
forwardButton.callback(function()
    print("button click")
    if webview.canGoForward() then
        webview.goForward()
    end
end)

local reloadButton = Button()
reloadButton.frame(200, 400, 80, 80)
reloadButton.text("reload")
reloadButton.callback(function()
    print("button click")
    webview.reload()
end)

local stopButton = Button()
stopButton.frame(300, 400, 80, 80)
stopButton.text("stoploading")
stopButton.callback(function()
    print("button click")
    webview.stopLoading()
end)

local getUrlButton = Button()
getUrlButton.frame(0, 500, 80, 80)
getUrlButton.text("geturl")
getUrlButton.callback(function()
    print("button click")
    Alert(webview.url())
end)

getUrlButton.removeFromSuper()
view.addView(getUrlButton)

local getTitleButton = Button()
getTitleButton.frame(100, 500, 80, 80)
getTitleButton.text("gettitle")
getTitleButton.callback(function()
    print("button click")
    Alert(webview.title())
end)

getTitleButton.removeFromSuper()
view.addView(getTitleButton)

local canPullButton = Button()
canPullButton.frame(200, 500, 80, 80)
canPullButton.text("canPull")
canPullButton.callback(function()
    print("button click")
    if webview.pullRefreshEnable() == true then
        webview.pullRefreshEnable(false)
    else
        webview.pullRefreshEnable(true)
    end
end)

canPullButton.removeFromSuper()
view.addView(canPullButton)

stopButton.removeFromSuper()
view.addView(stopButton)

reloadButton.removeFromSuper()
view.addView(reloadButton)

backButton.removeFromSuper()
view.addView(backButton)

forwardButton.removeFromSuper()
view.addView(forwardButton)
