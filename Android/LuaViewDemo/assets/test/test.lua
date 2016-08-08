local cv = CollectionView({
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
            end,
            Layout = function(cell, section, row) -- cell复用时调用
            print("Layout", section, row, 10 + row * 2)
            end,
            Callback = function(cell, section, row) -- 用户点击了section和row
            Toast("Section " .. section .. ", Row " .. row);
            end
        }
    }
})



cv.frame(0, 0, 200, 200)