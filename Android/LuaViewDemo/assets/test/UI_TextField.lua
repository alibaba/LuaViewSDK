--工具包引入
require "kit/lv_kit"

local width = System.screenSize()

local tf = TextField()
tf.hint(StyledString("测试额", {fontColor=0xff0000ff, fontSize=80, fontStyle=3, backgroundColor=0x99ff0000}))
tf.frame(0, 0, width, 100)

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
btn1.frame(0, 100, width, 100)



