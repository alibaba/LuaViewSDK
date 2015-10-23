--工具包引入
require "kit/lv_kit"

local width = System.screenSize()

local tf = TextField()
tf.hint("测试额")
tf.frame(0, 0, width, 150)

tf.callback({
    BeginEditing=function()
        Toast("开始编辑")
    end,

    EndEditing=function()
        Toast("结束编辑")
    end
})

local btn1 = Button()
btn1.callback(function()
    tf.setText("点击了")
end)


btn1.text("点我试试")
btn1.frame(0, 500, width, 200)



