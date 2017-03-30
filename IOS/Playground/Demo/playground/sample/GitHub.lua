--
-- Created by IntelliJ IDEA.
-- User: tuoli
-- Date: 17/3/17
-- Time: 14:27
-- To change this template use File | Settings | File Templates.
--

require("kit.pica")

local function start()
    if (Sys.android) then
        githubObjs = Pica:getInstance():render("sample/github_android.xml")
    else
        githubObjs = Pica:getInstance():render("sample/github_ios.xml")
    end

    local go = githubObjs["go"]
    local input = githubObjs["input"]
    local tableView = githubObjs["tableView"]
    local loading = githubObjs["loading"]

    local isSearching = false
    go:callback(function()
        if (not Sys.android) then
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
                                    return Sys.contW, Sys.contH/3
                                end,
                                Init = function(cell, section, row)
                                    cell.objs = Pica:getInstance():render("sample/github_cell.xml")
                                end,
                                Layout = function(cell, section, row)
                                    cell.objs["name"]:text(jsonData["items"][row]["full_name"])
                                    if (jsonData["items"][row]["description"]) then
                                        cell.objs["description"]:text(jsonData["items"][row]["description"])
                                    else
                                        cell.objs["description"]:text("no descriptions")
                                    end
                                    cell.objs["profile"]:image(jsonData["items"][row]["owner"]["avatar_url"])
                                    cell.objs["profile"]:scaleType(ScaleType.FIT_CENTER)
                                    cell.objs["stars"]:text("Stars: " .. jsonData["items"][row]["stargazers_count"])
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

Navigation:title("GitHub.lua")
start()
