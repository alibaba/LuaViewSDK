--
-- Created by IntelliJ IDEA.
-- User: tuoli
-- Date: 17/3/22
-- Time: 12:02
-- To change this template use File | Settings | File Templates.
--

if (Platform == nil) then
   Platform = {}

   Platform.isAndroid = System:android()

   Platform.screenWidth, Platform.screenHeight = System:screenSize()

   Platform.contentWidth = Platform.screenWidth

   -- 减掉ActionBar和StatusBar的高度
   if (Platform.isAndroid) then
       local device = System:device()
       Platform.contentHeight = device.window_height - device.status_bar_height - device.nav_height
   else
       Platform.contentHeight = Platform.screenHeight - 64      -- iOS, 稳定在这个值
   end

   --
   -- Variables
   --
   if( not Platform.isAndroid ) then
       Platform.ScaleAspectFit = 1
       Platform.ScaleAspectFill = 2
       Platform.ScaleFitXY = 0
   else
       Platform.ScaleAspectFit = "FIT_CENTER"
       Platform.ScaleAspectFill = "CENTER_CROP"
       Platform.ScaleFitXY = "FIT_XY"
   end

   --
   -- Functions
   --
   function Platform:loadString(param)
       if (System:android()) then
           return load(param)
       else
           return loadstring(param)
       end
   end
end