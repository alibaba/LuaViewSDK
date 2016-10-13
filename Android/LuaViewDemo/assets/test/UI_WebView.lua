

local scrW, scrH = System.screenSize()

local view = View()
view.frame(0, 0, scrW, scrH)

local webview = WebView()
webview.frame(0, 0, scrW, scrH - 300)
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

webview.removeFromSuper()
view.addView(webview)

local backButton = Button()
backButton.frame(0, 400, 100, 100)
backButton.text("go back")
backButton.callback(function()
    print("button click")
    if webview.canGoBack() then
        webview.goBack()
    end
end)

local forwardButton = Button()
forwardButton.frame(200, 400, 100, 100)
forwardButton.text("go forward")
forwardButton.callback(function()
    print("button click")
    if webview.canGoForward() then
        webview.goForward()
    end
end)

local reloadButton = Button()
reloadButton.frame(120, 400, 80, 80)
reloadButton.text("reload")
reloadButton.callback(function()
    print("button click")
    webview.reload()
end)

local stopButton = Button()
stopButton.frame(320, 400, 80, 80)
stopButton.text("stop")
stopButton.callback(function()
    print("button click")
    webview.stopLoading()
end)

stopButton.removeFromSuper()
view.addView(stopButton)

reloadButton.removeFromSuper()
view.addView(reloadButton)

backButton.removeFromSuper()
view.addView(backButton)

forwardButton.removeFromSuper()
view.addView(forwardButton)
