LIGHT_GRAY = 0xf5edb3
RED_COLOR = 0xf61d4b
BLUE_COLOR = 0x26a96d
GREEN_COLOR = 0x05A5D1
MAGENTA_COLOR = 0xff00ff
MOCCASIN_COLOR = 0xffe4b5
OLIVEDRAB_COLOR = 0x6b8e23

w,h = System:screenSize()

-- 依次从上往下对应
contentView = View()
contentView:frame(0, 0, w, 400)
contentView:backgroundColor(LIGHT_GRAY)

-- 对应第一个纯色的红色的view
view1 = View()
view1:backgroundColor(RED_COLOR)
view1:flexCss("flex: 1, margin: 5")

-- 对应第二个绿色的View，这个View有5个子View，从左往右排列
view2 = View()
view2:backgroundColor(BLUE_COLOR)

-- 左边红色与相邻的蓝色，宽度关系表示为1：3
view3 = View()
view3:backgroundColor(RED_COLOR)
view3:flexCss("flex: 1")

-- View4 蓝色这块也有一些子view, 这里解释了如何换行
view4 = View()
view4:backgroundColor(GREEN_COLOR)
view4:flexCss("flex: 3, flex-direction: row-reverse, align-items: stretch, flex-wrap: 1, align-content: flex-start, justify-content: center")

view41 = View()
view41:backgroundColor(MAGENTA_COLOR)
view41:flexCss("width: 50, align-self: stretch, margin: 5, height: 25")

view42 = Label()
view42:backgroundColor(MOCCASIN_COLOR)
view42:text("flex box")
view42:lineCount(0)
view42:flexCss("margin: 5, sizetofit: 1, align-self: center, margin-top: 20")

view43 = Label()
view43:backgroundColor(OLIVEDRAB_COLOR)
view43:flexCss("margin-left: 10, sizetofit: 1, align-self: center")
view43:text("you will like it!")
view43:lineCount(1)

view4:flexChildren(view41, view42, view43)

view5 = View()
view5:backgroundColor(RED_COLOR)
view5:flexCss("width: 40, height: 40, margin-left: 10, align-self: center")

image = Image()
image:image("http://img4.tbcdn.cn/L1/461/1/img_20151116194256.JPEG_240x240")
image:flexCss("width: 30, height: 30, align-self: flex-end")

-- 一个绝对定位的view，黑色的那块，与其他子不发生关系，只与父发生关系
view6 = View()
view6:backgroundColor(0x00)
view6:flexCss("width: 50, height: 50, position: absolute, left: 5, top: 5")

view2:flexChildren(view3, view4, image, view5, view6)
view2:flexCss("flex: 2, padding: 10.0, flex-direction: row, align-items: stretch")

label = Label()
label:backgroundColor(GREEN_COLOR)
label:text("The Réunion parrot or Dubois's parrot (Necropsittacus borbonicus) is a hypothetical extinct species of parrot based on descriptions of birds from the Mascarene island of Réunion. ")
label:flexCss("margin: 20, sizetofit: 1")
label:lineCount(0)

contentView:flexChildren(view1, view2, label)
contentView:flexCss("flex-direction: column")
contentView:flxLayout(true, function()
print("cccc")
end)

view2:callback(function()
view3:flexCss("flex: 2")
view4:flexCss("flex: 1")

contentView:flxLayout()
end)
