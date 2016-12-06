scrW, scrH = System.screenSize()
error = Button()
error.text("xxxx")
error.frame(0, 0, scrW, scrH - 50);
error.backgroundColor(0xffff00)
error.callback(function()
    hideError();
end);

function showError()
    window.addView(error);
end

function hideError()
    error.removeFromSuper();
end

hideError();
showError();



window.callback({
    onShow = function()
        print("onShow")
    end,
    onHide = function()
        print("onHide")
    end,
    onBack = function()
        print("onBack")
        return false
    end,
    onWifiConnected = function()
        print("onWifiConnected")
        print(System.network())
    end,
    onMobileConnected = function()
        print("onMobileConnected")
        print(System.network())
    end,
    onConnectionClosed = function()
        print("onConnectionClosed")
        print(System.network())
    end,
    onLayout = function()
        print("onLayout")
    end
})

