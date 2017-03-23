-- Created by LuaView.
-- Copyright (c) 2017, Alibaba Group. All rights reserved.
--
-- This source code is licensed under the MIT.
-- For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.

require "kit/lv_kit"

local label = Label()
label.text("请看日志输出")
label.frame(0, 0, System.screenSize())

http = Http()
http.get("http://www.baidu.com", {
    query = 1
}, function(response)
    print(response)
    printTable(response)
    print(response.data())
end)


