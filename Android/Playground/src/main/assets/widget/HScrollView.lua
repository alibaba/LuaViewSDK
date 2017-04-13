--
-- Copyright 2017 Alibaba Group
-- License: MIT
-- Website: https://alibaba.github.io/LuaViewSDK
-- User: tuoli
-- Date: 17/3/30
--

Navigation:title("HScrollView.lua")

local meta = object:new()

function meta:onCreate(args)
    self.views = pica:getInstance():render("widget/hscrollview.xml")
    self.scroller = self.views["scroller"]

    self:handle()
end

function meta:handle()
    for k, v in pairs(self.views) do
        if k ~= "scroller" then
            v:onClick(function()
                v:backgroundColor(0xEB3131)
                for _k, _v in pairs(self.views) do
                    if _k ~= "scroller" then
                        if (_v ~= v) then
                            _v:backgroundColor(0x004B97)
                        end
                    end
                end
                local x, y, w, h = v:frame()
                local dx = (sys.contW - w)/2
                self.scroller:offset(x-dx, 0, true)
            end)
        end
    end
end

return meta