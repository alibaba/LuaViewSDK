-- Created by LuaView.
-- Copyright (c) 2017, Alibaba Group. All rights reserved.
--
-- This source code is licensed under the MIT.
-- For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.

--1. 普通设置url，size，center
view = Image()
view.frame(0, 0, 200, 200)
view.backgroundColor(0xffaa0000)


--view.image("animate1")

--2. 普通构造传url
bmp = Bitmap("https://gju1.alicdn.com/bao/uploaded/i4/100000120700895002/TB2Qu0_fXXXXXb9XpXXXXXXXXXX_!!0-0-juitemmedia.jpg_560x560Q90.jpg", function(ok)
    if(ok) then
        view.image(bmp)
    else
        view.image("animate1")
    end
end)


spriteBmp = function(t)
    local w, h = t.size()
    t.sprite(0, 0, w / 2, h / 2, function(newBmp)
        view.image(newBmp)
        bmp = newBmp
    end)
    Toast("width = " .. w .. " height = " .. h)
end


view.callback(function()
    spriteBmp(bmp)
end)


view2 = Image()
view2.frame(0, 200, 200, 200)
bmp2 = Bitmap("animate1", function(ok)
    view2.image(ok and bmp2)
end)

view2.callback(function()
    local n = 5;
    local w, h = bmp2.size()
    local sw = w / 5;
    local vw = view2.width() / 5
    local x = 0
    local vx = 0;
    for i = 1, n do
        x = (i - 1) * sw
        vx = (i - 1) * vw + (i - 1) * 2
        local v = Image()
        v.frame(vx, 403, vw, h)
        v.image(bmp2.sprite(x, 0, sw, h))
    end
end)





