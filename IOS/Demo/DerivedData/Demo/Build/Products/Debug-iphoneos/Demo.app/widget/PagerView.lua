--
-- Created by IntelliJ IDEA.
-- User: tuoli
-- Date: 17/2/28
-- Time: 16:53
-- To change this template use File | Settings | File Templates.
--

Navigation:title("PagerView.lua")

local _screenWidth, _screenHeight = System:screenSize()

-- 减掉ActionBar和StatusBar的高度
if (System:android()) then
    local device = System:device()
    _screenHeight = device.window_height - device.status_bar_height - device.nav_height
else
    _screenHeight = _screenHeight - 64      -- iOS, 稳定在这个值
end

local function createIndicator()
    local indicator = PagerIndicator()
    indicator:selectedColor(0x666666)
    indicator:unselectedColor(0xeeeeee)
    return indicator
end

local function start()
    local pica = require("kit.pica")
    local xml = File:read("widget/pagerview.xml")
    pica:parseXml(xml)
    local pagerContainer1 = pica:getViewByName("pagerContainer1")
    local pagerContainer2 = pica:getViewByName("pagerContainer2")

    autoSlider = PagerView({
        PageCount = 3,
        Pages = {
            Init = function(page, pos)
                local xml_page = File:read("widget/pagerview_page.xml")
                pica:parseXml(xml_page)

                page.root = pica:getViewByName("root")
                page.img = pica:getViewByName("img")
            end,
            Layout = function(page, pos)
            end
        }
    })

    autoSlider:autoScroll(3)
    autoSlider:looping(true)
    autoSlider:frame(0, 0, _screenWidth, (_screenHeight - 100)/2)
    pagerContainer1:addView(autoSlider)
    local indicator = createIndicator()
    indicator:frame(0, _screenHeight/3, _screenWidth, 20)
    pagerContainer1:addView(indicator)
    autoSlider:indicator(indicator)

    manualSlider = PagerView({
        PageCount = 3,
        Pages = {
            Init = function(page, pos)
                local xml_page = File:read("widget/pagerview_page.xml")
                pica:parseXml(xml_page)

                page.root = pica:getViewByName("root")
                page.img = pica:getViewByName("img")
            end,
            Layout = function(page, pos)
            end
        }
    })

    manualSlider:frame(0, 0, _screenWidth, (_screenHeight - 100)/2)
    pagerContainer2:addView(manualSlider)
    local indicator2 = createIndicator()
    indicator2:frame(0, _screenHeight/3, _screenWidth, 20)
    pagerContainer2:addView(indicator2)
    manualSlider:indicator(indicator2)
end

start()

