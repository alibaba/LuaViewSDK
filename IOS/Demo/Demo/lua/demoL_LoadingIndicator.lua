
window:backgroundColor(0xeeddee);

local scrW, scrH = System:screenSize()





loading = LoadingIndicator();

btnH = scrH/10;

btn1 = Button()
btn1:text("Start")
btn1:frame(0, btnH*1.0, scrW, btnH)
btn1:backgroundColor(0xCCCCCC);
btn1:callback(function()
	print("start");
    loading:start()
end)


btn2 = Button()
btn2:text("Stop")
btn2:frame(0, btnH*2.5, scrW, btnH)
btn2:backgroundColor(0xCCCCCC);
btn2:callback(function()
	print("stop");
       loading:stop()
end)


index = 0;
btn3 = Button()
btn3:backgroundColor(0xCCCCCC);
btn3:text("Change Color")
btn3:frame(0, btnH*4.0, scrW, btnH)
btn3:callback(function()
	   print("stop");
	   if( index%2 == 0 ) then
       		loading:color(0xff0000);
       else 
       		loading:color(0xff00);
       end
       index = index+1;
end)


loading:size(30,30);
loading:center(scrW/2, btnH*6);


