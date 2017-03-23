--
-- Created by IntelliJ IDEA.
-- User: tuoli
-- Date: 17/2/28
-- Time: 16:53
-- To change this template use File | Settings | File Templates.
--

Navigation:title("List.lua")

local _screenWidth, _screenHeight = System:screenSize()

-- 减掉ActionBar和StatusBar的高度
if (System:android()) then
    local device = System:device()
    _screenHeight = device.window_height - device.status_bar_height - device.nav_height
else
    _screenHeight = _screenHeight - 64      -- iOS, 稳定在这个值
end

local function start()
    local pica = require("kit.pica")
    local xml = File:read("widget/list.xml")
    pica:parseXml(xml)
    local header = pica:getViewByName("headerText")
    local tableContainer = pica:getViewByName("tableContainer")

    local timer = Timer()
    timer:callback(function()
        tableView:stopRefreshing()
        timer:cancel()
    end)

    tableView = RefreshCollectionView({
        Section = {
            SectionCount = function()
                return 1
            end,
            RowCount = function(section)
                return 20
            end
        },
        Cell = {
            Id = function(section, row)
                return "RowCell"
            end,
            RowCell = {
                Size = function(section, row)
                    return _screenWidth, 60
                end,
                Init = function(cell, section, row)
                    cell.window:frame(0, 0, _screenWidth, 60)
                    cell.window:flexCss("flex-direction: row")
                    cell.window:backgroundColor(0xffffff)

                    local label = Label()
                    label:flexCss("margin: 15, flex: 1")

                    cell.label = label
                    cell.window:flexChildren(cell.label)
                    cell.window:flxLayout(true) -- iOS
                end,
                Layout = function(cell, section, row)
                    local style = StyledString("row " .. row, { fontSize = 16, fontColor = 0x000000})
                    cell.label:text(style)
                end
            }
        },
        Callback = {
            Scrolling = function( firstVisibleSection, firstVisibleRow, visibleCellCount )
                header:text("Visible Items: " .. firstVisibleRow .. ", " .. firstVisibleRow + visibleCellCount)
            end,
            PullDown = function()
                timer:start(3)
            end
        }
    })
    tableView:backgroundColor(0xeeeeee)
    tableView:miniSpacing(1)
    tableView:showScrollIndicator(false)
    tableView:frame(0, 0, _screenWidth, _screenHeight - 50)
    tableContainer:addView(tableView)
end

start()

