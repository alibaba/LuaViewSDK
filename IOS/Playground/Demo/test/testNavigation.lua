--工具包引入
require "kit/lv_kit"


--Navigation.setTitle("这是导航测试")

local button = Button()
button.text("测试view")
Navigation.title(button)


print(Navigation.title())