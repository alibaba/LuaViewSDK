--
-- Created by IntelliJ IDEA.
-- User: tuoli
-- Date: 2/7/17
-- Time: 18:12
-- To change this template use File | Settings | File Templates.
--

Navigation:title("Playground")

local _jsonDataWidget = ' ["Label", "Button", "Image", "TextField", "Loading", "List", "PagerView", "WebView", "Animation"] '
--                        ' "Audio", "Picker", "Dropdown", "Clipboard", "Video", "Camera"] '

local _dataWidget = Json:toTable(_jsonDataWidget)

local _jsonDataDemo = ' ["Douban", "GitHub"] '

local _dataDemo = Json:toTable(_jsonDataDemo)

local _jsonDataDescription = ' ["从豆瓣开放API获取电影信息", "按关键字检索GitHub的代码仓库"] '

local _dataDescription = Json:toTable(_jsonDataDescription)

local _screenWidth, _screenHeight = System:screenSize()

-- 减掉ActionBar和StatusBar的高度
if (System:android()) then
    local device = System:device()
    _screenHeight = device.window_height - device.status_bar_height - device.nav_height
else
    _screenHeight = _screenHeight - 64      -- iOS, 稳定在这个值
end

local function main()
    local pica = require("kit.pica")

    print("tuoli", "xml read start")
    local data = File:read("main.xml")
    print("tuoli", "xml read end")
    pica:parseXml(data)

    widgetTableView = CollectionView({
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
                    return _screenWidth, 80
                end,
                Init = function(cell, section, row)
                    cell.window:backgroundColor(0xffffff)
                    cell.label = Label()
                    cell.label:frame(15, 15, _screenWidth-15*2, 80-15*2)
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
    })
    if (not System:android()) then
        widgetTableView:frame(0, 0, _screenWidth, _screenHeight*0.9)
    end
    widgetTableView:backgroundColor(0xeeeeee)
    widgetTableView:miniSpacing(1)

    local topContainer = pica:getViewByName("topContainer")
    topContainer:addView(widgetTableView)

    local tab1 = pica:getViewByName("tab1")
    local tab2 = pica:getViewByName("tab2")
    local tab3 = pica:getViewByName("tab3")
    tab1:callback(function()
        tab1:textColor(0xff0000)
        tab2:textColor(0x000000)
        tab3:textColor(0x000000)
        if (widgetTableView) then
            widgetTableView:show()
        end

        if (demoTableView) then
            demoTableView:hide()
        end

        if (aboutView) then
            aboutView:hide()
        end
    end)
    tab2:callback(function()
        tab1:textColor(0x000000)
        tab2:textColor(0xff0000)
        tab3:textColor(0x000000)
        if (demoTableView) then
            demoTableView:show()
        else
            demoTableView = CollectionView({
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
                            return _screenWidth, 120
                        end,
                        Init = function(cell, section, row)
                            print("tuoli", "xml read start")
                            local xml = File:read("demo_item.xml")
                            print("tuoli", "xml read end")
                            pica:parseXml(xml)

                            local root = pica:getViewByName("root")
                            cell.window:addView(root)
                            cell.window:backgroundColor(0xffffff)

                            cell.item = pica:getViewByName("item")
                            cell.subitem = pica:getViewByName("subitem")
                        end,
                        Layout = function(cell, section, row)
                            cell.item:text(_dataDemo[row])
                            cell.subitem:text(_dataDescription[row])
                        end,
                        Callback = function(cell, section, row)
                            Bridge:jumpTo("demo/" .. _dataDemo[row] .. ".lua")
                        end
                    }
                }
            })
            demoTableView:miniSpacing(1)
            if (not System:android()) then
                demoTableView:frame(0, 0, _screenWidth, _screenHeight*0.9)
            end
            demoTableView:backgroundColor(0xeeeeee)
            topContainer:addView(demoTableView)
        end

        if (widgetTableView) then
            widgetTableView:hide()
        end

        if (aboutView) then
            aboutView:hide()
        end
    end)
    tab3:callback(function()
        tab1:textColor(0x000000)
        tab2:textColor(0x000000)
        tab3:textColor(0xff0000)
        if (aboutView) then
            aboutView:show()
        else
            local xml = File:read("about.xml")
            pica:parseXml(xml)

            aboutView = pica:getViewByName("root")
            aboutView:frame(widgetTableView:frame())
            aboutView:backgroundColor(0xeeeeee)
            topContainer:addView(aboutView)

            local info = pica:getViewByName("info")
            if (System:android()) then
                info:nativeView():setLineSpacing(10,1)
            end
        end

        if (widgetTableView) then
            widgetTableView:hide()
        end

        if (demoTableView) then
            demoTableView:hide()
        end
    end)
end

main()