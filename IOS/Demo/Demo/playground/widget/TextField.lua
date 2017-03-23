--
-- Created by IntelliJ IDEA.
-- User: tuoli
-- Date: 17/2/28
-- Time: 16:52
-- To change this template use File | Settings | File Templates.
--

require("kit.pickup")

local function start()
    if (Platform.isAndroid) then
        Pickup:getInstance():render("widget/textfield_android.xml")
    else
        Pickup:getInstance():render("widget/textfield_ios.xml")
    end
end

Navigation:title("TextField.lua")
start()