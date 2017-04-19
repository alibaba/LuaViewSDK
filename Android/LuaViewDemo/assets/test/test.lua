-- Created by LuaView.
-- Copyright (c) 2017, Alibaba Group. All rights reserved.
--
-- This source code is licensed under the MIT.
-- For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.

require ""
str = {"中","华","人","民","共","和","国","万","岁"}


txt = Label()
txt.frame(0, 0, 200, 300)
txt.backgroundColor(0xdcdcdc)

index = 1

t = ""

timer = Timer()
timer.callback(function()
--    t = t .. string:char(str, index)

    print(string:char(str, index))
--    txt.text(string:char(str, 1, index))
--    index = index + SubStringGetByteCount(str, index)

--    t = t .. str[index]
    txt.text(t)
    index = index + 1

end)

--返回当前字符实际占用的字符数
function SubStringGetByteCount(str, index)
    local curByte = string:byte(str, index)
    local byteCount = 1;
    if curByte == nil then
        byteCount = 0
    elseif curByte > 0 and curByte <= 127 then
        byteCount = 1
    elseif curByte>=192 and curByte<223 then
        byteCount = 2
    elseif curByte>=224 and curByte<239 then
        byteCount = 3
    elseif curByte>=240 and curByte<=247 then
        byteCount = 4
    end
    return byteCount;
end

timer.start(0.5, true)