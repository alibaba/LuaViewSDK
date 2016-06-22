--工具包引入
require "kit/lv_kit"

local width, height = System.screenSize()
local w, h = System.screenSize()
local cellHeigth = 50
imageUrl1 = "http://gju2.alicdn.com/bao/uploaded/i1/10000073270926575/TB2fpg0cXXXXXb6XpXXXXXXXXXX_!!0-0-juitemmedia.jpg"
imageUrl2 = "http://img4.duitang.com/uploads/item/201306/25/20130625045508_sairr.thumb.600_0.jpeg"


function createCollectionView()
    local tableView = RefreshCollectionView {
        Section = {
            SectionCount = 3,
            RowCount = function(section)
                if(section == 1) then
                    return 1
                elseif(section == 2) then
                    return 10
                elseif(section == 3) then
                    return 10
                end
            end
        },
        Cell = {
            Id = function(section, row)
                if(section == 1) then
                    return "HScrollView"
                elseif (section == 2) then
                    return "ImageAndLabel";
                else
                    return "ImageAndImage";
                end
            end,
            HScrollView = {
                Size = function(section, row)
                    return width, cellHeigth;
                end,
                Init = function(cell, section, row)

                    local bg = View();
                    local scrollView = HScrollView();
                    for i = 0, 10, 1 do
                        local btn = Label()
                        btn.text("BTN" .. i)
                        btn.frame(i * 40, 0, 100, cellHeigth)
                        btn.backgroundColor(0xff00ff00)
                        scrollView.addView(btn)
                    end

                    scrollView.backgroundColor(0xffcccccc)
                    scrollView.frame(0, 0, width, cellHeigth)

                    bg.addView(scrollView)
                    bg.frame(0, 0, width, cellHeigth)

                    cell.content= bg
                end,
                Layout = function(cell, section, row)

                end
            },
            ImageAndLabel = {
                Size = function(section, row)
                    return 50, cellHeight;
                end,
                Init = function(cell, section, row)
                    cell.icon = Image();
                    cell.title = Label();
                end,
                Layout = function(cell, section, row)
                    cell.icon.frame(0, 0, 20, 50);
                    cell.icon.image(imageUrl1, function()
                        print("imageLoaded-ImageAndLabel", section, row)
                    end);

                    cell.title.frame(20, 0, w - 20, cellHeight);
                    cell.title.text("测试" .. section .. "--" .. row);
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
                end
            }
        }
    };

    tableView.frame(0, 0, w, h - 64);
    tableView.backgroundColor(0xffFFFF);

    return tableView
end


local vp = PagerView({
    PageCount = 12,
    Pages = {
        Title = function(pos)
            return "Title"
        end,
        Init = function(page, pos)
            page.collectionView = createCollectionView()
        end,
        Layout = function(page, pos)

        end
    }
})

vp.setBackgroundColor(0xaaaa0000)
vp.setFrame(0, 0, width, height)
