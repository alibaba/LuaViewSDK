-- Created by LuaView.
-- Copyright (c) 2017, Alibaba Group. All rights reserved.
--
-- This source code is licensed under the MIT.
-- For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.

w, h = System.screenSize();


local vibrator = Vibrator();

button1 = Button();
button1.title("震动-1");
button1.callback(function()
    if (vibrator) then
        vibrator.vibrate();
    end
end)

button2 = Button();
button2.title("震动-2");
button2.xy(100, 0)
button2.callback(function()
    if (vibrator) then
        vibrator.vibrate(2)
    end
end)

button3 = Button();
button3.title("震动-3")
button3.xy(0, 50)
button3.callback(function()
    if (vibrator) then
        vibrator.vibrate({3})
    end
end)

button4 = Button();
button4.title("震动-4")
button4.xy(100, 50)
button4.callback(function()
    if (vibrator) then
        vibrator.vibrate({1, 2, 1, 0.3, 0.2, 0.1, 0.01, 1.1}, 4)
    end
end)


button5 = Button();
button5.title("震动-cancel")
button5.xy(100, 100)
button5.callback(function()
    if (vibrator) then
        vibrator.cancel()
    end
end)








