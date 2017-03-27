--
-- Created by IntelliJ IDEA.
-- User: tuoli
-- Date: 17/2/28
-- Time: 16:52
-- To change this template use File | Settings | File Templates.
--

require("kit.pickup")

local function start()
    Pickup:getInstance():render("widget/image.xml")
end

Navigation:title("Image.lua")
start()
