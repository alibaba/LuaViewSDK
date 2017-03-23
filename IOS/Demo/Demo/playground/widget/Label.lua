--
-- Created by IntelliJ IDEA.
-- User: tuoli
-- Date: 2/7/17
-- Time: 16:30
-- To change this template use File | Settings | File Templates.
--

require("kit.pica_new")

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
                    return Platform.contentWidth, Platform.contentHeight*2.5
                end,
                Init = function(cell, section, row)
                    cell.objs = Pica:getInstance():render("widget/label_item.xml")

                    local fs1 = cell.objs["fs1"]
                    fs1:text(StyledString("normal", {fontStyle = "normal", fontSize=25}))
                    local fs2 = cell.objs["fs2"]
                    fs2:text(StyledString("bold", {fontStyle = "bold", fontSize=25}))
                    local fs3 = cell.objs["fs3"]
                    fs3:text(StyledString("italic", {fontStyle = "italic", fontSize=25}))
                    local td1 = cell.objs["td1"]
                    td1:text(StyledString("strikethrough", {strikethrough = true, fontSize=25}))
                    local td2 = cell.objs["td2"]
                    td2:text(StyledString("underline", {underline = true, fontSize=25}))
                end
            }
        }
    }
    tableView = CollectionView(tableData)
    tableView:frame(0, 0, Platform.contentWidth, Platform.contentHeight)
end

Navigation:title("Label.lua")
start()
