--
-- Created by IntelliJ IDEA.
-- User: tuoli
-- Date: 17/2/28
-- Time: 16:53
-- To change this template use File | Settings | File Templates.
--

require("kit.pica_new")

local function start()
    aniObjs = Pica:getInstance():render("widget/animation.xml")

    local img = aniObjs["img"]

    local button1 = aniObjs["button1"]
    button1:callback(function()
        local translate = Animation():translation(200, 50):duration(1)
        translate:with(img):start()
    end)

    local button2 = aniObjs["button2"]
    button2:callback(function()
        local scale = Animation():scale(0.5, 0.5):duration(1)
        scale:with(img):start()
    end)

    local button3 = aniObjs["button3"]
    button3:callback(function()
        local alpha = Animation():alpha(0.3):duration(1)
        alpha:with(img):start()
    end)
end

Navigation:title("Animation.lua")
start()

