--
-- Created by IntelliJ IDEA.
-- User: tuoli
-- Date: 17/2/28
-- Time: 16:52
-- To change this template use File | Settings | File Templates.
--

Navigation:title("TextField.lua")

local _screenWidth, _screenHeight = System:screenSize()

-- 减掉ActionBar和StatusBar的高度
if (System:android()) then
    _screenHeight = _screenHeight - 80 -- Android, 不同机型, 高度不定, 比较蛋疼, 最好是底层直接计算好
else
    _screenHeight = _screenHeight - 64      -- iOS, 稳定在这个值
end

local function start()
    local root = View()
    root:frame(0, 0, _screenWidth, _screenHeight * 0.8)
    root:flexCss("flex-direction: column")

    local textType = Label()
    textType:flexCss("height: 50")
    textType:backgroundColor(0x0000ff)
    local style = StyledString("Text Input", { fontSize = 20, fontColor = 0xffffff})
    textType:text(style)

    local textInput = TextField()
    textInput:flexCss("flex: 1")
    textInput:callback({
        BeginEditing=function()
            Toast("Begin Editing")
        end,

        EndEditing=function()
            Toast("End Editing")
        end
    })

    local hintType = Label()
    hintType:flexCss("height: 50")
    hintType:backgroundColor(0x0000ff)
    local style = StyledString("Input Hint", { fontSize = 20, fontColor = 0xffffff})
    hintType:text(style)

    local hintInput = TextField()
    hintInput:flexCss("flex: 1")
    local style = StyledString("Please Input", { fontSize = 16, fontColor = 0x58000000})
    hintInput:hint(style)

    local passwordType = Label()
    passwordType:flexCss("height: 50")
    passwordType:backgroundColor(0x0000ff)
    local style = StyledString("Password Input", { fontSize = 20, fontColor = 0xffffff})
    passwordType:text(style)

    local passwordInput = TextField()
    passwordInput:flexCss("flex: 1")

    local numberType = Label()
    numberType:flexCss("height: 50")
    numberType:backgroundColor(0x0000ff)
    local style = StyledString("Number Input", { fontSize = 20, fontColor = 0xffffff})
    numberType:text(style)

    local numberInput = TextField()
    numberInput:flexCss("flex: 1")

    root:flexChildren(textType, textInput, hintType, hintInput, passwordType, passwordInput, numberType, numberInput)
    root:flxLayout(true)     -- iOS
end

start()
