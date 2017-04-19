-- Created by LuaView.
-- Copyright (c) 2017, Alibaba Group. All rights reserved.
--
-- This source code is licensed under the MIT.
-- For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.


local function createRootView(params)
    local rootView = View()
    rootView.frame(params.x, params.y, params.w, params.h)
    return rootView
end

local function createWebView(rootView, params)
    local webview = WebView()
    webview.frame(params.x, params.y, params.w, params.h)
    webview.loadUrl(params.url)
    webview.callback(params.callbacks)
    webview.removeFromSuper()
    rootView.addView(webview)
    return webview
end

local function test(rootView, params)
    local button = Button()
    button.frame(params.x, params.y, params.w, params.h)
    button.text(params.text)
    button.callback(params.callback)
    button.removeFromSuper()
    rootView.addView(button)
end

local function main()
    local scrW, scrH = System.screenSize()

    local rootViewParams = {
        x = 0,
        y = 0,
        w = scrW,
        h = scrH
    }
    local rootView = createRootView(rootViewParams)

    local webViewParams = {
        x = 0,
        y = 0,
        w = scrW,
        h = scrH - 350,
        url = "http://m.taobao.com",
        callbacks = {
            onPageStarted = function()
                print("started")
            end,
            onPageFinished = function()
                print("finished")
            end,
            onReceiveError = function(errorCode, description, failedUrl)
                print("error", errorCode, description, failedUrl)
            end
        }
    }
    local webview = createWebView(rootView, webViewParams)

    local backParams = {
        x = 0,
        y = 400,
        w = 80,
        h = 80,
        text = "back",
        callback = function()
            if webview.canGoBack() then
                webview.goBack()
            end
        end
    }
    test(rootView, backParams)

    local forwardParams = {
        x = 100,
        y = 400,
        w = 80,
        h = 80,
        text = "forward",
        callback = function()
            if webview.canGoForward() then
                webview.goForward()
            end
        end
    }
    test(rootView, forwardParams)

    local reloadParams = {
        x = 200,
        y = 400,
        w = 80,
        h = 80,
        text = "reload",
        callback = function()
            webview.reload()
        end
    }
    test(rootView, reloadParams)

    local stopLoadingParams = {
        x = 300,
        y = 400,
        w = 80,
        h = 80,
        text = "stopLoading",
        callback = function()
            webview.stopLoading()
        end
    }
    test(rootView, stopLoadingParams)

    local getUrlParams = {
        x = 0,
        y = 500,
        w = 80,
        h = 80,
        text = "getUrl",
        callback = function()
            Alert(webview.url())
        end
    }
    test(rootView, getUrlParams)

    local getTitleParams = {
        x = 100,
        y = 500,
        w = 80,
        h = 80,
        text = "getTitle",
        callback = function()
            Alert(webview.title())
        end
    }
    test(rootView, getTitleParams)

    local canPullParams = {
        x = 200,
        y = 500,
        w = 80,
        h = 80,
        text = "canPull",
        callback = function()
            if webview.pullRefreshEnable() == true then
                webview.pullRefreshEnable(false)
            else
                webview.pullRefreshEnable(true)
            end
        end
    }
    test(rootView, canPullParams)
end

main()




