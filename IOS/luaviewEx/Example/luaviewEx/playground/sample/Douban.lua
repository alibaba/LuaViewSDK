--
-- Copyright 2017 Alibaba Group
-- License: MIT
-- Website: https://alibaba.github.io/LuaViewSDK
-- User: tuoli
-- Date: 17/3/30
--

Navigation:title("Douban.lua")

local meta = object:new()

function meta:onCreate(args)
    self.views = pica:getInstance():render("sample/douban.xml")
    self.loading = self.views["loading"]
    self.list = self.views["tableView"]

    self:handle()
end

function meta:handle()
    self.loading:show()
    print("tuoli", "http request start")
    Http():get("http://api.douban.com/v2/movie/in_theaters?apikey=0df993c66c0c636e29ecbb5344252a4a", function(response)
        print("tuoli", "http request end")
        self.loading:hide()
        if (tostring(response:code()) == "200") then
            local _jsonData = Json:toTable(tostring(response:data()))
            self.list:initParams({
                Section = {
                    SectionCount = function()
                        return 1
                    end,
                    RowCount = function(section)
                        return _jsonData["count"]
                    end
                },
                Cell = {
                    Id = function(section, row)
                        return "ItemCell"
                    end,
                    ItemCell = {
                        Size = function(section, row)
                            return sys.contW, sys.contH/3
                        end,
                        Init = function(cell, section, row)
                            cell.objs = pica:getInstance():render("sample/douban_cell.xml")
                        end,
                        Layout = function(cell, section, row)
                            cell.objs["profile"]:image(_jsonData["subjects"][row]["images"]["large"])
                            cell.objs["movieName"]:text(_jsonData["subjects"][row]["title"])
                            cell.objs["score"]:text("评分: " .. _jsonData["subjects"][row]["rating"]["average"])
                            local director = "导演: " .. _jsonData["subjects"][row]["directors"][1]["name"]
                            local actors = "主演: "
                            for _k, _v in pairs(_jsonData["subjects"][row]["casts"]) do
                                actors = actors .. _v["name"] .. "/"
                            end
                            cell.objs["character"]:text(director .. "\n" .. actors)
                            cell.objs["number"]:text(_jsonData["subjects"][row]["collect_count"] .. "人看过")

                            cell.objs["item"]:callback(function()
                                Bridge:require({page="sample.Douban_detail", url=_jsonData["subjects"][row]["alt"] .. "mobile"})
                            end)
                        end
                    }
                },
                Callback = {
                    PullDown = function()
                        Http():get("http://api.douban.com/v2/movie/in_theaters?apikey=0df993c66c0c636e29ecbb5344252a4a",
                            function(response)
                                if (tostring(response:code()) == "200") then
                                    _jsonData = Json:toTable(tostring(response:data()))
                                    self.list:reload()
                                end

                                self.list:stopRefreshing()
                            end
                        )
                    end
                }
            })

            self.list:reload()
        else
            Toast("Request Error")
        end
    end)
end

return meta

