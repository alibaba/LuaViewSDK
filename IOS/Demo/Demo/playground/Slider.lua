--
-- Created by IntelliJ IDEA.
-- User: tuoli
-- Date: 17/2/28
-- Time: 16:53
-- To change this template use File | Settings | File Templates.
--

Navigation:title("Slider.lua")

local _screenWidth, _screenHeight = System:screenSize()

-- 减掉ActionBar和StatusBar的高度
if (System:android()) then
    _screenHeight = _screenHeight - 80 -- Android, 不同机型, 高度不定, 比较蛋疼, 最好是底层直接计算好
else
    _screenHeight = _screenHeight - 64      -- iOS, 稳定在这个值
end

local function createIndicator()
    local indicator = PagerIndicator()
    indicator:flexCss("flex: 1, position: absolute")
    indicator:selectedColor(0xff0000)
    indicator:unselectedColor(0x00ff00)
    return indicator
end

local function start()
    local root = View()
    root:frame(0, 0, _screenWidth, _screenHeight)
    root:flexCss("flex-direction: column")

    local autoLabel = Label()
    autoLabel:flexCss("height: 50")
    autoLabel:backgroundColor(0x0000ff)
    local style = StyledString("Auto Play", { fontSize = 20, fontColor = 0xffffff})
    autoLabel:text(style)

    local parent1 = View()
    parent1:flexCss("flex-direction: column, flex: 1")

    local autoSlider = PagerView({
        PageCount = 3,
        Pages = {
            Init = function(page, pos)
                page.img = Image()
            end,
            Layout = function(page, pos)
                page.img:image("http://g.alicdn.com/ju/lua/2.0.25/doc/icon.png")
            end
        },
        Callback = {
            Scrolling=function(pos, percent, distance)
            end,
            Selected=function(pos)
            end
        }
    })

    autoSlider:autoScroll(3)
    autoSlider:looping(true)
    autoSlider:flexCss("flex: 1, margin-bottom: 10")

    parent1:flexChildren(autoSlider)
    parent1:flxLayout(true)

    local manualLabel = Label()
    manualLabel:flexCss("height: 50")
    manualLabel:backgroundColor(0x0000ff)
    local style = StyledString("Manual Play", { fontSize = 20, fontColor = 0xffffff})
    manualLabel:text(style)

    local parent2 = View()
    parent2:flexCss("flex-direction: column, flex: 1")

    local manualSlider = PagerView({
        PageCount = 3,
        Pages = {
            Init = function(page, pos)
                page.img = Image()
            end,
            Layout = function(page, pos)
                page.img:image("http://g.alicdn.com/ju/lua/2.0.25/doc/icon.png")
            end
        },
        Callback = {
            Scrolling=function(pos, percent, distance)
            end,
            Selected=function(pos)
            end
        }
    })

    manualSlider:flexCss("flex: 1, margin-bottom: 10")

    parent2:flexChildren(manualSlider)
    parent2:flxLayout(true)

    root:flexChildren(autoLabel, parent1, manualLabel, parent2)

    if (System:android()) then
        root:callback({
            OnLayout = function()
                print("tuoli", manualSlider:height())
            end
        })
    else
        root:flxLayout(false)     -- iOS
        print("tuoli", manualSlider:height())
    end
end

start()

