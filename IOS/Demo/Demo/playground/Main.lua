--
-- Created by IntelliJ IDEA.
-- User: tuoli
-- Date: 2/7/17
-- Time: 18:12
-- To change this template use File | Settings | File Templates.
--

Navigation:title("Main.lua")

local _jsonData = ' ["00xml", "Label", "Button", "Image", "TextField", "Loading", "List", "ListPullDown", ' ..
                    ' "Slider", "Web", "Animation", "Http", "Timer", "Storage", "Audio", ' ..
                    ' "Tabbar", "Navigator", "Picker", "Dropdown", "Clipboard", "Video", "Camera"] '

local _data = Json:toTable(_jsonData)

local _screenWidth, _screenHeight = System:screenSize()

-- 减掉ActionBar和StatusBar的高度
if (System:android()) then
    _screenHeight = _screenHeight - 80 -- Android, 不同机型, 高度不定, 比较蛋疼
else
    _screenHeight = _screenHeight - 64      -- iOS, 稳定在这个值
end

local function main()
    local tableData = {
        Section = {
            SectionCount = function()
                return 1
            end,
            RowCount = function(section)
                return table.getn(_data)
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
                    cell.label = Label()
                    cell.label:frame(15, 15, _screenWidth-15*2, 80-15*2)
                end,
                Layout = function(cell, section, row)
                    local style = StyledString(_data[row], { fontSize = 25, fontColor = 0x000000})
                    cell.label:text(style)
                end,
                Callback = function(cell, section, row)
                    Bridge:jumpTo(_data[row] .. ".lua")
                end
            }
        }
    }
    local tableView = CollectionView(tableData)
    tableView:frame(0, 0, _screenWidth, _screenHeight)
    tableView:miniSpacing(1)
end

main()