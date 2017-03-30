--
-- Copyright 2017 Alibaba Group
-- License: MIT
-- Website: https://alibaba.github.io/LuaViewSDK
-- User: tuoli
-- Date: 17/3/30
--

require("kit.pica")

local _jsonDataWidget = ' ["Label", "Button", "Image", "TextField", "Loading", "List", "PagerView", "HScrollView", "WebView", "Animation"] '
local _jsonDataDemo = ' ["Douban", "GitHub"] '
local _jsonDataDescription = ' ["Get movie information from Douban.", "Search repositories from GitHub."] '
local _dataWidget = Json:toTable(_jsonDataWidget)
local _dataDemo = Json:toTable(_jsonDataDemo)
local _dataDescription = Json:toTable(_jsonDataDescription)

local function main()
    print("tuoli", "render start")
    mainObjs = Pica:getInstance():render("main.xml")
    print("tuoli", "render end")

    local widgetData = {
        Section = {
            SectionCount = function()
                return 1
            end,
            RowCount = function(section)
                return table.getn(_dataWidget)
            end
        },
        Cell = {
            Id = function(section, row)
                return "SampleCell"
            end,
            SampleCell = {
                Size = function(section, row)
                    return Sys.contW, Sys.contH/8
                end,
                Init = function(cell, section, row)
                    cell.window:backgroundColor(0xffffff)
                    cell.label = Label()
                    cell.label:frame(15*Sys.scale, 15*Sys.scale, Sys.contW-15*2*Sys.scale, (Sys.contH/8-15*2*Sys.scale))
                end,
                Layout = function(cell, section, row)
                    local style = StyledString(_dataWidget[row], { fontSize = 25*Sys.scale, fontColor = 0x000000})
                    cell.label:text(style)
                end,
                Callback = function(cell, section, row)
                    Bridge:jumpTo("widget/" .. _dataWidget[row] .. ".lua")
                end
            }
        }
    }

    local sampleData = {
        Section = {
            SectionCount = function()
                return 1
            end,
            RowCount = function(section)
                return table.getn(_dataDemo)
            end
        },
        Cell = {
            Id = function(section, row)
                return "SampleCell"
            end,
            SampleCell = {
                Size = function(section, row)
                    return Sys.contW, Sys.contH/5
                end,
                Init = function(cell, section, row)
                    cell.objs = Pica:getInstance():render("demo_item.xml")
                end,
                Layout = function(cell, section, row)
                    cell.objs["item"]:text(_dataDemo[row])
                    cell.objs["subitem"]:text(_dataDescription[row])
                end,
                Callback = function(cell, section, row)
                    Bridge:jumpTo("sample/" .. _dataDemo[row] .. ".lua")
                end
            }
        }
    }

    local widgetTableView = mainObjs["widgetTableView"]
    widgetTableView:initParams(widgetData)
    widgetTableView:reload()

    local sampleTableView = mainObjs["sampleTableView"]
    sampleTableView:initParams(sampleData)
    sampleTableView:reload()

    local aboutView = mainObjs["aboutView"]

    local info = mainObjs["info"]
    if (Sys.android) then
        info:nativeView():setLineSpacing(10*Sys.scale,1)
    end

    local widgetTab = mainObjs["widgetTab"]
    local sampleTab = mainObjs["sampleTab"]
    local aboutTab = mainObjs["aboutTab"]

    widgetTab:callback(function()
        widgetTab:textColor(0xEB3131)
        sampleTab:textColor(0x000000)
        aboutTab:textColor(0x000000)

        widgetTableView:show()
        sampleTableView:hide()
        aboutView:hide()
    end)
    sampleTab:callback(function()
        widgetTab:textColor(0x000000)
        sampleTab:textColor(0xEB3131)
        aboutTab:textColor(0x000000)

        sampleTableView:show()
        widgetTableView:hide()
        aboutView:hide()
    end)
    aboutTab:callback(function()
        widgetTab:textColor(0x000000)
        sampleTab:textColor(0x000000)
        aboutTab:textColor(0xEB3131)

        aboutView:show()
        widgetTableView:hide()
        sampleTableView:hide()
    end)
end

Navigation:title("Playground")
main()