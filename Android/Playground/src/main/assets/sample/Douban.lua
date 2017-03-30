--
-- Copyright 2017 Alibaba Group
-- License: MIT
-- Website: https://alibaba.github.io/LuaViewSDK
-- User: tuoli
-- Date: 17/3/30
--

require("kit.pica")

local function start()
    doubanObjs = Pica:getInstance():render("sample/douban.xml")

    local loading = doubanObjs["loading"]
    local tableView = doubanObjs["tableView"]

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
                            return Sys.contW, Sys.contH/3
                        end,
                        Init = function(cell, section, row)
                            cell.objs = Pica:getInstance():render("sample/douban_cell.xml")
                        end,
                        Layout = function(cell, section, row)
                            cell.objs["profile"]:image(jsonData["subjects"][row]["images"]["large"])
                            cell.objs["movieName"]:text(jsonData["subjects"][row]["title"])
                            cell.objs["score"]:text("评分: " .. jsonData["subjects"][row]["rating"]["average"])

                            local director = "导演: " .. jsonData["subjects"][row]["directors"][1]["name"]
                            local actors = "主演: "
                            for _k, _v in pairs(jsonData["subjects"][row]["casts"]) do
                                actors = actors .. _v["name"] .. "/"
                            end
                            cell.objs["character"]:text(director .. "\n" .. actors)

                            cell.objs["number"]:text(jsonData["subjects"][row]["collect_count"] .. "人看过")
                        end
                    }
                },
                Callback = {
                    PullDown = function()
                        local httpReload = Http()
                        httpReload:get("http://api.douban.com/v2/movie/in_theaters?apikey=0df993c66c0c636e29ecbb5344252a4a",
                            function(response)
                                if (tostring(response:code()) == "200") then
                                    jsonData = Json:toTable(tostring(response:data()))
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

