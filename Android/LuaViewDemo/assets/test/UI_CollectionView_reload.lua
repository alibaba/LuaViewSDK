-- Created by LuaView.
-- Copyright (c) 2017, Alibaba Group. All rights reserved.
--
-- This source code is licensed under the MIT.
-- For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.

w, h = System.screenSize()
cellHeight = 100
imageUrl1 = "http://gju2.alicdn.com/bao/uploaded/i1/10000073270926575/TB2fpg0cXXXXXb6XpXXXXXXXXXX_!!0-0-juitemmedia.jpg"
imageUrl2 = "http://img4.duitang.com/uploads/item/201306/25/20130625045508_sairr.thumb.600_0.jpeg"

sectionCount = 2
rowCount1 = 1
rowCount2 = 1
rowTitle = "x"
tableView = CollectionView {
    Section = {
        SectionCount = function()
            return sectionCount
        end,
        RowCount = function(section)
            if(section == 1) then
                return rowCount1
            else
                return rowCount2
            end
        end
    },
    Cell = {
        Id = function(section, row)
            if (section == 1) then
                return "ImageAndLabel"
            elseif(section == 2) then
                return "ImageAndImage"
            else
                return "Label"
            end
        end,
        Label = {
            Size = function(section, row)
                return w, cellHeight
            end,
            Init = function(cell, section, row)
                cell.title = Label()
            end,
            Layout = function(cell, section, row)
                cell.title.frame(0, 0, w - cellHeight, cellHeight)
                cell.title.text("测试" .. section .. "--" .. row .. "--" .. rowTitle)
                cell.title.backgroundColor(0xdcdcdc)
            end
        },
        ImageAndLabel = {
            Size = function(section, row)
                return w, cellHeight
            end,
            Init = function(cell, section, row)
                cell.icon = Image()
                cell.title = Label()
                cell.bg = Button()
            end,
            Layout = function(cell, section, row)
                cell.icon.frame(20, 0, cellHeight, cellHeight)
                cell.icon.image(imageUrl1)

                cell.title.frame(20 + cellHeight, 0, w - cellHeight, cellHeight)
                cell.title.text("测试" .. section .. "--" .. row .. "--" .. rowTitle)

                cell.bg.frame(0, 0, w, cellHeight)
                cell.bg.image(nil, imageUrl1)
            end,
            Callback = function(section, row)
                print(section, row)
                tableView.stopRefreshing()
                System.gc()
            end
        },
        ImageAndImage = {
            Size = function(section, row)
                return cellHeight
            end,
            Init = function(cell, section, row)
                cell.icon = Image()
                cell.icon2 = Image()
            end,
            Layout = function(cell, section, row)
                local cellWdith, cellHeight = cell.window.size()
                cell.icon.frame(0, 0, cellHeight, cellHeight)
                cell.icon.image(imageUrl1)


                cell.icon2.frame(cellHeight + 10, 0, cellHeight, cellHeight)
                cell.icon2.image(imageUrl1)
            end,
            Callback = function(section, row)
                print(section, row)
                tableView.stopRefreshing()
                System.gc()
            end
        }
    }
}

tableView.frame(0, 152, w, h - 64 - 152)


local btn = Button()
btn.text("reload")
btn.frame(0, 0, 80, 50)
btn.callback(function()
    print("reload")
    tableView.reload()
end)

local btn2 = Button()
btn2.text("+rows")
btn2.frame(81, 0, 80, 50)
btn2.callback(function()
    rowCount1 = rowCount1 + 2
    print("+rows", 1, rowCount1)
    tableView.reload(1)
end)

local btn3 = Button()
btn3.text("-rows")
btn3.frame(162, 0, 80, 50)
btn3.callback(function()
    rowCount1 = rowCount1 - 2
    rowCount1 = math:max(rowCount1, 1)
    print("-rows", 1, rowCount1)
    tableView.reload(1)
end)

local btn4 = Button()
btn4.text("update-rows")
btn4.frame(243, 0, 80, 50)
btn4.callback(function()
    rowTitle = rowTitle .. "y"
    print("update-rows", 1)
    tableView.reload(1)
end)

local btn5 = Button()
btn5.text("+row")
btn5.frame(0, 51, 80, 50)
btn5.callback(function()
    rowCount1 = rowCount1 + 1
    print("+row", 1, rowCount1)
    tableView.reload(1, 1)
end)

local btn6 = Button()
btn6.text("-row")
btn6.frame(81, 51, 80, 50)
btn6.callback(function()
    rowCount1 = rowCount1 - 1
    rowCount1 = math:max(rowCount1, 1)
    print("-row", 1, rowCount1)
    tableView.reload(1, 1)
end)

local btn7 = Button()
btn7.text("update-row")
btn7.frame(162, 51, 80, 50)
btn7.callback(function()
    rowTitle = rowTitle .. "x"
    print("update-row", 1, 1)
    tableView.reload(1, 1)
end)


local btn8 = Button()
btn8.text("+section")
btn8.frame(0, 102, 80, 50)
btn8.callback(function()
    sectionCount = sectionCount + 1
    print("+section", sectionCount)
    tableView.reload(sectionCount)
end)

local btn9 = Button()
btn9.text("-section")
btn9.frame(91, 102, 80, 50)
btn9.callback(function()
    sectionCount = sectionCount - 1
    sectionCount = math:max(sectionCount, 1)
    print("-section", sectionCount)
    tableView.reload(sectionCount)
end)

