-- Created by LuaView.
-- Copyright (c) 2017, Alibaba Group. All rights reserved.
--
-- This source code is licensed under the MIT.
-- For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.

function createButton(text, x, y, w, h, callback)
    local button = Button();
    button.frame(x, y, w, h);
    button.text(text);
    button.backgroundColor(0xfa0000);
    button.callback(function()
        if (callback) then
            callback();
        end
    end);
    button.adjustSize()
    return button;
end

button1 = createButton("123456中文", 10, 10, 1000, 60);

button2 = Image();
button2.frame(10, 100 + 80, 100, 100);
button2.image("http://g.alicdn.com/ju/lua/2.0.25/doc/icon.png");

button2.callback(function()
    print("我是图片按钮");
    button2.adjustSize();
end);

obj = {};

function obj.test(a)
    print(self, a);
end

obj.test "good";

button3 = Button()
button3.frame(150, 180, 100, 100)
button3.image("animate2", "http://online.sccnn.com/icon/517/actionIcons_006.png")
button3.callback(function()
    print("button3 clicked")
end)