--
-- Copyright 2017 Alibaba Group
-- License: MIT
-- Website: https://alibaba.github.io/LuaViewSDK
-- User: tuoli
-- Date: 17/3/30
--

require("kit.pica")

local _color = {0xA066D3, 0xFF7F50, 0x228B22}

local function start()
    pvObjs = Pica:getInstance():render("widget/pagerview.xml")

    local manualSlider = pvObjs["pagerView1"]
    local indicator2 = pvObjs["indicator1"]
    local data2 = {
        PageCount = 3,
        Pages = {
            Init = function(page, pos)
                page.objs = Pica:getInstance():render("widget/pagerview_page1.xml")
            end,
            Layout = function(page, pos)
                page.objs["root"]:backgroundColor(_color[pos])
            end
        }
    }
    manualSlider:initParams(data2)
    manualSlider:reload()
    manualSlider:indicator(indicator2)

    local autoSlider = pvObjs["pagerView2"]
    local indicator1 = pvObjs["indicator2"]

    local data = {
        PageCount = 3,
        Pages = {
            Init = function(page, pos)
                page.objs = Pica:getInstance():render("widget/pagerview_page.xml")
            end,
            Layout = function(page, pos)
                page.objs["root"]:backgroundColor(_color[pos])
            end
        }
    }

    autoSlider:initParams(data)
    autoSlider:reload()
    autoSlider:indicator(indicator1)
end

Navigation:title("PagerView.lua")
start()

