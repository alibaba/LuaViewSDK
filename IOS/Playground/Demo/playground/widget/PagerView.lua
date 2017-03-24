--
-- Created by IntelliJ IDEA.
-- User: tuoli
-- Date: 17/2/28
-- Time: 16:53
-- To change this template use File | Settings | File Templates.
--

require("kit.common")
require("kit.platform")

local _pica = require("kit.pica")

local function start()
    _pica:parseXml("widget/pagerview.xml")

    autoSlider = _pica:getViewByName("pagerView1")
    indicator1 = _pica:getViewByName("indicator1")

    local data = {
        PageCount = 3,
        Pages = {
            Init = function(page, pos)
                _pica:parseXml("widget/pagerview_page.xml")

                page.root = _pica:getViewByName("root")
                page.img = _pica:getViewByName("img")
            end,
            Layout = function(page, pos)
            end
        }
    }

    autoSlider:initParams(data)
    autoSlider:reload()
    autoSlider:indicator(indicator1)

    manualSlider = _pica:getViewByName("pagerView2")
    local indicator2 = _pica:getViewByName("indicator2")
    local data2 = {
        PageCount = 3,
        Pages = {
            Init = function(page, pos)
                _pica:parseXml("widget/pagerview_page.xml")

                page.root = _pica:getViewByName("root")
                page.img = _pica:getViewByName("img")
            end,
            Layout = function(page, pos)
            end
        }
    }
    manualSlider:initParams(data2)
    manualSlider:reload()
    manualSlider:indicator(indicator2)
end

Navigation:title("PagerView.lua")
start()

