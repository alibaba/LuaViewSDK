--
-- Created by IntelliJ IDEA.
-- User: tuoli
-- Date: 2/7/17
-- Time: 16:30
-- To change this template use File | Settings | File Templates.
--

Navigation:title("Label.lua")

local _screenWidth, _screenHeight = System:screenSize()

-- 减掉ActionBar和StatusBar的高度
if (System:android()) then
    _screenHeight = _screenHeight - 80 -- Android, 不同机型, 高度不定, 比较蛋疼
else
    _screenHeight = _screenHeight - 64      -- iOS, 稳定在这个值
end

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
                    return _screenWidth, _screenHeight*2.5
                end,
                Init = function(cell, section, row)
                    local pica = require("kit.pica")

                    print("tuoli", "xml read start")
                    local callback = function(data)
                        print("tuoli", "xml read end")
                        pica:parseXml(data)
                        local fs1 = pica:getViewByName("fs1")
                        fs1:text(StyledString("normal", {fontStyle = "normal"}))
                        local fs2 = pica:getViewByName("fs2")
                        fs2:text(StyledString("bold", {fontStyle = "bold"}))
                        local fs3 = pica:getViewByName("fs3")
                        fs3:text(StyledString("italic", {fontStyle = "italic"}))
                        local td1 = pica:getViewByName("td1")
                        td1:text(StyledString("strikethrough", {strikethrough = true}))
                        local td2 = pica:getViewByName("td2")
                        td2:text(StyledString("underline", {underline = true}))
                    end
                    File:read("widget/label.xml", callback)  -- 有时候会返回nil
                end
            }
        }
    }
    tableView = CollectionView(tableData)
    tableView:frame(0, 0, _screenWidth, _screenHeight)
end

start()
