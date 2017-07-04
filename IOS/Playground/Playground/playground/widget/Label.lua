--
-- Copyright 2017 Alibaba Group
-- License: MIT
-- Website: https://alibaba.github.io/LuaViewSDK
-- User: tuoli
-- Date: 17/3/30
--

Navigation:title("Label.lua")

local meta = object:new()

function meta:onCreate(args)
    self.list = CollectionView()
    self.list:frame(0, 0, sys.contW, sys.contH)
    self.list:initParams({
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
                    return sys.contW, sys.contH*2.5
                end,
                Init = function(cell, section, row)
                    cell.objs = pica:getInstance():render("widget/label_item.xml")

                    local fs1 = cell.objs["fs1"]
                    fs1:text(StyledString("normal", {fontStyle = "normal", fontSize=25*sys.scale }))
                    local fs2 = cell.objs["fs2"]
                    fs2:text(StyledString("bold", {fontStyle = "bold", fontSize=25*sys.scale}))
                    local fs3 = cell.objs["fs3"]
                    fs3:text(StyledString("italic", {fontStyle = "italic", fontSize=25*sys.scale}))
                    local td1 = cell.objs["td1"]
                    td1:text(StyledString("strikethrough", {strikethrough = true, fontSize=25*sys.scale}))
                    local td2 = cell.objs["td2"]
                    td2:text(StyledString("underline", {underline = true, fontSize=25*sys.scale}))
                end
            }
        }
    })
end

return meta
