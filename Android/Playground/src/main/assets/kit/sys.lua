--
-- Copyright 2017 Alibaba Group
-- License: MIT
-- Website: https://alibaba.github.io/LuaViewSDK
-- User: tuoli
-- Date: 17/3/30
--

if (sys == nil) then
    sys = {}

   --
   -- Variables
   --
   sys.android = System:android()
   sys.scrW, sys.scrH = System:screenSize()
   sys.contW = sys.scrW
   sys.scale = sys.scrW/375 > 1 and 1 or sys.scrW/375 <= 1 and sys.scrW/375
   -- 减掉ActionBar和StatusBar的高度
   if (sys.android) then
       local device = System:device()
       sys.contH = device.window_height - device.status_bar_height - device.nav_height
   else
       sys.contH = sys.scrH - 64      -- iOS, 稳定在这个值
   end

   --
   -- Functions
   --
   function sys:loadstring(param)
       if (sys.android) then
           return load(param)
       else
           return loadstring(param)
       end
   end
end