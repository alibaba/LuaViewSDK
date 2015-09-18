window.setFrame(0, 80, 300, 120);

RED_COLOR = 0xf61d4b;
BLUE_COLOR = 0x26a96d;

btn1 = UIButton();
btn1.setBackgroundColor = RED_COLOR;

btn2 = UIButton();
btn2.setBackgroundColor = BLUE_COLOR;

btn3 = UIButton();
btn3.setBackgroundColor = RED_COLOR;

btn4 = UILabel();
btn4.setBackgroundColor = BLUE_COLOR;
btn4.setText("hello, flexbox jdjljldjal;j;dfaljljjl!");

childCSSString = "flex: 1, margin: 5";
btn1.flxBindingCSS(childCSSString);
btn2.flxBindingCSS("flex: 2");
btn3.flxBindingCSS(childCSSString);
btn4.flxBindingCSS("sizetofit: 1, margin: 10, align-self:center, padding: 3.0");

window.flxChildNodes(btn1, btn2, btn3, btn4);
window.flxBindingCSS("flex-direction: column");
window.flxLayout(true, ^() {
print("cccc");
    UIAnimate(^(){
        window.setFrame(10.0, 200.0, 300.0, 120.0);
    });
});