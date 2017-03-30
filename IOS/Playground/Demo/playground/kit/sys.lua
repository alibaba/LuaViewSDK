--
-- Created by IntelliJ IDEA.
-- User: tuoli
-- Date: 17/3/22
-- Time: 12:02
-- To change this template use File | Settings | File Templates.
--

if (Sys == nil) then
    Sys = {}

   --
   -- Variables
   --
   Sys.android = System:android()
   Sys.scrW, Sys.scrH = System:screenSize()
   Sys.contW = Sys.scrW
   Sys.scale = Sys.scrW/375 > 1 and 1 or Sys.scrW/375 <= 1 and Sys.scrW/375
   -- 减掉ActionBar和StatusBar的高度
   if (Sys.android) then
       local device = System:device()
       Sys.contH = device.window_height - device.status_bar_height - device.nav_height
   else
       Sys.contH = Sys.scrH - 64      -- iOS, 稳定在这个值
   end

   --
   -- Functions
   --
   function Sys:loadString(param)
       if (Sys.android) then
           return load(param)
       else
           return loadstring(param)
       end
   end
end