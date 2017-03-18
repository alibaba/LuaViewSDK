--
-- Created by IntelliJ IDEA.
-- User: tuoli
-- Date: 17/3/17
-- Time: 14:29
-- To change this template use File | Settings | File Templates.
--

Navigation:title("Douban.lua")

local _screenWidth, _screenHeight = System:screenSize()

-- 减掉ActionBar和StatusBar的高度
if (System:android()) then
    _screenHeight = _screenHeight - 80 -- Android, 不同机型, 高度不定, 比较蛋疼
else
    _screenHeight = _screenHeight - 64      -- iOS, 稳定在这个值
end

local function start()
    local pica = require("kit.pica")

    local http = Http()
    http:get("http://api.douban.com/v2/movie/top250?apikey=0df993c66c0c636e29ecbb5344252a4a", {
        query = 1
    }, function(response)
        print("tuoli to json start")
        local jsonData = Json:toTable(response:data())
        print("tuoli to json end")
        if (tostring(response:code()) == "200") then
            local pica = require("kit.pica")

            local tableData = {
                Section = {
                    SectionCount = function()
                        return 1
                    end,
                    RowCount = function(section)
                        return jsonData["count"] - jsonData["start"]
                    end
                },
                Cell = {
                    Id = function(section, row)
                        return "ItemCell"
                    end,
                    ItemCell = {
                        Size = function(section, row)
                            return _screenWidth, _screenHeight/3
                        end,
                        Init = function(cell, section, row)
                            local xml = File:read("demo/douban.xml")
                            pica:parseXml(xml)

                            local root = pica:getViewByName("root")
                            cell.window:addView(root)
                            cell.window:backgroundColor(0xffffff)
                            cell.profile = pica:getViewByName("profile")
                            cell.movieName = pica:getViewByName("movieName")
                            cell.score = pica:getViewByName("score")
                            cell.character = pica:getViewByName("character")
                            cell.number = pica:getViewByName("number")
                        end,
                        Layout = function(cell, section, row)
                            cell.profile:image(jsonData["subjects"][row]["images"]["large"])
                            cell.movieName:text(jsonData["subjects"][row]["title"])
                            cell.score:text("评分: " .. jsonData["subjects"][row]["rating"]["average"])
                            local director = "导演: " .. jsonData["subjects"][row]["directors"][1]["name"]
                            local actors = "主演: "
                            for _k, _v in pairs(jsonData["subjects"][row]["casts"]) do
                                actors = actors .. _v["name"] .. "/"
                            end
                            cell.character:text(director .. "\n" .. actors)

                            cell.number:text(jsonData["subjects"][row]["collect_count"] .. "人看过")
                        end
                    }
                }
            }
            tableView = CollectionView(tableData)
            tableView:backgroundColor(0xeeeeee)
            tableView:frame(0, 0, _screenWidth, _screenHeight)
            tableView:miniSpacing(1)
        end
    end)
end

start()

