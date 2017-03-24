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
    _pica:parseXml("widget/list.xml")

    local header = _pica:getViewByName("headerText")
    tableView = _pica:getViewByName("tableView")

    local timer = Timer()
    timer:callback(function()
        tableView:stopRefreshing()
        timer:cancel()
    end)

    local tableData = {
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
                    return Platform.contentWidth, 60
                end,
                Init = function(cell, section, row)
                    cell.window:frame(0, 0, Platform.contentWidth, 60)
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
    }

    tableView:initParams(tableData)
    tableView:reload()
end

Navigation:title("List.lua")
start()

