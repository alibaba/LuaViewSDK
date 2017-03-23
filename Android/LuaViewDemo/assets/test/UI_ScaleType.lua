-- Created by LuaView.
-- Copyright (c) 2017, Alibaba Group. All rights reserved.
--
-- This source code is licensed under the MIT.
-- For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.

--
-- Created by IntelliJ IDEA.
-- User: song
-- Date: 16/10/13
-- Time: 下午2:32
-- To change this template use File | Settings | File Templates.
--

--2. 普通构造传url
v1 = Image()
v1.frame(0, 0, 100, 100)
v1.scaleType(ScaleType.FIT_XY)
v1.image("https://gju1.alicdn.com/bao/uploaded/i4/100000120700895002/TB2Qu0_fXXXXXb9XpXXXXXXXXXX_!!0-0-juitemmedia.jpg_560x560Q90.jpg")

v2 = Image()
v2.frame(101, 0, 100, 100)
v2.scaleType(ScaleType.FIT_START)
v2.image("https://gju1.alicdn.com/bao/uploaded/i4/100000120700895002/TB2Qu0_fXXXXXb9XpXXXXXXXXXX_!!0-0-juitemmedia.jpg_560x560Q90.jpg")

v3 = Image()
v3.frame(202, 0, 100, 100)
v3.scaleType(ScaleType.FIT_END)
v3.image("https://gju1.alicdn.com/bao/uploaded/i4/100000120700895002/TB2Qu0_fXXXXXb9XpXXXXXXXXXX_!!0-0-juitemmedia.jpg_560x560Q90.jpg")


v4 = Image()
v4.scaleType(ScaleType.FIT_CENTER)
v4.image("https://gju1.alicdn.com/bao/uploaded/i4/100000120700895002/TB2Qu0_fXXXXXb9XpXXXXXXXXXX_!!0-0-juitemmedia.jpg_560x560Q90.jpg")
v4.frame(0, 101, 100, 100)

v5 = Image()
v5.scaleType(ScaleType.CENTER)
v5.frame(101, 101, 100, 100)
v5.image("https://gju1.alicdn.com/bao/uploaded/i4/100000120700895002/TB2Qu0_fXXXXXb9XpXXXXXXXXXX_!!0-0-juitemmedia.jpg_560x560Q90.jpg")

--TODO
v6 = Image()
v6.scaleType(ScaleType.CENTER_CROP)
v6.image("https://gju1.alicdn.com/bao/uploaded/i4/100000120700895002/TB2Qu0_fXXXXXb9XpXXXXXXXXXX_!!0-0-juitemmedia.jpg_560x560Q90.jpg")
v6.frame(202, 101, 100, 100)


v7 = Image()
v7.scaleType(ScaleType.CENTER_INSIDE)
v7.image("https://gju1.alicdn.com/bao/uploaded/i4/100000120700895002/TB2Qu0_fXXXXXb9XpXXXXXXXXXX_!!0-0-juitemmedia.jpg_560x560Q90.jpg")
v7.frame(0, 202, 100, 100)

--TODO
v8 = Image()
v8.scaleType(ScaleType.MATRIX)
v8.image("https://gju1.alicdn.com/bao/uploaded/i4/100000120700895002/TB2Qu0_fXXXXXb9XpXXXXXXXXXX_!!0-0-juitemmedia.jpg_560x560Q90.jpg")
v8.frame(101, 202, 100, 100)


v9 = Image()
v9.scaleType(ScaleType.CENTER_CROP)
v9.image("https://gju1.alicdn.com/bao/uploaded/i4/100000120700895002/TB2Qu0_fXXXXXb9XpXXXXXXXXXX_!!0-0-juitemmedia.jpg_560x560Q90.jpg")
v9.scale(1.2, 1.2)
v9.frame(0, 303, 100, 100)





