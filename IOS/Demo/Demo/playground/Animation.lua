--
-- Created by IntelliJ IDEA.
-- User: tuoli
-- Date: 17/2/28
-- Time: 16:53
-- To change this template use File | Settings | File Templates.
--

Navigation:title("Animation.lua")

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
    root:flexCss("flex-direction: column")

    local rowTop = View()
    rowTop:flexCss("flex: 3")

    local img = Image()
    local imgWidth = (150 / _screenWidth) * _screenWidth
    img:flexCss("margin: 0, height: " .. imgWidth .. ", width: " .. imgWidth)
    img:image("http://g.alicdn.com/ju/lua/2.0.25/doc/icon.png")

rowTop:flexChildren({img})

    local rowBottom = View()
    rowBottom:flexCss("flex: 1, flex-wrap: wrap, flex-direction: column")

    local top1 = View()
    top1:flexCss("flex: 1, flex-direction: row")

    local button1 = Button()
    button1:flexCss("margin-left: 10, margin-right: 10, margin-top: 10, margin-bottom: 10, flex: 1")
    button1:backgroundColor(0x0000ff)
    button1:cornerRadius(5)
    button1:title("Translate")
    button1:titleColor(0xffffff)
    button1:callback(function()
        local translate = Animation():translation(200, 50):duration(1)
        translate:with(img):start()
    end)

    local button2 = Button()
    button2:flexCss("margin-left: 10, margin-right: 10, margin-top: 10, margin-bottom: 10, flex: 1")
    button2:backgroundColor(0x0000ff)
    button2:cornerRadius(5)
    button2:title("Scale")
    button2:titleColor(0xffffff)
    button2:callback(function()
        local scale = Animation():scale(0.5, 0.5):duration(1)
        scale:with(img):start()
    end)

    local button3 = Button()
    button3:flexCss("margin-left: 10, margin-right: 10, margin-top: 10, margin-bottom: 10, flex: 1")
    button3:backgroundColor(0x0000ff)
    button3:cornerRadius(5)
    button3:title("Alpha")
    button3:titleColor(0xffffff)
    button3:callback(function()
        local alpha = Animation():alpha(1, 0.3):duration(1)
        alpha:with(img):start()
    end)

top1:flexChildren({button1, button2, button3})

    local top2 = View()
    top2:flexCss("flex: 1, flex-direction: row")

    local button4 = Button()
    button4:flexCss("margin-left: 10, margin-right: 10, margin-top: 10, margin-bottom: 10, flex: 1")
    button4:backgroundColor(0x0000ff)
    button4:cornerRadius(5)
    button4:title("Click Me")
    button4:titleColor(0xffffff)
    button4:callback(function()
    end)

    local button5 = Button()
    button5:flexCss("margin-left: 10, margin-right: 10, margin-top: 10, margin-bottom: 10, flex: 1")
    button5:backgroundColor(0x0000ff)
    button5:cornerRadius(5)
    button5:title("Click Me")
    button5:titleColor(0xffffff)
    button5:callback(function()
    end)

    local button6 = Button()
    button6:flexCss("margin-left: 10, margin-right: 10, margin-top: 10, margin-bottom: 10, flex: 1")
    button6:backgroundColor(0x0000ff)
    button6:cornerRadius(5)
    button6:title("Click Me")
    button6:titleColor(0xffffff)
    button6:callback(function()
    end)

top2:flexChildren({button4, button5, button6})

rowBottom:flexChildren({top1, top2})

root:flexChildren({rowTop, rowBottom})
    root:flxLayout(true)     -- iOS
end

start()

