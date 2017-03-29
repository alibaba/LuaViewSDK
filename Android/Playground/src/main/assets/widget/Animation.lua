--
-- Created by IntelliJ IDEA.
-- User: tuoli
-- Date: 17/2/28
-- Time: 16:53
-- To change this template use File | Settings | File Templates.
--

require("kit.pickup")

local function start()
    aniObjs = Pickup:getInstance():render("widget/animation.xml")

    local img = aniObjs["img"]

    local translateBtn = aniObjs["translateBtn"]
    translateBtn:callback(function()
        local translate = Animation():translation(200, 50):duration(1)
        translate:with(img):start()
    end)

    local scaleBtn = aniObjs["scaleBtn"]
    scaleBtn:callback(function()
        local scale = Animation():scale(0.5, 0.5):duration(1)
        scale:with(img):start()
    end)

    local alphaBtn = aniObjs["alphaBtn"]
    alphaBtn:callback(function()
        local alpha = Animation():alpha(0.3):duration(1)
        alpha:with(img):start()
    end)

    local resetBtn = aniObjs["resetBtn"]
    resetBtn:callback(function()
        img:translation(0, 0)
        img:scale(1, 1)
        img:alpha(1)
    end)

    local allBtn = aniObjs["allBtn"]
    allBtn:callback(function()
        local translate = Animation():translation(200, 50):duration(1)
        local scale = Animation():scale(0.5, 0.5):duration(1)
        local alpha = Animation():alpha(0.3):duration(1)
        alpha:with(img):start()
        scale:with(img):start()
        translate:with(img):start()
    end)

end

Navigation:title("Animation.lua")
start()

