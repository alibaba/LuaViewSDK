--
-- Created by IntelliJ IDEA.
-- User: tuoli
-- Date: 2/7/17
-- Time: 18:12
-- To change this template use File | Settings | File Templates.
--

require("kit.pickup")

local _jsonDataWidget = ' ["Label", "Button", "Image", "TextField", "Loading", "List", "PagerView", "WebView", "Animation"] '
local _jsonDataDemo = ' ["Douban", "GitHub"] '
local _jsonDataDescription = ' ["从豆瓣开放API获取电影信息", "按关键字检索GitHub的代码仓库"] '
local _dataWidget = Json:toTable(_jsonDataWidget)
local _dataDemo = Json:toTable(_jsonDataDemo)
local _dataDescription = Json:toTable(_jsonDataDescription)

local function main()
    print("tuoli", "render start")
    mainObjs = Pickup:getInstance():render("main.xml")
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
                    return Platform.contentWidth, 80
                end,
                Init = function(cell, section, row)
                    cell.window:backgroundColor(0xffffff)
                    cell.label = Label()
                    cell.label:frame(15, 15, Platform.contentWidth-15*2, 80-15*2)
                end,
                Layout = function(cell, section, row)
                    local style = StyledString(_dataWidget[row], { fontSize = 25, fontColor = 0x000000})
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
                    return Platform.contentWidth, 120
                end,
                Init = function(cell, section, row)
                    cell.objs = Pickup:getInstance():render("demo_item.xml")
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
    if (Platform.isAndroid) then
        info:nativeView():setLineSpacing(10,1)
    end

    local widgetTab = mainObjs["widgetTab"]
    local sampleTab = mainObjs["sampleTab"]
    local aboutTab = mainObjs["aboutTab"]

    widgetTab:callback(function()
        widgetTab:textColor(0xff0000)
        sampleTab:textColor(0x000000)
        aboutTab:textColor(0x000000)

        widgetTableView:show()
        sampleTableView:hide()
        aboutView:hide()
    end)
    sampleTab:callback(function()
        widgetTab:textColor(0x000000)
        sampleTab:textColor(0xff0000)
        aboutTab:textColor(0x000000)

        sampleTableView:show()
        widgetTableView:hide()
        aboutView:hide()
    end)
    aboutTab:callback(function()
        widgetTab:textColor(0x000000)
        sampleTab:textColor(0x000000)
        aboutTab:textColor(0xff0000)

        aboutView:show()
        widgetTableView:hide()
        sampleTableView:hide()
    end)
end

Navigation:title("Playground")
main()