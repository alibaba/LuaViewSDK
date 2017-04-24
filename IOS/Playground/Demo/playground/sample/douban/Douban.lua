--
-- Copyright 2017 Alibaba Group
-- License: MIT
-- Website: https://alibaba.github.io/LuaViewSDK
-- User: tuoli
-- Date: 17/3/30
--

Navigation:title("Douban.lua")

local meta = object:new()

local _httpPrototype = require("sample.douban.http")

function meta:onCreate(args)
    self.views = pica:getInstance():render("sample/douban/douban.xml")
    self.loading = self.views["loading"]
    self.list = self.views["tableView"]
    self.left = self.views["left"]
    self.right = self.views["right"]
    self.line = self.views["line"]

    self.cacheData = {}
    self.theatersDta = {}
    self.comingDta = {}

    self.isLeft = true

    self.theatersUrl = "http://api.douban.com/v2/movie/in_theaters"
    self.comingUrl = "http://api.douban.com/v2/movie/coming_soon"

    self:handle()
end

function meta:handle()
    self.loading:show()

    self.left:onClick(function()
        self:leftClick()
    end)

    self.right:onClick(function()
        self:rightClick()
    end)

    self.list:initParams({
        Section = {
            SectionCount = function()
                return 1
            end,
            RowCount = function(section)
                if (self.cacheData and self.cacheData["count"] ~= nil) then
                    return self.cacheData["count"]
                else
                    return 0
                end
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
                    cell.objs = pica:getInstance():render("sample/douban/douban_cell.xml")
                end,
                Layout = function(cell, section, row)
                    cell.objs["profile"]:image(self.cacheData["subjects"][row]["images"]["large"])
                    cell.objs["movieName"]:text(self.cacheData["subjects"][row]["title"])
                    cell.objs["score"]:text("评分: " .. self.cacheData["subjects"][row]["rating"]["average"])
                    local director = "导演: " .. self.cacheData["subjects"][row]["directors"][1]["name"]
                    local actors = "主演: "
                    for _k, _v in pairs(self.cacheData["subjects"][row]["casts"]) do
                        actors = actors .. _v["name"] .. "/"
                    end
                    cell.objs["character"]:text(director .. "\n" .. actors)
                    cell.objs["number"]:text(self.cacheData["subjects"][row]["collect_count"] .. "人看过")

                    cell.objs["item"]:onClick(function()
                        Bridge:require({page="sample.douban.Douban_detail", detail_id=self.cacheData["subjects"][row]["id"]})
                    end)
                end
            }
        },
        Callback = {
            PullDown = function()
                if (self.isLeft) then
                    self.http = self:request(self.theatersUrl,
                        function(data)
                            self.theatersDta = data
                            self.cacheData = self.theatersDta
                            self.list:reload()
                            self.list:stopRefreshing()
                        end
                    )
                else
                    self.http = self:request(self.comingUrl,
                        function(data)
                            self.comingData = data
                            self.cacheData = self.comingData
                            self.list:reload()
                            self.list:stopRefreshing()
                        end
                    )
                end
            end
        }
    })

    self.http = self:request(self.theatersUrl,
        function(data)
            self.theatersDta = data
            self.cacheData = self.theatersDta
            self.list:reload()
        end
    )
end

function meta:leftClick()
    self.isLeft = true
    self.left:textColor(0xEB3131)
    self.right:textColor(0x0000000)
    local translate = Animation():translation(0, 0):duration(0.1)
    translate:with(self.line):start()

    self.list:stopRefreshing()
    self.http:cancel()
    self.loading:hide()

    if (self.theatersDta and self.theatersDta["count"] ~= nil and self.theatersDta["count"] > 0) then
        self.cacheData = self.theatersDta
        self.list:reload()
    else
        -- 先清空缓存的数据,再请求新的数据
        self.cacheData = {}
        self.list:reload()

        self.loading:show()
        self.http = self:request(self.theatersUrl,
            function(data)
                self.theatersDta = data
                self.cacheData = self.theatersDta
                self.list:reload()
            end
        )
    end
end

function meta:rightClick()
    self.isLeft = false
    self.left:textColor(0x0000000)
    self.right:textColor(0xEB3131)
    local translate = Animation():translation(sys.contW/2, 0):duration(0.1)
    translate:with(self.line):start()

    self.list:stopRefreshing()
    self.http:cancel()
    self.loading:hide()

    if (self.comingDta and self.comingDta["count"] ~= nil and self.comingDta["count"] > 0) then
        self.cacheData = self.comingDta
        self.list:reload()
    else
        -- 先清空缓存的数据,再请求新的数据
        self.cacheData = {}
        self.list:reload()

        self.loading:show()
        self.http = self:request(self.comingUrl,
            function(data)
                self.comingDta = data
                self.cacheData = self.comingDta
                self.list:reload()
            end
        )
    end
end

function meta:request(url, callback)
    local http = _httpPrototype:new()
    http:request(url, function(response)
        self.loading:hide()
        if (tostring(response:code()) == "200") then
            if (callback ~= nil) then
                callback(Json:toTable(tostring(response:data())))
            end
        else
            Toast("Request Error")
        end
    end)

    return http
end

return meta

