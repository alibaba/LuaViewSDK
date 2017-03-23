--
-- Created by IntelliJ IDEA.
-- User: tuoli
-- Date: 17/3/17
-- Time: 14:27
-- To change this template use File | Settings | File Templates.
--

require("kit.common")
require("kit.platform")

local _pica = require("kit.pica")

local function start()
    if (Platform.isAndroid) then
        _pica:parseXml("sample/github_android.xml")
    else
        _pica:parseXml("sample/github_ios.xml")
    end

    root = _pica:getViewByName("root")
    topContainer = _pica:getViewByName("topContainer")
    inputContainer = _pica:getViewByName("inputContainer")
    go = _pica:getViewByName("go")
    input = _pica:getViewByName("input")
    tableView = _pica:getViewByName("tableView")

    loading = _pica:getViewByName("loading")

    local isSearching = false
    go:callback(function()
        if (not Platform.isAndroid) then
            input:cancelFocus()
        end

        if (isSearching == true) then
            return
        end

        local content = input:text()
        local baseUrl = "https://api.github.com/search/repositories?sort=stars&q="

        loading:show()
        isSearching = true

        print("tuoli", "http request start")
        local http = Http()
        http:get(baseUrl .. content, function(response)
            isSearching = false
            print("tuoli", "http request end")

            loading:hide()

            if (tostring(response:code()) == "200") then
                local jsonData = Json:toTable(tostring(response:data()))

                if (table.getn(jsonData["items"]) == 0) then
                    Toast("No Result")
                else
                    local tableData = {
                        Section = {
                            SectionCount = function()
                                return 1
                            end,
                            RowCount = function(section)
                                return table.getn(jsonData["items"])
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
                                    _pica:parseXml("sample/github_cell.xml")

                                    cell.root = _pica:getViewByName("root")
                                    cell.window:addView(cell.root)

                                    cell.bottomContainer = _pica:getViewByName("bottomContainer")
                                    cell.bottomLeft = _pica:getViewByName("bottomLeft")
                                    cell.author = _pica:getViewByName("author")

                                    cell.name = _pica:getViewByName("name")
                                    cell.description = _pica:getViewByName("description")
                                    cell.profile = _pica:getViewByName("profile")
                                    cell.stars = _pica:getViewByName("stars")
                                end,
                                Layout = function(cell, section, row)
                                    cell.name:text(jsonData["items"][row]["full_name"])
                                    if (jsonData["items"][row]["description"]) then
                                        cell.description:text(jsonData["items"][row]["description"])
                                    else
                                        cell.description:text("no descriptions")
                                    end
                                    cell.profile:image(jsonData["items"][row]["owner"]["avatar_url"])
                                    cell.profile:scaleType(ScaleType.FIT_CENTER)
                                    cell.stars:text("Stars: " .. jsonData["items"][row]["stargazers_count"])
                                end
                            }
                        }
                    }

                    tableView:initParams(tableData)
                    tableView:reload()
                end
            else
                Toast("Request Error")
            end
        end)
    end)
end

Navigation:title("Github.lua")
start()