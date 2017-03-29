--
-- Created by IntelliJ IDEA.
-- User: tuoli
-- Date: 17/2/28
-- Time: 16:52
-- To change this template use File | Settings | File Templates.
--

require("kit.pica")

local function start()
    if (Sys.android) then
        Pica:getInstance():render("widget/textfield_android.xml")
    else
        Pica:getInstance():render("widget/textfield_ios.xml")
    end
end

Navigation:title("TextField.lua")
start()