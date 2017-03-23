-- Created by LuaView.
-- Copyright (c) 2017, Alibaba Group. All rights reserved.
--
-- This source code is licensed under the MIT.
-- For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.

panel = CustomError()
panel.backgroundColor(0xff0000)
panel.frame(0, 0, 200, 200)
panel.callback(function(message, arg1, arg2, arg3, arg4)
    print(message, arg1, arg2, arg3, arg4)
    if(value == "success") then
        -- do
    else
        -- do
    end
end)

print("rootView", panel.nativeView().getAlpha())


