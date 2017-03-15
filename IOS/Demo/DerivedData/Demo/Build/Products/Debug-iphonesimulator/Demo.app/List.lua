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
    _screenHeight = _screenHeight - 80      -- Android, 不同机型, 高度不定, 比较蛋疼, 最好是底层直接计算好
else
    _screenHeight = _screenHeight - 64      -- iOS, 稳定在这个值
end

local function start()
    local tableView = CollectionView({
        Section = {
            SectionCount = function()
                return 5
            end,
            RowCount = function(section)
                return 20
            end
        },
        Cell = {
            Id = function(section, row)
                if (row == 1) then
                    return "HeaderCell", Pinned.YES
                else
                    return "RowCell"
                end
            end,
            HeaderCell = {
                Size = function(section, row)
                    return _screenWidth, 60
                end,
                Init = function(cell, section, row)
                    cell.window:frame(0, 0, _screenWidth, 60)
                    cell.window:flexCss("flex-direction: row")
                    cell.window:backgroundColor(0x0000ff)

                    local label = Label()
                    label:flexCss("flex: 1")
                    label:textAlign(TextAlign.CENTER)

                    cell.label = label
                    cell.window:flexChildren(cell.label)
                    cell.window:flxLayout(true) -- iOS
                end,
                Layout = function(cell, section, row)
                    local style = StyledString("section " .. section .. " row " .. row, { fontSize = 25, fontColor = 0xffffff})
                    cell.label:text(style)
                end
            },
            RowCell = {
                Size = function(section, row)
                    return _screenWidth, 80
                end,
                Init = function(cell, section, row)
                    cell.window:frame(0, 0, _screenWidth, 80)
                    cell.window:flexCss("flex-direction: row")

                    local label = Label()
                    label:flexCss("margin: 15, flex: 1")

                    cell.label = label
                    cell.window:flexChildren(cell.label)
                    cell.window:flxLayout(true) -- iOS
                end,
                Layout = function(cell, section, row)
                    local style = StyledString("section " .. section .. " row " .. row, { fontSize = 25, fontColor = 0x000000})
                    cell.label:text(style)
                end
            }
        }
    })
    tableView:flexCss("flex: 1")
    tableView:miniSpacing(1)
    tableView:showScrollIndicator(false)
end

start()

