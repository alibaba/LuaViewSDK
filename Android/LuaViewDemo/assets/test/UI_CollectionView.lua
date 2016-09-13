w, h = System.screenSize();
cellHeight = 100
imageUrl1 = "http://gju2.alicdn.com/bao/uploaded/i1/10000073270926575/TB2fpg0cXXXXXb6XpXXXXXXXXXX_!!0-0-juitemmedia.jpg"
imageUrl2 = "http://img4.duitang.com/uploads/item/201306/25/20130625045508_sairr.thumb.600_0.jpeg"

rowCount1 = 1
rowCount2 = 1
rowTitle = "x"
tableView = CollectionView {
    Section = {
        SectionCount = 2,
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
                return "ImageAndLabel";
            else
                return "ImageAndImage";
            end
        end,
        ImageAndLabel = {
            Size = function(section, row)
                return w, cellHeight;
            end,
            Init = function(cell, section, row)
                cell.icon = Image();
                cell.title = Label();
                print("构造Cell");

                cell.bg = Button()
            end,
            Layout = function(cell, section, row)
                cell.icon.frame(20, 0, cellHeight, cellHeight);
                cell.icon.image(imageUrl1, function()
                    print("imageLoaded-ImageAndLabel", section, row)
                end);

                cell.title.frame(20 + cellHeight, 0, w - cellHeight, cellHeight);
                cell.title.text("测试" .. section .. "--" .. row .. "--" .. rowTitle);


                cell.bg.frame(0, 0, w, cellHeight)
                cell.bg.image(nil, imageUrl1)
            end,
            Callback = function(section, row)
                print(section, row);
                tableView.stopRefreshing();
                System.gc();
            end
        },
        ImageAndImage = {
            Size = function(section, row)
                return cellHeight;
            end,
            Init = function(cell, section, row)
                cell.icon = Image();
                cell.icon2 = Image();
            end,
            Layout = function(cell, section, row)
                local cellWdith, cellHeight = cell.window.size();
                cell.icon.frame(0, 0, cellHeight, cellHeight);
                cell.icon.image(imageUrl1, function()
                    print("imageLoaded-ImageAndImage-1", section, row)
                end);


                cell.icon2.frame(cellHeight + 10, 0, cellHeight, cellHeight);
                cell.icon2.image(imageUrl1, function()
                    print("imageLoaded-ImageAndImage-2", section, row)
                end);
            end,
            Callback = function(section, row)
                print(section, row);
                tableView.stopRefreshing();
                System.gc();
            end
        }
    }
};

tableView.frame(0, 0, w, h - 64);


local btn = Button()
btn.text("reload")
btn.frame(0, 0, 50, 50)
btn.callback(function()
    tableView.realod()
end)

local btn2 = Button()
btn2.text("+rows")
btn2.frame(51, 0, 50, 50)
btn2.callback(function()
    rowCount1 = rowCount1 + 1
    tableView.realod(1)
end)

local btn3 = Button()
btn3.text("-rows")
btn3.frame(102, 0, 50, 50)
btn3.callback(function()
    rowCount1 = rowCoun1 - 1
    rowCount1 = math:max(rowCount1, 1)
    tableView.reload(1)
end)

local btn4 = Button()
btn4.text("update-rows")
btn4.frame(153, 0, 50, 50)
btn4.callback(function()
    rowTitle = rowTitle .. "y"
    tableView.reload(1)
end)

local btn5 = Button()
btn5.text("+row")
btn5.frame(0, 51, 50, 50)
btn5.callback(function()
    rowCount1 = rowCount1 + 1
    tableView.reload(1, rowCount1)
end)

local btn6 = Button()
btn6.text("-row")
btn6.frame(51, 51, 50, 50)
btn5.callback(function()
    rowCount1 = rowCount1 - 1
    rowCount1 = math:max(rowCount1, 1)
    tableView.reload(1, rowCount1)
end)

local btn7 = Button()
btn7.text("update-row")
btn7.frame(102, 51, 50, 50)
btn7.callback(function()
    rowTitle = rowTitle .. "x"
    tableView.reload(1, 1)
end)




