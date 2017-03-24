window:frame(0, 80, 300, 120);

RED_COLOR = 0xf61d4b;
BLUE_COLOR = 0x26a96d;

btn1 = Button();
btn1:backgroundColor( RED_COLOR );

btn2 = Button();
btn2:backgroundColor( BLUE_COLOR );

btn3 = Button();
btn3:backgroundColor( RED_COLOR );

btn4 = Label();
btn4:backgroundColor( BLUE_COLOR );
btn4:text("hello, flexbox jdjljldjal;j;dfaljljjl!");

childCSSString = "flex: 1, margin: 5";
btn1:flxBindingCSS(childCSSString);
btn2:flxBindingCSS("flex: 2");
btn3:flxBindingCSS(childCSSString);
btn4:flxBindingCSS("sizetofit: 1, margin: 10, align-self:center, padding: 3.0");

window:flxChildNodes(btn1, btn2, btn3, btn4);
window:flxBindingCSS("flex-direction: column");
window:flxLayout(true, function()
    print("cccc");
    Animate( function()
        window:frame(10.0, 200.0, 300.0, 120.0);
    end);
end);