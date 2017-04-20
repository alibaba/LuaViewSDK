--
-- Copyright 2017 Alibaba Group
-- License: MIT
-- Website: https://alibaba.github.io/LuaViewSDK
-- User: tuoli
-- Date: 17/3/30
--

Navigation:title("PagerView.lua")

local meta = object:new()

function meta:onCreate(args)
    self.views = pica:getInstance():render("widget/pagerview.xml")
    self.manualSlider = self.views["pagerView1"]
    self.indicator1 = self.views["indicator1"]
    self.autoSlider = self.views["pagerView2"]
    self.indicator2 = self.views["indicator2"]

    self.color = {0xA066D3, 0xFF7F50, 0x228B22}

    self:handle()
end

function meta:handle()
    self.manualSlider:initParams({
        PageCount = 3,
        Pages = {
            Init = function(page, pos)
                page.objs = pica:getInstance():render("widget/pagerview_page1.xml")
            end,
            Layout = function(page, pos)
                page.objs["root"]:backgroundColor(self.color[pos])
            end
        }
    })
    self.manualSlider:indicator(self.indicator1)

    self.autoSlider:initParams({
        PageCount = 3,
        Pages = {
            Init = function(page, pos)
                page.objs = pica:getInstance():render("widget/pagerview_page.xml")
            end,
            Layout = function(page, pos)
                page.objs["root"]:backgroundColor(self.color[pos])
            end
        }
    })
    self.autoSlider:indicator(self.indicator2)
end

return meta

