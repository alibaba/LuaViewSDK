view = View()
view.frame(100, 100, 200, 300)
view.backgroundColor(0x44aa0000)
local x, y = view.center();
Toast("中心点位置: " .. x .. "," .. y)