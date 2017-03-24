--
-- Created by IntelliJ IDEA.
-- User: tuoli
-- Date: 17/2/28
-- Time: 16:53
-- To change this template use File | Settings | File Templates.
--

require("kit.pickup")

local function start()
    pvObjs = Pickup:getInstance():render("widget/pagerview.xml")

    local autoSlider = pvObjs["pagerView1"]
    local indicator1 = pvObjs["indicator1"]

    local data = {
        PageCount = 3,
        Pages = {
            Init = function(page, pos)
                page.objs = Pickup:getInstance():render("widget/pagerview_page.xml")
            end,
            Layout = function(page, pos)
            end
        }
    }

    autoSlider:initParams(data)
    autoSlider:reload()
    autoSlider:indicator(indicator1)

    local manualSlider = pvObjs["pagerView2"]
    local indicator2 = pvObjs["indicator2"]
    local data2 = {
        PageCount = 3,
        Pages = {
            Init = function(page, pos)
                page.objs = Pickup:getInstance():render("widget/pagerview_page.xml")
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

