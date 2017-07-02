-- Created by LuaView.
-- Copyright (c) 2017, Alibaba Group. All rights reserved.
--
-- This source code is licensed under the MIT.
-- For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.

-- Created by LuaView.
-- Copyright (c) 2017, Alibaba Group. All rights reserved.
--
-- This source code is licensed under the MIT.
-- For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.

code = [[
    a = {}
    a.x = function()
        local btn = Button()
        btn:frame(0, 0, 100, 100)
        btn:text("button")
--        btn:callback(function
--          Toast("btn clicked")
--        end)
    end

    a.x()
]]


--hi = load(code)
--hi()



load("local btn = Button(); btn:text('yyy'); btn:callback(function Toast('x') end)")()


