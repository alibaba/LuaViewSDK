local btn = Button()
btn.frame(0, 0, 200, 200)
btn.text("测试Button")
btn.callback(function()
    Toast("clicked")
end)


local view = View()
view.frame(210, 0, 200, 200)
view.backgroundColor(0xff0000, 0.3)
view.callback(function()
    Toast("clicked")
end)