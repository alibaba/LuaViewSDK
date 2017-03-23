-- Created by LuaView.
-- Copyright (c) 2017, Alibaba Group. All rights reserved.
--
-- This source code is licensed under the MIT.
-- For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.

require "kit/lv_kit"

print(type(System))
print(System.ios())
print(System.android())
print(System.vmVersion())
print(System.osVersion())
print(System.platform())
print(System.scale())
printTable(System.device())
print(System.screenSize())

-- network
Timer(function()
    print("Timer--" .. System.network())
end).start(1, true)

System.gc()

print(window.size())

btn1 = Button();
btn1.text("Screen-On")
btn1.xy(0, 0)
btn1.callback(function()
    System.keepScreenOn()
end)

btn2 = Button()
btn2.xy(120, 0)
btn2.text("Screen-Off")
btn2.callback(function()
    System.keepScreenOn(false)
end)



print("System.ios = " , System.ios() )
print("System.android = ", System.android() )
print("System.vmVersion = ", System.vmVersion() )
print("System.osVersion = ", System.osVersion() )	--获取手机操作系统版本号
print("System.platform = ", System.platform() )	--获取平台信息
print("System.scale = ", System.scale() )	--获取屏幕分辨率
print("System.device = ", System.device() )	--获取设备信息
print("System.screenSize() = ", System.screenSize() );