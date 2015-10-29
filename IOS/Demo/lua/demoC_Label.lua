
w,h = System.screenSize();
window.frame(0, 0, w, h);
window.backgroundColor(0xDDDDDD);

Y0 = 50

att1 = StyledString( "123test",  { fontSize=12, fontWeight="bold", foregroundColor=0x220000ff, foregroundAlpha=0.5 } );
att2 = StyledString( "测试测试",  { fontSize=30, foregroundColor=0xff0000, backgroundColor=0xff} );
att3 = StyledString( "2897348",  { fontSize=13, foregroundColor=0x777777, strikethrough =1} );
att4 = StyledString( "下划线测试", { fontSize=9, foregroundColor=0x777777, underline=true} );

label = Label();
label.frame(0,Y0,w, 60);
label.text(att1 + att2 + att3 + att4);



label2 = Label();
label2.frame(0,Y0+100,w, 60);
label2.text("测试我是文本框");
label2.backgroundColor(0xEEEEEE);
label2.textAlign(TextAlign.CENTER);


lable3 = Label();
lable3.frame( 10, 250, 100, 60);
lable3.backgroundColor(0xEEEEEE);
lable3.text("测试我是测试我是测试我是测试我是测试我是");




button = Button();
button.frame( 10, 320, w, 60 );
button.backgroundColor(0xeeDDee);
button.title("按钮");
button.callback(function()
    lable3.adjustSize();
    button.title(att1+att2);
end)