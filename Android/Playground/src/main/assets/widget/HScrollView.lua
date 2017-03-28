--
-- Created by IntelliJ IDEA.
-- User: tuoli
-- Date: 17/3/28
-- Time: 11:38
-- To change this template use File | Settings | File Templates.
--

require("kit.pickup")

local function start()
    objects = Pickup:getInstance():render("widget/hscrollview.xml")

    local scroller = objects["scroller"]

    for k, v in pairs(objects) do
        if k ~= "scroller" then
            v:callback(function()
                v:backgroundColor(0xEB3131)
                for _k, _v in pairs(objects) do
                    if _k ~= "scroller" then
                        if (_v ~= v) then
                            _v:backgroundColor(0x004B97)
                        end
                    end
                end
                local x, y, w, h = v:frame()
                local dx = (Platform.contentWidth - w)/2
                scroller:offset(x-dx, 0, true)
            end)
        end
    end
end

Navigation:title("HScrollView.lua")
start()