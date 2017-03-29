--
-- Created by IntelliJ IDEA.
-- User: tuoli
-- Date: 17/2/28
-- Time: 16:53
-- To change this template use File | Settings | File Templates.
--

require("kit.pickup")

local _color = {0xA066D3, 0xFF7F50, 0x228B22}

local function start()
    pvObjs = Pickup:getInstance():render("widget/pagerview.xml")

    local manualSlider = pvObjs["pagerView1"]
    local indicator2 = pvObjs["indicator1"]
    local data2 = {
        PageCount = 3,
        Pages = {
            Init = function(page, pos)
                page.objs = Pickup:getInstance():render("widget/pagerview_page1.xml")
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
                page.objs = Pickup:getInstance():render("widget/pagerview_page.xml")
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

