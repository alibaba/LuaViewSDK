
w,h = System:screenSize();
window:frame(0, 0, w, h);
window:backgroundColor(0xDDDDDD);

Y0 = 50

att1 = StyledString( "123test",  { fontSize=12, fontWeight=FontWeight.BOLD, fontColor=0x220000ff, foregroundAlpha=0.5 } );
att2 = StyledString( "测试测试",  { fontSize=30, fontColor=0xff0000, backgroundColor=0xff} );
iconFontAtt = StyledString( Unicode(0xe611),  {fontName="JHSIconfont", fontSize=30, fontColor=0xff0000 } );
att3 = StyledString( "2897348",  { fontSize=13, fontColor=0x777777, strikethrough =1} );
att4 = StyledString( "下划线测试", { fontSize=9, fontColor=0x777777, underline=true} );

label = Label();
label:frame(0,Y0,w, 60);
label:text(att1 + att2 +iconFontAtt+ att3 + att4 + "字符串");



label2 = Label();
label2:frame(0,Y0+100,w, 60);
label2:font("JHSIconfont", 18);
label2:text(Unicode(0xe611));
