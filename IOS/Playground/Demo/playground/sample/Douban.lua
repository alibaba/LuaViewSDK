--
-- Created by IntelliJ IDEA.
-- User: tuoli
-- Date: 17/3/17
-- Time: 14:29
-- To change this template use File | Settings | File Templates.
--

require("kit.common")
require("kit.platform")

local _pica = require("kit.pica")

local function start()
    _pica:parseXml("sample/douban.xml")

    local loading = _pica:getViewByName("loading")
    tableView = _pica:getViewByName("tableView")

    loading:show()

    local http = Http()
    print("tuoli", "http request start")
    http:get("http://api.douban.com/v2/movie/in_theaters?apikey=0df993c66c0c636e29ecbb5344252a4a", function(response)
        print("tuoli", "http request end")

        loading:hide()

        if (tostring(response:code()) == "200") then
            local jsonData = Json:toTable(tostring(response:data()))
            local tableData = {
                Section = {
                    SectionCount = function()
                        return 1
                    end,
                    RowCount = function(section)
                        return jsonData["count"]
                    end
                },
                Cell = {
                    Id = function(section, row)
                        return "ItemCell"
                    end,
                    ItemCell = {
                        Size = function(section, row)
                            return Platform.contentWidth, Platform.contentHeight/3
                        end,
                        Init = function(cell, section, row)
                            _pica:parseXml("sample/douban_cell.xml")

                            cell.left = _pica:getViewByName("left.pannel")
                            cell.right = _pica:getViewByName("right.pannel")
                            cell.root = _pica:getViewByName("root")
                            cell.window:addView(root)
                            cell.profile = _pica:getViewByName("profile")
                            cell.movieName = _pica:getViewByName("movieName")
                            cell.score = _pica:getViewByName("score")
                            cell.character = _pica:getViewByName("character")
                            cell.number = _pica:getViewByName("number")
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
                },
                Callback = {
                    PullDown = function()
                        local httpReload = Http()
                        httpReload:get("http://api.douban.com/v2/movie/in_theaters?apikey=0df993c66c0c636e29ecbb5344252a4a",
                            function(response)
                                jsonData = Json:toTable(tostring(response:data()))
                                if (tostring(response:code()) == "200") then
                                    tableView:reload()
                                end

                                tableView:stopRefreshing()
                            end
                        )
                    end
                }
            }

            tableView:initParams(tableData)
            tableView:reload()
        else
            Toast("Request Error")
        end
    end)
end

Navigation:title("Douban.lua")
start()

