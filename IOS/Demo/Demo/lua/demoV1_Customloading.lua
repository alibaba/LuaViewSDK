
scrW,scrH = System:screenSize();

x0 = scrW/6;

customLoading = CustomLoading();

customLoading:frame(x0, x0, scrW-x0*2, scrH/2);

customLoading:backgroundColor(0xff0000);
