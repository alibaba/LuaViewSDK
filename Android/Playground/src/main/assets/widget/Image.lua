--
-- Created by IntelliJ IDEA.
-- User: tuoli
-- Date: 17/2/28
-- Time: 16:52
-- To change this template use File | Settings | File Templates.
--

require("kit.pica")

local function start()
    Pica:getInstance():render("widget/image.xml")
end

Navigation:title("Image.lua")
start()
