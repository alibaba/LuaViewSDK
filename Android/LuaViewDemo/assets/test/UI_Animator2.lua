-- Created by LuaView.
-- Copyright (c) 2017, Alibaba Group. All rights reserved.
--
-- This source code is licensed under the MIT.
-- For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.

sw, sh = System.screenSize()
x = sw / 2 - 50
y = sh / 2 - 50

w = 100
h = 100

sw = 100
sh = 100

dx = 20
dy = 20

xr = x + w
yb = y + h


container = View()
container.frame(x, y, w, h)
container.borderColor(0xff0000)
container.borderWidth(1)


x = 0
y = 0
xr = x + w
yb = y + h

--左
v1 = View()
v1.frame(x - sw + dx, y, sw, sh)
v1.backgroundColor(0xf00000, 0.5)
container.addView(v1)

--上
v2 = View()
v2.frame(x, y - sh + dy, sw, sh)
v2.backgroundColor(0x0f0000, 0.5)
container.addView(v2)

-- 右
v3 = View()
v3.frame(xr - dx, y, sw, sh)
v3.backgroundColor(0x00f000, 0.5)
container.addView(v3)

-- 下
v4 = View()
v4.frame(x, yb - dy, sw, sh)
v4.backgroundColor(0x0000f0, 0.5)
container.addView(v4)

container.nativeView().setClipChildren(true)
container.callback(function()
    t = 2

    print("v1", v1.frame())
    print("v2", v2.frame())
    print("v3", v3.frame())
    print("v4", v4.frame())


    --V1
    tx, ty = v1.xy()
    Animation().with(v1).translation(-tx, -ty).duration(t).start()

    --V2
    tx, ty = v2.xy()
    Animation().with(v2).translation(-tx, -ty).delay(t).duration(t).start()

    --V3
    tx, ty = v3.xy()
    Animation().with(v3).translation(-tx, -ty).delay(2 * t).duration(t).start()

    --v4
    tx, ty = v4.xy()
    Animation().with(v4).translation(-tx, -ty).delay(3 * t).duration(t).start()

    print("container clicked")

end)

print("end")