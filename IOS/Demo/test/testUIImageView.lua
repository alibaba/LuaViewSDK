--1. 普通设置url，size，center，contentmode等
view = Image()
view.size(500, 400)
view.center(50, 50)
print(view.size())
view.backgroundColor(0xffaa0000)
view.callback(function()
    print("Button Clicked")
end)
view.contentMode(2)
view.image("https://gju1.alicdn.com/bao/uploaded/i1/100000119234047241/TB2JnnKeVXXXXcpXXXXXXXXXXXX_!!0-0-juitemmedia.jpg_560x560Q90.jpg")


--2. 普通构造传url
view2 = Image("https://gju1.alicdn.com/bao/uploaded/i4/100000120700895002/TB2Qu0_fXXXXXb9XpXXXXXXXXXX_!!0-0-juitemmedia.jpg_560x560Q90.jpg")
view2.frame(0, 300, 300, 300)


--3. 动画
view3 = Image("https://gju1.alicdn.com/bao/uploaded/i4/100000120700895002/TB2Qu0_fXXXXXb9XpXXXXXXXXXX_!!0-0-juitemmedia.jpg_560x560Q90.jpg");
images = {}
images[1] = "animate1"
images[2] = "animate2"
view3.backgroundColor(0xff00aa00)
view3.frame(300, 600, 300, 600)
view3.startAnimation(images, 1 , 0)





