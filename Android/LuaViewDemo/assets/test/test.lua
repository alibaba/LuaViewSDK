local view = View()
view.frame(0, 0, 100, 100)
view.backgroundColor(0xaaaaaa)
view.effects(ViewEffect.CLICK)

local view2 = View()
view2.frame(102, 0, 100, 100)
view2.backgroundColor(0x00ff00)


local image = Image()
image.frame(100, 100, 100, 100)
image.image("https://gju1.alicdn.com/bao/uploaded/i4/100000120700895002/TB2Qu0_fXXXXXb9XpXXXXXXXXXX_!!0-0-juitemmedia.jpg_560x560Q90.jpg")
image.backgroundColor(0xdcdcdc)
image.effects(ViewEffect.CLICK)