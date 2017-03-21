--
-- Created by IntelliJ IDEA.
-- User: tuoli
-- Date: 17/3/17
-- Time: 14:27
-- To change this template use File | Settings | File Templates.
--

Navigation:title("Github.lua")

local _screenWidth, _screenHeight = System:screenSize()

-- 减掉ActionBar和StatusBar的高度
if (System:android()) then
    local device = System:device()
    _screenHeight = device.window_height - device.status_bar_height - device.nav_height
else
    _screenHeight = _screenHeight - 64      -- iOS, 稳定在这个值
end

local function start()
    local pica = require("kit.pica")

    print("tuoli", "xml read start")
    local xml = File:read("sample/github.xml")
    print("tuoli", "xml read end")
    pica:parseXml(xml)

    root = pica:getViewByName("root")
    topContainer = pica:getViewByName("topContainer")
    go = pica:getViewByName("go")
    input = pica:getViewByName("input")
    container = pica:getViewByName("container")

    local loading = LoadingIndicator()
    loading:frame(0, _screenHeight/2 - 50, _screenWidth, 50)
    loading:color(0xF06292)

    local isSearching = false
    go:callback(function()
        if (isSearching == true) then
            return
        end

        local content = input:text()
        local baseUrl = "https://api.github.com/search/repositories?sort=stars&q="

        if (tableView) then
            tableView:removeFromSuper()
        end

        loading:show()

        print("tuoli", "http request start")
        isSearching = true

        local http = Http()
        http:get(baseUrl .. content, function(response)
            isSearching = false
            print("tuoli", "http request end")
            loading:hide()
            local jsonData = Json:toTable(tostring(response:data()))
--                Common:printTable(jsonData)
            if (tostring(response:code()) == "200") then
                if (table.getn(jsonData["items"]) == 0) then
                    Toast("No Result")
                else
                    local tableData = {
                        Section = {
                            SectionCount = function()
                                return 1
                            end,
                            RowCount = function(section)
                                print("tuoli", "hhhhhhh")
                                return table.getn(jsonData["items"])
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
                                    print("tuoli", "xml read start")
                                    local xml = File:read("sample/github_cell.xml")
                                    print("tuoli", "xml read end")
                                    pica:parseXml(xml)

                                    cell.root = pica:getViewByName("root")
                                    cell.window:addView(cell.root)

                                    cell.bottomContainer = pica:getViewByName("bottomContainer")
                                    cell.bottomLeft = pica:getViewByName("bottomLeft")
                                    cell.author = pica:getViewByName("author")

                                    cell.name = pica:getViewByName("name")
                                    cell.description = pica:getViewByName("description")
                                    cell.profile = pica:getViewByName("profile")
                                    cell.stars = pica:getViewByName("stars")
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
                    tableView = CollectionView(tableData)
                    local x, y, w, h = container:frame()
                    tableView:frame(0, 0, w, h)
                    container:addView(tableView)
                end
            else
                Toast("Request Error")
            end
        end)
    end)
end

start()