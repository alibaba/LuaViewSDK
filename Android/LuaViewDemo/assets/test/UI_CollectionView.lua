-- Created by LuaView.
-- Copyright (c) 2017, Alibaba Group. All rights reserved.
--
-- This source code is licensed under the MIT.
-- For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.

--工具包引入
require "kit/lv_kit"
-- 变量定义
local s_width, s_height = System.screenSize()
local tableView

local tableViewData = {
    Section = {
        SectionCount = 2, -- section数量
        RowCount = function(section)
            if(section == 1) then
                return 50
            else
                return 50
            end
        end
    },
    Cell = {
        Id = function(section, row) -- 指定section和row对应的Cell类型唯一识别号
        local id = "Cell1" .. section .. row
        print("Id", id)
        return id;
        end,

        Cell1 = {
            Size = function(section, row)
                print("Size", section, row, 10 + row * 2)
                return 10 + row * 2
            end,
            Init = function(cell, section, row) -- 初始化cell
            print("Init", section, row, 10 + row * 2)
            cell.title = Label();
            cell.title.backgroundColor(section == 1 and 0xff0000 or 0x00ff00)
            end,
            Layout = function(cell, section, row) -- cell复用时调用
            print("Layout", section, row, 10 + row * 2)
--            cell.title.frame(0, 0, s_width, 10 + row * 2)
            cell.title.text(section .. '--' .. row)
            end,
            Callback = function(cell, section, row) -- 用户点击了section和row
            Toast("Section " .. section .. ", Row " .. row);
            end
        }
    }
}

local cellData = function(_, key)
    print(key)
    return {
        Size = function(section, row)
            print("Size", section, row, 10 + row * 2)
--            return 10 + row * 2
            return s_width, 200
        end,
        Init = function(cell, section, row) -- 初始化cell
        print("Init", section, row, 10 + row * 2)
        cell.title = Label();
        cell.title.backgroundColor(section == 1 and 0xff0000 or 0x00ff00)
            cell.title.lines(10)
            cell.title.maxLine(10)
        end,
        Layout = function(cell, section, row) -- cell复用时调用
        print("Layout", section, row, 10 + row * 2)
--        cell.title.frame(0, 0, s_width, 10 + row * 2)

        cell.title.text("sfadfssfadfssfadfssfadfssfadfssfadfssfadfssfadfssfadfssfadfssfadfssfadfssfadfssfadfssfadfssfadfssfadfssfadfssfadfssfadfssfadfssfadfssfadfssfadfssfadfssfadfssfadfssfadfssfadfssfadfssfadfssfadfssfadfssfadfssfadfssfadfs")

            print("cell-size 11", cell.title.height())

        cell.title.adjustSize()

            print("cell-size 22", cell.title.height())

        end,
        Callback = function(cell, section, row) -- 用户点击了section和row
        Toast("Section " .. section .. ", Row " .. row);
        end
    }
end

setmetatable(tableViewData.Cell, { __index = cellData })
tableView = CollectionView(tableViewData)
tableView.miniSpacing(1)
tableView.frame(0, 0, s_width, s_height)
tableView.showScrollIndicator(false)
tableView.enabled(false)





