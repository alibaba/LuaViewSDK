-- Created by LuaView.
-- Copyright (c) 2017, Alibaba Group. All rights reserved.
--
-- This source code is licensed under the MIT.
-- For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.

view = View()
view.frame(0, 0, 100, 100)
view.backgroundColor(0xaaaaaa)
view.callback(function()
    print("xxxx")
end)

local view2 = View()
view2.frame(102, 0, 100, 100)
view2.backgroundColor(0x00ff00)


local image = Image()
image.frame(100, 100, 100, 100)
image.backgroundColor(0xdcdcdc)
-- 网络图有问题，属于GlideImageProvider造成的问题
 image.image("https://gju1.alicdn.com/bao/uploaded/i4/100000120700895002/TB2Qu0_fXXXXXb9XpXXXXXXXXXX_!!0-0-juitemmedia.jpg_560x560Q90.jpg")
--image.image("animate1")
image.effects(ViewEffect.CLICK, 0xff0000, 0.6)
image.callback(function()
    print("yyy")
end)


local button = Button()
button.frame(100, 200, 100, 100)
button.text("Button")
button.backgroundColor(0xdcdcdc)
button.callback(function()
    local effect = view.effects()
    if(effect == ViewEffect.CLICK) then
        view.effects(ViewEffect.NONE)
    else
        view.effects(ViewEffect.CLICK)
    end
    print("Change ViewEffect", view.effects())
end)
button.effects(ViewEffect.CLICK)


imageView2 = Image();
imageView2.frame( 50, 300, 300, 200);
imageView2.image("http://g.alicdn.com/ju/lua/2.0.24/doc/icon.png");

imageView2.effects(ViewEffect.PARALLAX, 150, 140)
imageView2.effects(ViewEffect.CLICK, 0xff0000, 0.6)
imageView2.callback(function()
    print("yyy")
end)