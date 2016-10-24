--工具包引入
require "kit/lv_kit"

w, h = System.screenSize()

cellHeight = 100
imageUrl1 = "http://gju2.alicdn.com/bao/uploaded/i1/10000073270926575/TB2fpg0cXXXXXb6XpXXXXXXXXXX_!!0-0-juitemmedia.jpg"
imageUrl2 = "http://img4.duitang.com/uploads/item/201306/25/20130625045508_sairr.thumb.600_0.jpeg"

local tableView = RefreshCollectionView {
    Section = {
        SectionCount = 5,
        RowCount = function(section)
            if(section <= 2) then
                return section
            else
                return 0
            end
        end
    },
    Cell = {
        Id = function(section, row)
--            if(section == 1 and row == 1) then
--                return "None"
--            end
--            if(row % 4 == 0) then
--                return "Pager"
--            elseif (row % 4 == 1) then
--                return "Image"
--            elseif (row % 4 == 2) then
--                return "ImageAndImage"
--            elseif (row % 4 == 3) then
--                return "Image"
--            end

            if(row == 1) then
                return "None"
            else
                return "Image"
            end

        end,
        None = {
            Size = function(section, row)
                return 0, 0
            end,
            Init = function(cell, section, row)
                cell.bg = View()
                cell.bg.backgroundColor(0xff0000)
            end,
            Layout = function(cell, section, row)
                cell.bg.frame(0, 0, w, 0)
            end
        },
        Pager = {
            Size = function(section, row)
                return w, 100
            end,
            Init = function(cell, section, row)
                local function createCircleIndicator()
                    local indicator = PagerIndicator()
                    indicator.frame(0, 50, w / 2, 50)
                    return indicator
                end

                local pagerView = PagerView({
                    PageCount = 12,
                    Pages = {
                        Title = function(pos)
                            return "Title"
                        end,
                        Init = function(page, pos)
                            page.icon = Label()
                        end,
                        Layout = function(page, pos)
                            page.icon.text("测试按钮"..pos)
                            page.icon.backgroundColor(pos % 2 == 0 and 0xaaff00 or 0x000fff)
                            page.icon.frame(0, 0, w, 100)
                        end
                    },
                    Callback = {
                        Scrolling=function(pos, percent, distance)
                            Toast("滑动"..pos)
                        end,
                        Selected=function(pos)
                            Toast("选中"..pos)
                        end
                    }
                })

                --活动指示器
                pagerView.indicator(createCircleIndicator())
                pagerView.frame(0, 0, w, 100)

                cell.container = pagerView
            end,
            Layout = function(cell, section, row)
            end
        },
        Image = {
            Size = function(section, row)
                return cellHeight
            end,
            Init = function(cell, section, row)
                cell.icon = Image()
            end,
            Layout = function(cell, section, row)
                local cellWdith, cellHeight = cell.window.size()
                cell.icon.frame(0, 0, cellHeight, cellHeight)
                cell.icon.image(imageUrl1, function()
                    print("imageLoaded-ImageAndImage-1", section, row)
                end)
                cell.icon.callback(function()
                    print("cell_icon1")
                end)
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
                cell.icon.image(imageUrl1, function()
                    print("imageLoaded-ImageAndImage-1", section, row)
                end)
                cell.icon.callback(function()
                    print("cell_icon1")
                end)


                cell.icon2.frame(cellHeight + 10, 0, cellHeight, cellHeight)
                cell.icon2.image(imageUrl1, function()
                    print("imageLoaded-ImageAndImage-2", section, row)
                end)
                cell.icon2.callback(function()
                    print("cell_icon2")
                end)
            end
        }
    }
}
tableView.miniSpacing(0)
tableView.frame(0, 0, w, h)





--local scrollView = HScrollView()
--for i = 0, 10, 1 do
--    local btn = Label()
--    btn.text("BTN" .. i)
--    btn.frame(i * 80, 0, 79, 100)
--    btn.backgroundColor(0xff00ff00)
--    btn.callback(function()
--        Toast(btn.text())
--    end)
--    scrollView.addView(btn)
--end
--scrollView.size(w, 50)
--scrollView.backgroundColor(0xffcccccc)



