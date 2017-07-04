--
-- Created by IntelliJ IDEA.
-- User: song

--1 一个按钮
Alert("一个按钮", "文字", "确定", function()
    print("点击了")
end)

--2 两个按钮
Alert("两个按钮", "文字", "确定", "取消", function()
    print("点击了")
end, function()
    print("取消了")
end)

--3 只有内容
Alert(nil, "只有内容")

--4 只有标题
Alert("只有标题")
