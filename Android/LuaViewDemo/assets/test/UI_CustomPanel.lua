-- 简单动画
panel = CustomError()
panel.backgroundColor(0xff0000)
panel.frame(0, 0, 200, 200)
panel.callback(function(value)
    print(value)
end)