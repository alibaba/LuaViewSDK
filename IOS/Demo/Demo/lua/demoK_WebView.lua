--工具包引入
require "lv_kit"

local scrW, scrH= System:screenSize()

webView = WebView();
webView:frame(20,20,300,500 );

webView:loadUrl("http://www.taobao.com");

webView:callback{
			onPageStarted = function()
				print("[webview] onPageStarted");
			end,
			onPageFinished = function()
				print("[webview] onPageFinished");
			end,
			onReceivedError = function()
				print("[webview] onReceivedError");
			end
		};


