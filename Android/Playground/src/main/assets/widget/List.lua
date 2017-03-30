--
-- Copyright 2017 Alibaba Group
-- License: MIT
-- Website: https://alibaba.github.io/LuaViewSDK
-- User: tuoli
-- Date: 17/3/30
--

require("kit.pica")

local function start()
    listObjs = Pica:getInstance():render("widget/list.xml")

    local tableView = listObjs["tableView"]
    local header = listObjs["headerText"]
    if (not Sys.android) then
        -- iOS还有bug，暂时屏蔽
        header:hide()
    end

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
                    return Sys.contW, Sys.contH/10
                end,
                Init = function(cell, section, row)
                    cell.window:frame(0, 0, Sys.contW, Sys.contH/10)
                    cell.window:flexCss("flex-direction: row")
                    cell.window:backgroundColor(0xffffff)

                    local label = Label()
                    label:flexCss("margin: 15, flex: 1")

                    cell.label = label
                    cell.window:flexChildren(cell.label)
                    cell.window:flxLayout(true) -- iOS
                end,
                Layout = function(cell, section, row)
                    local style = StyledString("row " .. row, { fontSize = 16*Sys.scale, fontColor = 0x000000})
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

