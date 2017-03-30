--
-- Created by IntelliJ IDEA.
-- User: tuoli
-- Date: 17/2/28
-- Time: 16:52
-- To change this template use File | Settings | File Templates.
--

require("kit.pica")

local function start()
    loadingObjs = Pica:getInstance():render("widget/loading.xml")

    local loading = loadingObjs["loading"]
    loading:start()
end

Navigation:title("Loading.lua")
start()

