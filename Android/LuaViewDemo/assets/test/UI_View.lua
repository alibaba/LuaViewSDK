-- Created by LuaView.
-- Copyright (c) 2017, Alibaba Group. All rights reserved.
--
-- This source code is licensed under the MIT.
-- For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.

view = View()
view.frame(0, 10, 100, 100)
view.backgroundColor(0x11001100)
view.borderColor(0x020304)
view.borderWidth(0.3)
local x, y = view.center();
Toast("中心点位置: " .. x .. "," .. y)

print("background-color", view.backgroundColor(), 0x11001100)
--print("border-color", view.borderColor(), 0x020304)
--print("border-width", view.borderWidth(), 0.3)


view.backgroundColor(1)
--print(view.backgroundColor())

view.backgroundColor(0x12000001)
print(view.backgroundColor())

view.backgroundColor(0x000001, 0.5)
print(view.backgroundColor())

view.backgroundColor(0xff000001, 0.5)
print(view.backgroundColor())


print(view)
view.children(function(parent)
    print(parent)
    local child1 = View()
    child1.frame(2, 2, 90, 90)
    child1.backgroundColor(0x00ff00)
    print(child1)
    child1.children(function(parent)
        print(parent)
        local child2 = View()
        child2.frame(2, 2, 80, 80)
        child2.backgroundColor(0xff0000)
        print(child2)
        child2.children(function(parent)
            print(parent)
            local child3 = View()
            child3.frame(2, 2, 60, 60)
            child3.backgroundColor(0x770000ff)
        end)
    end)
end)



view3 = View()
view3.frame(120, 50, 100, 100)
view3.borderColor(0xff0000)
view3.borderWidth(2)

view4 = View()
view4.backgroundColor(0x0fffff)

view3.addView(view4)
view3.nativeView().setClipChildren(true)
view4.frame(5, -20, 90, 50)

view3.callback(function()
    print("view4.frame=", view4.frame())
    Animation().with(view4).translation(0, 40).duration(3).start()
end)

print("end")