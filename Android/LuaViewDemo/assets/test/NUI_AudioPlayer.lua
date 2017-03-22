-- Created by LuaView.
-- Copyright (c) 2017, Alibaba Group. All rights reserved.
--
-- This source code is licensed under the MIT.
-- For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.

w, h = System.screenSize();


local player1 = AudioPlayer();

button1 = Button();
button1.title("播放音效-url");
button1.callback(function()
    if (player1) then
        player1.play("http://dx.sc.chinaz.com/files/download/sound1/201308/3381.wav", 2);
    end
end)

button2 = Button();
button2.title("播放音效-本地");
button2.xy(200, 0)
button2.callback(function()
    if (player1) then
        player1.play("music/test.mp3", 0);
    end
end)

button1_pause = Button();
button1_pause.title("pause")
button1_pause.xy(0, 50)
button1_pause.callback(function()
    if (player1) then
        player1.pause();
    end
end)

button1_resume = Button();
button1_resume.title("resume")
button1_resume.xy(200, 50)
button1_resume.callback(function()
    if (player1) then
        player1.resume();
    end
end)








