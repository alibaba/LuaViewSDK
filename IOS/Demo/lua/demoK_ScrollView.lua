--工具包引入
require "lv_kit"

local scrW, scrH= System.screenSize()

scrollView = ScrollView ();
buttons = {};

scrollView.frame(0, scrH/5, scrW, scrH/5 )
scrollView.contentSize( scrW*3, scrH/5 );
for i=1,10 do
    local button = Button();
    button.frame( (i-1)*scrW, 0, scrW,scrH/5);
    button.title("hhaha" .. i);
    scrollView.addView(button);
    buttons[i] = button;
end
scrollView.backgroundColor(0xff0000);

scrollView.scrollingCallback(function ( )
	print("scrolling");
end);


