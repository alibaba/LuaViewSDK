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
    --tf.text("点击了")
    print( tf.text() );
end)


btn1.text("点我试试")
btn1.frame(0, 300, width, 200)



att1 = StyledString( "123test",  { fontSize=12, fontWeight=FontWeight.BOLD, fontColor=0x220000ff, foregroundAlpha=0.5 } );
att2 = StyledString( "测试测试",  { fontSize=30, fontColor=0xff0000, backgroundColor=0xff} );
att3 = StyledString( "2897348",  { fontSize=13, fontColor=0x777777, strikethrough =1} );
att4 = StyledString( "下划线测试", { fontSize=9, fontColor=0x777777, underline=true} );

tf.text(att1+att2+att3+att4);


