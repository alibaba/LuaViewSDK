--
-- Created by IntelliJ IDEA.
-- User: tuoli
-- Date: 17/2/28
-- Time: 16:52
-- To change this template use File | Settings | File Templates.
--

Navigation:title("Image.lua")

local _screenWidth, _screenHeight = System:screenSize()

-- 减掉ActionBar和StatusBar的高度
if (System:android()) then
    _screenHeight = _screenHeight - 80 -- Android, 不同机型, 高度不定, 比较蛋疼
else
    _screenHeight = _screenHeight - 64      -- iOS, 稳定在这个值
end

local function start()
    local pica = require("kit.pica")
    tableView = CollectionView({
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
                return "ImageCell"
            end,
            ImageCell = {
                Size = function(section, row)
                    return _screenWidth, _screenHeight + _screenHeight/2
                end,
                Init = function(cell, section, row)
                    local xml = File:read("xml/image.xml")
                    pica:parseXml(xml)
                end
            }
        }
    })
    tableView:frame(0, 0, _screenWidth, _screenHeight)
end

start()
