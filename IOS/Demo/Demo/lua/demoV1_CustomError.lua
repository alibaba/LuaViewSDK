
scrW,scrH = System.screenSize();

x0 = scrW/6;

y0 = scrH/10;

w = scrW - x0*2;

h = scrH / 3;

customError = CustomPanel( "CustomError", x0,y0,w,h );
customError.backgroundColor(0xff0000);
customError.callback( function(tag, a,b,c)
    print("customError", tag,a,b,c);
end);




customError2 = CustomError(x0,y0*2 + h,w,h );
customError2.backgroundColor(0xff0000);
customError2.callback( function(tag)
    print("customError2", tag);
end)