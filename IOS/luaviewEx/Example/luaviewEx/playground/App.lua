--
-- Copyright 2017 Alibaba Group
-- License: MIT
-- Website: https://alibaba.github.io/LuaViewSDK
-- User: tuoli
-- Date: 17/3/30
--

Navigation:title("Playground")

local meta = object:new()

function meta:onCreate(args)
    self.views = pica:getInstance():render("app.xml")
    self.widgetList = self.views["widgetTableView"]
    self.sampleList = self.views["sampleTableView"]
    self.aboutView = self.views["aboutView"]
    self.widgetTab = self.views["widgetTab"]
    self.sampleTab = self.views["sampleTab"]
    self.aboutTab = self.views["aboutTab"]
    self.aboutInfo = self.views["info"]
    if (sys.android) then
        self.aboutInfo:nativeView():setLineSpacing(10*sys.scale,1)
    end

    self.widgetData = Json:toTable(' ["Label", "Button", "Image", "TextField", "Loading", "List", "PagerView", "HScrollView", "WebView", "Animation"] ')
    self.sampleData = Json:toTable(' ["Douban", "GitHub"] ')
    self.sampleSpec = Json:toTable(' ["Get movie information from Douban.", "Search repositories from GitHub."] ')

    self:handle()
end

function meta:handle()
    self.widgetList:initParams({
        Section = {
            SectionCount = function()
                return 1
            end,
            RowCount = function(section)
                return table.getn(self.widgetData)
            end
        },
        Cell = {
            Id = function(section, row)
                return "SampleCell"
            end,
            SampleCell = {
                Size = function(section, row)
                    return sys.contW, sys.contH/8
                end,
                Init = function(cell, section, row)
                    cell.window:backgroundColor(0xffffff)
                    cell.label = Label()
                    cell.label:frame(15*sys.scale, 15*sys.scale, sys.contW-15*2*sys.scale, (sys.contH/8-15*2*sys.scale))
                end,
                Layout = function(cell, section, row)
                    local style = StyledString(self.widgetData[row], { fontSize = 25*sys.scale, fontColor = 0x000000})
                    cell.label:text(style)
                end,
                Callback = function(cell, section, row)
                    Bridge:require({page="widget/" .. self.widgetData[row]})
                end
            }
        }
    })
    self.widgetList:reload()

    self.sampleList:initParams({
        Section = {
            SectionCount = function()
                return 1
            end,
            RowCount = function(section)
                return table.getn(self.sampleData)
            end
        },
        Cell = {
            Id = function(section, row)
                return "SampleCell"
            end,
            SampleCell = {
                Size = function(section, row)
                    return sys.contW, sys.contH/5
                end,
                Init = function(cell, section, row)
                    cell.objs = pica:getInstance():render("demo.xml")
                end,
                Layout = function(cell, section, row)
                    cell.objs["item"]:text(self.sampleData[row])
                    cell.objs["subitem"]:text(self.sampleSpec[row])
                end,
                Callback = function(cell, section, row)
                    Bridge:require({page="sample/" .. self.sampleData[row]})
                end
            }
        }
    })
    self.sampleList:reload()

    self.widgetTab:callback(function()
        self.widgetTab:textColor(0xEB3131)
        self.sampleTab:textColor(0x000000)
        self.aboutTab:textColor(0x000000)

        self.widgetList:show()
        self.sampleList:hide()
        self.aboutView:hide()
    end)
    self.sampleTab:callback(function()
        self.widgetTab:textColor(0x000000)
        self.sampleTab:textColor(0xEB3131)
        self.aboutTab:textColor(0x000000)

        self.sampleList:show()
        self.widgetList:hide()
        self.aboutView:hide()
    end)
    self.aboutTab:callback(function()
        self.widgetTab:textColor(0x000000)
        self.sampleTab:textColor(0x000000)
        self.aboutTab:textColor(0xEB3131)

        self.aboutView:show()
        self.widgetList:hide()
        self.sampleList:hide()
    end)
end

return meta