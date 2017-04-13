--
-- Copyright 2017 Alibaba Group
-- License: MIT
-- Website: https://alibaba.github.io/LuaViewSDK
-- User: tuoli
-- Date: 17/4/11
--

Navigation:title("ListPinned.lua")

local meta = object:new()

function meta:onCreate(args)
    self.list = CollectionView()
    self.list:frame(0, 0, sys.contW, sys.contH)
    self.list:initParams({
        Section = {
            SectionCount = function()
                return (1 + 1 + 12)
            end,
            RowCount = function(section)
                if (section == 1 or section == 2) then
                    return 1
                else
                    return 10
                end
            end
        },
        Cell = {
            Id = function(section, row)
                if (section == 1) then
                    return "HeaderCell"
                elseif (section == 2) then
                    return "PinnedCell", Pinned.YES
                else
                    return "CommonCell"
                end
            end,
            HeaderCell = {
                Size = function(section, row)
                    return sys.contW, sys.contH*0.3
                end,
                Init = function(cell, section, row)
                    cell.objs = pica:getInstance():render("widget/list_pinned_head.xml")
                end
            },
            PinnedCell = {
                Size = function(section, row)
                    return sys.contW, sys.contH/10
                end,
                Init = function(cell, section, row)
                    cell.objs = pica:getInstance():render("widget/hscrollview.xml")
                    self.pinnedViews = cell.objs
                    for k, v in pairs(cell.objs) do
                        if k ~= "scroller" then
                            v:onClick(function()
                                self.gotoTop:show()
                                v:backgroundColor(0xEB3131)
                                for _k, _v in pairs(cell.objs) do
                                    if _k ~= "scroller" then
                                        if (_v ~= v) then
                                            _v:backgroundColor(0x004B97)
                                        end
                                    end
                                end
                                local x, y, w, h = v:frame()
                                local dx = (sys.contW - w)/2
                                cell.objs["scroller"]:offset(x-dx, 0, true)
                                local index = math.floor(x/(sys.contW/4))
                                -- todo: SDK需要统一这个API的第三个参数offset
                                if (sys.android) then
                                    self.list:scrollToCell(index+3, 1, sys.contH/10, false)
                                else
                                    self.list:scrollToCell(index+3, 1, -sys.contH/10, false)
                                end
                            end)
                        end
                    end
                end
            },
            CommonCell = {
                Size = function(section, row)
                    return sys.contW, sys.contH/3
                end,
                Init = function(cell, section, row)
                    cell.objs = pica:getInstance():render("widget/button.xml")
                end,
                Layout = function(cell, section, row)
                    cell.objs["button1"]:title(tostring((section-2)*10+row-1))
                end
            }
        },
        Callback = {
            Scrolling = function( firstVisibleSection, firstVisibleRow, visibleCellCount )
                local dx, dy = self.list:offset()
                if (dy <= 0) then
                    if (self.pinnedViews) then
                        self.pinnedViews["scroller"]:offset(0, 0, true)
                        for _k, _v in pairs(self.pinnedViews) do
                            if _k ~= "scroller" then
                                if (_k ~= "item1") then
                                    _v:backgroundColor(0x004B97)
                                else
                                    _v:backgroundColor(0xEB3131)
                                end
                            end
                        end
                    end
                end

                local diff = dy-sys.contH*0.3
                if (diff >= 0) then
                    self.gotoTop:show()
                    local index = math.floor(diff/(10*sys.contH/3)+0.01) + 1
                    if (self.pinnedViews["item"..index]) then
                        local x, y, w, h = self.pinnedViews["item"..index]:frame()
                        local dx = (sys.contW - w)/2
                        self.pinnedViews["scroller"]:offset(x-dx, 0, true)
                        for _k, _v in pairs(self.pinnedViews) do
                            if _k ~= "scroller" then
                                if (_k ~= "item"..index) then
                                    _v:backgroundColor(0x004B97)
                                else
                                    _v:backgroundColor(0xEB3131)
                                end
                            end
                        end
                    end
                else
                    self.gotoTop:hide()
                end
            end,
            ScrollEnd = function(firstVisibleSection, firstVisibleRow, visibleCellCount )
            end,
        }
    })

    self.gotoTop = Image()
    self.gotoTop:frame(sys.contW*0.85, sys.contH*0.85, sys.contH*0.07, sys.contH*0.07)
    self.gotoTop:image("backtop.png")
    self.gotoTop:hide()
    self.gotoTop:onClick(function()
        self.gotoTop:hide()
        self.list:scrollToCell(1, 1, 0, false)
        if (self.pinnedViews) then
            self.pinnedViews["scroller"]:offset(0, 0, true)
            for _k, _v in pairs(self.pinnedViews) do
                if _k ~= "scroller" then
                    if (_k ~= "item1") then
                        _v:backgroundColor(0x004B97)
                    else
                        _v:backgroundColor(0xEB3131)
                    end
                end
            end
        end
    end)
end

return meta

