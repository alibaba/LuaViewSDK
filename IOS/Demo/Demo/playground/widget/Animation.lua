--
-- Created by IntelliJ IDEA.
-- User: tuoli
-- Date: 17/2/28
-- Time: 16:53
-- To change this template use File | Settings | File Templates.
--

Navigation:title("Animation.lua")

local function start()
    local pica = require("kit.pica")
    local xml = File:read("widget/animation.xml")
    pica:parseXml(xml)

    local img = pica:getViewByName("img")

    local button1 = pica:getViewByName("button1")
    button1:callback(function()
        local translate = Animation():translation(200, 50):duration(1)
        translate:with(img):start()
    end)

    local button2 = pica:getViewByName("button2")
    button2:callback(function()
        local scale = Animation():scale(0.5, 0.5):duration(1)
        scale:with(img):start()
    end)

    local button3 = pica:getViewByName("button3")
    button3:callback(function()
        local alpha = Animation():alpha(1, 0.3):duration(1)
        alpha:with(img):start()
    end)
end

start()

