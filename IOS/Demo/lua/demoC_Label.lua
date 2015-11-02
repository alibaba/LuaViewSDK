
w,h = System.screenSize();
window.frame(0, 0, w, h);
window.backgroundColor(0xDDDDDD);

Y0 = 50

att1 = StyledString( "123test",  { fontSize=12, fontWeight="bold", fontColor=0x220000ff, foregroundAlpha=0.5 } );
att2 = StyledString( "测试测试",  { fontSize=30, fontColor=0xff0000, backgroundColor=0xff} );
att3 = StyledString( "2897348",  { fontSize=13, fontColor=0x777777, strikethrough =1} );
att4 = StyledString( "下划线测试", { fontSize=9, fontColor=0x777777, underline=true} );

label = Label();
label.frame(0,Y0,w, 60);
label.text(att1 + att2 + att3 + att4 + "字符串");



label2 = Label();
label2.frame(0,Y0+100,w, 60);
label2.text("测试我是文本框");
label2.backgroundColor(0xEEEEEE);
label2.textAlign(TextAlign.CENTER);


lable3 = Label();
lable3.frame( 10, 250, w, 60);
lable3.backgroundColor(0xEEEEEE);
lable3.text("测试我是测试");
lable3.textAlign(TextAlign.CENTER)
lable3.callback(
    function()
        print("lable3");
    end
);




button = Button();
button.frame( 10, 320, w, 60 );
button.backgroundColor(0xeeDDee);
button.title("按钮");
button.callback(function()
lable3.adjustSize();
button.title(att1+att2);
end)

button3 = Button();
button3.frame( 10, 390, w, 60 );
button3.backgroundColor(0xeeDDee);
button3.title("按钮");
button3.callback(function()
lable3.frame( 10, 250, w, 60);
end)