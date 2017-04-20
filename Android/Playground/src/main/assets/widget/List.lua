--
-- Copyright 2017 Alibaba Group
-- License: MIT
-- Website: https://alibaba.github.io/LuaViewSDK
-- User: tuoli
-- Date: 17/3/30
--

Navigation:title("List.lua")

local meta = object:new()

function meta:onCreate(args)
    self.views = pica:getInstance():render("widget/list.xml")
    self.list = self.views["tableView"]
    self.header = self.views["headerText"]

    self:handle()
end

function meta:handle()
    self.list:initParams({
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
                    return sys.contW, sys.contH/10
                end,
                Init = function(cell, section, row)
                    cell.window:frame(0, 0, sys.contW, sys.contH/10)
                    cell.window:flexCss("flex-direction: row")
                    cell.window:backgroundColor(0xffffff)

                    local label = Label()
                    label:flexCss("margin: 15, flex: 1")

                    cell.label = label
                    cell.window:flexChildren(cell.label)
                    cell.window:flxLayout(true) -- iOS
                end,
                Layout = function(cell, section, row)
                    local style = StyledString("row " .. row, { fontSize = 16*sys.scale, fontColor = 0x000000})
                    cell.label:text(style)
                end
            }
        },
        Callback = {
            Scrolling = function( firstVisibleSection, firstVisibleRow, visibleCellCount )
                self.header:text("Visible Items: " .. firstVisibleSection * firstVisibleRow .. ", " .. firstVisibleRow + visibleCellCount - 1)
            end
        }
    })
end

return meta

