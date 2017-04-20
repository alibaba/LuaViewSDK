--
-- Copyright 2017 Alibaba Group
-- License: MIT
-- Website: https://alibaba.github.io/LuaViewSDK
-- User: tuoli
-- Date: 17/4/5
--

Navigation:title("豆瓣电影详情")

local meta = object:new()

function meta:onCreate(args)
    self.args = args
    self.list = CollectionView()
    self.list:frame(0, 0, sys.contW, sys.contH)
    self.list:initParams({
        Section = {
            SectionCount = function()
                return 1
            end,
            RowCount = function()
                return 1
            end
        },
        Cell = {
            Id = function()
                return "DetailCell"
            end,
            DetailCell = {
                Size = function()
                    return sys.contW, sys.contH*1.5
                end,
                Init = function(cell)
                    cell.objs = pica:getInstance():render("sample/douban/douban_detail.xml")
                    Http():get("http://api.douban.com/v2/movie/subject/" .. self.args.detail_id, function(response)
                        if (tostring(response:code()) == "200") then
                            local _jsonData = Json:toTable(tostring(response:data()))
                            cell.objs["profile"]:image(_jsonData["images"]["large"])
                            cell.objs["title"]:text(_jsonData["title"])
                            local tagInfo = _jsonData["year"]
                            for _, _v in pairs(_jsonData["countries"]) do
                                tagInfo = tagInfo .. " / " .. _v
                            end
                            for _, _v in pairs(_jsonData["genres"]) do
                                tagInfo = tagInfo .. " / " .. _v
                            end
                            cell.objs["tag"]:text(tagInfo)

                            cell.objs["intro"]:text(_jsonData["summary"])
                            if (sys.android) then
                                cell.objs["intro"]:nativeView():setLineSpacing(10*sys.scale,1)
                            end

                            local directorNum = table.getn(_jsonData["directors"])
                            local actorNum = table.getn(_jsonData["casts"])
                            local characterNum = directorNum + actorNum
                            for i = 1, characterNum, 1 do
                                local x,y,w,h = cell.objs["scroller"]:frame()
                                if (not sys.android) then
                                    cell.objs["scroller"]:contentSize((w/3.5)*characterNum, h)
                                end

                                local container = View()
                                container:frame((i-1) * (w/3.5), 0, (w/3.5)-5, h)
                                local img = Image()
                                img:frame(0, 0, container:width(), container:height()*0.8)
                                local name = Label()
                                name:frame(0, container:height()*0.8, container:width(), container:height()*0.2)
                                name:fontSize(12*sys.scale)
                                name:textAlign(TextAlign.CENTER)

                                container:addView(img)
                                container:addView(name)
                                cell.objs["scroller"]:addView(container)

                                if (i <= directorNum) then
                                    img:image(_jsonData["directors"][i]["avatars"]["large"])
                                    name:text(_jsonData["directors"][i]["name"])
                                else
                                    img:image(_jsonData["casts"][i-directorNum]["avatars"]["large"])
                                    name:text(_jsonData["casts"][i-directorNum]["name"])
                                end
                            end
                        else
                            Toast("Request Error")
                        end
                    end)
                end
            }
        }
    })
end

return meta

