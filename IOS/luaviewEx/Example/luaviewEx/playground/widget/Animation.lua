--
-- Copyright 2017 Alibaba Group
-- License: MIT
-- Website: https://alibaba.github.io/LuaViewSDK
-- User: tuoli
-- Date: 17/3/30
--

Navigation:title("Animation.lua")

local meta = object:new()

function meta:onCreate(args)
    self.views = pica:getInstance():render("widget/animation.xml")
    self.spirit = self.views["img"]
    self.translateBtn = self.views["translateBtn"]
    self.scaleBtn = self.views["scaleBtn"]
    self.alphaBtn = self.views["alphaBtn"]
    self.resetBtn = self.views["resetBtn"]
    self.allBtn = self.views["allBtn"]

    self:handle()
end

function meta:handle()
    self.translateBtn:callback(function()
        local translate = Animation():translation(sys.contW/2, sys.contW/2):duration(1)
        translate:with(self.spirit):start()
    end)
    self.scaleBtn:callback(function()
        local scale = Animation():scale(0.5, 0.5):duration(1)
        scale:with(self.spirit):start()
    end)
    self.alphaBtn:callback(function()
        local alpha = Animation():alpha(0.3):duration(1)
        alpha:with(self.spirit):start()
    end)
    self.allBtn:callback(function()
        local translate = Animation():translation(sys.contW/2, sys.contW/2):duration(1)
        local scale = Animation():scale(0.5, 0.5):duration(1)
        local alpha = Animation():alpha(0.3):duration(1)
        alpha:with(self.spirit):start()
        scale:with(self.spirit):start()
        translate:with(self.spirit):start()
    end)
    self.resetBtn:callback(function()
        self.spirit:translation(0, 0)
        self.spirit:scale(1, 1)
        self.spirit:alpha(1)
    end)
end

return meta
