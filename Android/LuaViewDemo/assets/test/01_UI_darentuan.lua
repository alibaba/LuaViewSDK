--
-- Created by IntelliJ IDEA.
-- User: tuoli
-- Date: 11/15/16
-- Time: 10:16 AM
-- To change this template use File | Settings | File Templates.
--

--
-- Created by IntelliJ IDEA.
-- User: tuoli
-- Date: 11/4/16
-- Time: 7:00 PM
-- To change this template use File | Settings | File Templates.
--

gifPlayTime = 3;

w,h = System.screenSize();
imageUrl1 = "http://gju2.alicdn.com/bao/uploaded/i1/10000073270926575/TB2fpg0cXXXXXb6XpXXXXXXXXXX_!!0-0-juitemmedia.jpg"
imageUrl2 = "http://img4.duitang.com/uploads/item/201306/25/20130625045508_sairr.thumb.600_0.jpeg"

gifAddress = {
    "https://img.alicdn.com/tps/TB1s5.GOXXXXXbJaXXXXXXXXXXX-750-420.gif",
    "http://image58.360doc.com/DownloadImg/2013/01/0714/29443574_2.gif",
    "http://image.haha.mx/2014/02/02/middle/1115779_c221d1fc47b97bb1605cddc9c8aec0a7_1391347675.gif"
}
gifCells = {}

local function alphaAnimation(startAlpha, endAlpha, duration, endCallback)
    local animation = Animation().alpha(startAlpha, endAlpha).duration(duration).interpolator(Interpolator.ACCELERATE_DECELERATE).callback({
        onStart = function()
        end,
        onCancel = function()
        end,
        onEnd = function()
            endCallback()
        end,
        onPause = function()
        end,
        onResume = function()
        end,
    });
    return animation;
end

local function showGif(row)
    if (gifCells[row] == nil) then
        return;
    end

    for key, value in pairs(gifCells) do
        value.staticImg.alpha(1)
    end

    local callback = function()
        local timer = Timer()
        timer.callback(function()
            timer.cancel()
            alphaAnimation(0, 1, 1.5, nil).with(gifCells[row].staticImg).start()
        end)
        timer.start(gifPlayTime)
    end
    alphaAnimation(1, 0, 1.5, callback).with(gifCells[row].staticImg).start()
end

collectionView = CollectionView {
    Section = {
        SectionCount = 40,
        RowCount = function(section, row)
            if (section == 2) then
                return 4
            else
                return 10;
            end
        end,
    },
    Cell = {
        Id = function ( section, row )
            if (row == 1) then
                                return "PinnedCell1", Pinned.YES;
--                return "PinnedCell1"
            else
                if (section == 2) then
                    return "ImageAndLabel2"
                else
                    return "ImageAndLabel"
                end
            end
        end,
        PinnedCell1 = {
            Size = function(section, row)
                return w, 50;
            end,
            Init = function(cell, section, row)
                cell.title = Label();
                cell.title.frame(50, 0, 100, 50);
            end,
            Layout = function(cell, section, row)
                cell.title.text("Type" .. section);
                if (section % 2 == 1) then
                    cell.title.textColor(0x000309)
                else
                    cell.title.textColor(0x000000)
                end
                if (section % 2 == 1) then
                    cell.window.backgroundColor(0x80ffff)
                else
                    cell.window.backgroundColor(0x80f56f)
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
            Init = function(cell,section,row)
                cell.gifImg = Image();
                cell.gifImg.frame(10, 10, w-20, 200-20);
                cell.staticImg = Image();
                cell.staticImg.frame(10, 10, w-20, 200-20);
                cell.staticImg.image(imageUrl1);
                cell.gifImg.image(gifAddress[row-1]);
            end,
            Layout = function(cell , section, row)
                print("tuoli", section, row, "layout")
                if (gifCells[row] == nil) then
                    table:insert(gifCells, row, cell)
                end
            end,
        }
    },
    Callback = {
        Scrolling = function( firstVisibleSection, firstVisibleRow, visibleCellCount )
            --                        print("scrolling", firstVisibleSection,"---" ,firstVisibleRow, "---", visibleCellCount);
        end,
        ScrollBegin = function(firstVisibleSection, firstVisibleRow, visibleCellCount )
            --                        print("scrolling begin", firstVisibleSection,"---" ,firstVisibleRow, "---", visibleCellCount);
        end,
        ScrollEnd = function(firstVisibleSection, firstVisibleRow, visibleCellCount )
            print("scrolling end", firstVisibleSection,"---" ,firstVisibleRow, "---", visibleCellCount);
            if (firstVisibleSection == 2) then
                showGif(firstVisibleRow+1)
            end
            if (firstVisibleSection == 1 and firstVisibleRow == 10) then
                showGif(2)
            end
        end,
        PullDown = function()
            collectionView.stopPullDownRefreshing()
            collectionView.reload()
        end
    }
};
collectionView.frame(0,0,w,h-64);
collectionView.backgroundColor(0xffFFFF);
collectionView.miniSpacing(0)

