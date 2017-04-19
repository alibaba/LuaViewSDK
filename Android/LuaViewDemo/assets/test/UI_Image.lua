-- Created by LuaView.
-- Copyright (c) 2017, Alibaba Group. All rights reserved.
--
-- This source code is licensed under the MIT.
-- For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.

--1. 普通设置url，size，center
view = Image()
view.size(100, 100)
view.center(50, 50)
print(view.size())
view.backgroundColor(0xffaa0000)
view.callback(function()
    print("Button Clicked")
end)
view.image("animate1")

print("view-url", view.image())


--2. 普通构造传url
view2 = Image("https://gju1.alicdn.com/bao/uploaded/i4/100000120700895002/TB2Qu0_fXXXXXb9XpXXXXXXXXXX_!!0-0-juitemmedia.jpg_560x560Q90.jpg")
view2.frame(150, 0, 150, 100)
print("view2-url", view2.image())



scrW, scrH = System.screenSize();
window.backgroundColor("#FFFF00", 0.5);

w = scrW / 3;
X0 = scrW / 10;
Y0 = scrH / 4;


imageView = Image();
imageView.frame(X0, Y0, w, w);
imageView.backgroundColor(0xff0000, 0.5);
imageView.startAnimationImages({"animate1", "animate2"}, 3.0, 1);

imageView.callback({
    onShow = function()
        print("show")
    end,
    onHide = function()
        print("hide")
    end
})

window.callback({
    onShow = function()
        print("window show")
    end,
    onHide = function()
        print("window hide")
    end
})

label = Label();

imageView2 = Image();
imageView2.frame(X0 + w + X0, Y0, w, w);
imageView2.image("http://g.alicdn.com/ju/lua/2.0.24/doc/icon.png",
    function()
        --这里的text会报一个bug
        print("图片加载成功!");
        label.text("图片加载成功!")
        label.adjustSize();
        label.center(scrW / 2, scrH / 2);
    end);



-- gif
imageView3 = Image()
imageView3.image("http://static1.squarespace.com/static/552a5cc4e4b059a56a050501/565f6b57e4b0d9b44ab87107/565f6ddee4b0d9b44ab89215/1449098068446/NYCGifathon2.gif", function() end)
imageView3.frame(scrW / 2 - 50, scrH / 2 + 100, 100, 100)
imageView3.callback(function()
    imageView3.adjustSize()
end)








