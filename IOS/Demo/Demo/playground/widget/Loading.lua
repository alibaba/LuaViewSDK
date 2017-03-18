--
-- Created by IntelliJ IDEA.
-- User: tuoli
-- Date: 17/2/28
-- Time: 16:52
-- To change this template use File | Settings | File Templates.
--

Navigation:title("Loading.lua")

local _screenWidth, _screenHeight = System:screenSize()

-- 减掉ActionBar和StatusBar的高度
if (System:android()) then
    _screenHeight = _screenHeight - 80      -- Android, 不同机型, 高度不定, 比较蛋疼, 最好是底层直接计算好
else
    _screenHeight = _screenHeight - 64      -- iOS, 稳定在这个值
end

local function start()
    local root = View()
    root:frame(0, 0, _screenWidth, _screenHeight)
    root:flexCss("flex-direction: column, justify-content: center")

    local loading = LoadingIndicator()
    loading:flexCss("height: 50")
    loading:start()

    root:flexChildren(loading)
    root:flxLayout(true)     -- iOS
end

start()

