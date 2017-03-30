--
-- Copyright 2017 Alibaba Group
-- License: MIT
-- Website: https://alibaba.github.io/LuaViewSDK
-- User: tuoli
-- Date: 17/3/30
--

require("kit.pica")

local function start()
    local tableData = {
        Section = {
            SectionCount = function()
                return 1
            end,
            RowCount = function(section)
                return 1
            end
        },
        Cell = {
            Id = function(section, row)
                return "LabelCell"
            end,
            LabelCell = {
                Size = function(section, row)
                    return Sys.contW, Sys.contH*2.5
                end,
                Init = function(cell, section, row)
                    cell.objs = Pica:getInstance():render("widget/label_item.xml")

                    local fs1 = cell.objs["fs1"]
                    fs1:text(StyledString("normal", {fontStyle = "normal", fontSize=25*Sys.scale }))
                    local fs2 = cell.objs["fs2"]
                    fs2:text(StyledString("bold", {fontStyle = "bold", fontSize=25*Sys.scale}))
                    local fs3 = cell.objs["fs3"]
                    fs3:text(StyledString("italic", {fontStyle = "italic", fontSize=25*Sys.scale}))
                    local td1 = cell.objs["td1"]
                    td1:text(StyledString("strikethrough", {strikethrough = true, fontSize=25*Sys.scale}))
                    local td2 = cell.objs["td2"]
                    td2:text(StyledString("underline", {underline = true, fontSize=25*Sys.scale}))
                end
            }
        }
    }
    tableView = CollectionView(tableData)
    tableView:frame(0, 0, Sys.contW, Sys.contH)
end

Navigation:title("Label.lua")
start()
