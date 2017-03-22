-- Created by LuaView.
-- Copyright (c) 2017, Alibaba Group. All rights reserved.
--
-- This source code is licensed under the MIT.
-- For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.

view = Image()
view.frame(10, 10, 100, 100)
view.image("animate3")
view.cornerRadius(50)
view.borderColor(0x33ff0000)
view.borderWidth(2)


view2 = Image()
view2.frame(120, 10, 100, 100)
view2.image("animate1")
view2.cornerRadius(20)
view2.borderColor(0xff0000)
view2.borderWidth(2)


view3 = Image()
view3.frame(230, 10, 100, 100)
view3.image("animate1")
view3.cornerRadius(20)
view3.borderColor(0xff0000)
view3.borderWidth(2)
view3.borderDash(5, 2)