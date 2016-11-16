--
-- Created by IntelliJ IDEA.
-- User: tuoli
-- Date: 11/4/16
-- Time: 7:00 PM
-- To change this template use File | Settings | File Templates.
--

w,h = System.screenSize();
imageUrl1 = "http://gju2.alicdn.com/bao/uploaded/i1/10000073270926575/TB2fpg0cXXXXXb6XpXXXXXXXXXX_!!0-0-juitemmedia.jpg"
imageUrl2 = "http://img4.duitang.com/uploads/item/201306/25/20130625045508_sairr.thumb.600_0.jpeg"
collectionView = RefreshCollectionView {
    Section = {
        SectionCount = 26,
        RowCount = function(section, row)
            return 8;
        end,
    },
    Cell = {
        Id = function ( section, row )
            if (row == 2) then
                return "PinnedCell1", Pinned.YES;
--            if (section == 2 and row == 1) then
--                return "PinnedScrollCell", Pinned.YES;
            else
                return "ImageAndLabel"
            end
        end,
        PinnedScrollCell = {
            Size = function(section, row)
                return w, 60;
            end,
            Init = function(cell, section, row)
                cell.window.backgroundColor(0xf0ff97)
                cell.scrollView = HScrollView();
                cell.scrollView.frame(0, 0, w, 60);
                cell.views = {};
                for i = 0, 30, 1 do
                    local btn = Label()
                    btn.text(section .. " BTN " .. i)
                    btn.frame(0, 0, 60, 40)
                    btn.backgroundColor(0xff00ff00)
                    local view = View()
                    view.frame(i * 60, 10, 60, 50)
                    view.callback(
                        function()
                            Toast(btn.text())
                            cell.window.backgroundColor(0x032f00)
                        end
                    )
                    view.addView(btn)
                    cell.scrollView.addView(view)
                    cell.views[i] = view;
                end
                cell.scrollView.contentSize(30*60,0);
            end,
            Layout = function(cell, section, row)
            end
        },
        PinnedCell1 = {
            Size = function(section, row)
                return w, 50;
            end,
            Init = function(cell, section, row)
                cell.title = Label();
                cell.title.frame(50, 0, 100, 50);
                cell.title.backgroundColor(0xffffff)
                if (section % 2 == 1) then
                    cell.window.backgroundColor(0x80ffff)
                else
                    cell.window.backgroundColor(0x80f56f)
                end
            end,
            Layout = function(cell, section, row)
                cell.title.text("Type " .. section);
                if (section % 2 == 1) then
                    cell.title.textColor(0x00f309)
                else
                    cell.title.textColor(0x000000)
                end

            end,

            Callback = {
                Click = function()
                end,
                LongClick = function()
                end
            }
        },
        ImageAndLabel = {
            Size = function(section, row)
                return w ,200;
            end,
            Init = function(cell, section, row)
                local cellWidth ,cellHeight = cell.window.size();
                cellHeight = cellHeight / 2;
                cell.icon = Image();
                cell.icon.frame(0, 0, cellHeight, cellHeight);
                cell.title = Label();
                cell.title.frame(0, cellHeight, cellHeight, cellHeight/2);
                cell.title.textColor(0xffFFFF);
                cell.title.backgroundColor(0xff00ff);
            end,
            Layout = function(cell , section, row)
                cell.icon.image(imageUrl1, function()
                    local x,y,w,h = cell.icon.frame();
                end);
                cell.title.text("测试"..section .."--" .. row);
                cell.window.backgroundColor( section*0x770000 +  (row%3)*0x33 );
            end,
            Callback = function(cell, section, row)
                print(section, row);
                System.gc();
                collectionView.scrollToCell(section, row);
            end
        },
        ImageAndLabel2 = {
            Size = function(section, row)
                return w ,200;
            end,
            Init = function(cell)
                local cellHeight = 100
                cell.icon = Image();
                cell.icon.frame(w*0.05, 10, cellHeight, cellHeight);
                cell.icon2 = Image();
                cell.icon2.frame(160, 0, cellHeight, cellHeight);
                cell.button = Button();
                cell.button.frame(0,0,100,60);
                cell.button.backgroundColor(0x777777);
                cell.button.callback(
                    function()
                        Toast("hhhhhhh");
                    end);
            end,
            Layout = function(cell , section, row)
                cell.icon.image(
                    imageUrl1,
                    function()
                        local x,y,w,h = cell.icon.frame();
                    end);
                cell.icon2.image(imageUrl1)
                cell.window.backgroundColor( section*0x770000 +  (row%3)*0x33 );
            end,
            Callback = {
                Click = function()
                    print("tuoli ImageAndLabel2 Click")
                end,
                LongClick = function()
                end
            }
        }
    },
    Callback = {
        Scrolling = function( firstVisibleSection, firstVisibleRow, visibleCellCount )
--            print("scrolling", firstVisibleSection,"---" ,firstVisibleRow, "---", visibleCellCount);
        end,
        ScrollBegin = function(firstVisibleSection, firstVisibleRow, visibleCellCount )
--            print("scrolling begin", firstVisibleSection,"---" ,firstVisibleRow, "---", visibleCellCount);
        end,
        ScrollEnd = function(firstVisibleSection, firstVisibleRow, visibleCellCount )
--            print("scrolling end", firstVisibleSection,"---" ,firstVisibleRow, "---", visibleCellCount);
        end,
        PullDown = function()
            collectionView.stopRefreshing()
            collectionView.reload()
        end
    }
};
collectionView.frame(0,0,w,h-64);
collectionView.backgroundColor(0xffFFFF);
collectionView.miniSpacing(5)

