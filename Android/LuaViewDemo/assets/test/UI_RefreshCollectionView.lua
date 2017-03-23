-- Created by LuaView.
-- Copyright (c) 2017, Alibaba Group. All rights reserved.
--
-- This source code is licensed under the MIT.
-- For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.

w, h = System.screenSize();

cellHeight = 100
imageUrl1 = "http://gju2.alicdn.com/bao/uploaded/i1/10000073270926575/TB2fpg0cXXXXXb6XpXXXXXXXXXX_!!0-0-juitemmedia.jpg"
imageUrl2 = "http://img4.duitang.com/uploads/item/201306/25/20130625045508_sairr.thumb.600_0.jpeg"

collectionView = RefreshCollectionView {
    Section = {
        SectionCount = 10,
        RowCount = function(section)
            if(section == 4 or section == 6) then
                return 10;
            end
            return 1
        end
    },
    Cell = {
        Id = function(section, row)
            if (section == 4) then
                return "ImageAndLabel";
            elseif(section == 6) then
                return "ImageAndImage";
            end
            return "NONE"
        end,
        NONE = {
            Init = function(cell)
            end,
            Layout = function(cell, section, row)
                cell.line = View();
                cell.line.size(w, 1)
                cell.line.backgroundColor(0xff0000);
            end,
            Size = function(section, row)
                return w, 1;
            end,
            Callback = function(cell, section, row)
            end

        },
        ImageAndLabel = {
            Size = function(section, row)
                return 50, cellHeight;
            end,
            Init = function(cell, section, row)
                cell.bg = View()
                cell.icon = Image();
                cell.title = Label();
                print("构造Cell");
            end,
            Layout = function(cell, section, row)
                cell.icon.frame(0, 0, 20, 50);
                cell.icon.image(imageUrl1, function()
                    print("imageLoaded-ImageAndLabel", section, row)
                end);

                cell.title.frame(20, 0, w - 20, cellHeight);
                cell.title.text("测试" .. section .. "--" .. row);

                cell.bg.addView(cell.bg)
            end,
            Callback = function(section, row)
                print(section, row);
                collectionView.stopRefreshing();
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
                print(window);
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
                collectionView.stopRefreshing();
                System.gc();
            end
        }
    },
    Callback = {
        Scrolling = function()
            print("scrolling");
        end,
        ScrollBegin = function()
            print("scrolling begin");
        end,
        ScrollEnd = function()
            print("scrolling end");
        end,
        PullDown = function()
            print("PullDown");
        end
    }
};

loading = false;

collectionView.frame(0, 0, w, h - 64);
collectionView.showScrollIndicator(false)



