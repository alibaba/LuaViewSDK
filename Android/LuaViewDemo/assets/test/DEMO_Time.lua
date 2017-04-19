-- Created by LuaView.
-- Copyright (c) 2017, Alibaba Group. All rights reserved.
--
-- This source code is licensed under the MIT.
-- For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.

view1 = View()
view1.cornerRadius(25)
view1.frame(50, 50, 100, 20)
view1.backgroundColor(0x0000ff, 0.5)


container = View()
container.frame(51, 51, 10, 20)


view2 = View()
container.addView(view2)
view2.cornerRadius(25)
view2.frame(0, 0, 98, 18)
view2.backgroundColor(0xff0000, 0.8)

local time = os:time();
local t = os:date("*t", time);
print("time", time, "t.year", t.year, "t.month", t.month,"t.day", t.day,"t.hour", t.hour,"t.min", t.min,"t.sec", t.sec);
local tb1 = {year=t.year, month=t.month, day=t.day, hour=10, min=0, sec=0, isdst=false};
local time1 = os:time(tb1) * 1000;
print("time1", time1);

local tb2 = {year=t.year, month=t.month, day=t.day, hour=20, min=0, sec=0, isdst=false};
local time2 = os:time(tb2) * 1000;
print("time2", time2);

local tb3 = {year=t.year, month=t.month, day=t.day, hour=25, min=0, sec=0, isdst=false};
local time3 = os:time(tb3) * 1000;
print("time3", time3);

local tb4 = {year=t.year, month=t.month, day=t.day, min=0, sec=0, isdst=false};
local time4 = os:time(tb4) * 1000;
print("time4", time4);




