
window.setBackgroundColor(0xeeddee);

local scrW, scrH = System.screenSize()





loading = UILoadingIndicator();

btnH = scrH/10;

btn1 = UIButton()
btn1.setText("Start")
btn1.setFrame(0, btnH*1.0, scrW, btnH)
btn1.setBackgroundColor(0xCCCCCC);
btn1.setCallback(function()
	print("start");
    loading.start()
end)


btn2 = UIButton()
btn2.setText("Stop")
btn2.setFrame(0, btnH*2.5, scrW, btnH)
btn2.setBackgroundColor(0xCCCCCC);
btn2.setCallback(function()
	print("stop");
       loading.stop()
end)


index = 0;
btn3 = UIButton()
btn3.setBackgroundColor(0xCCCCCC);
btn3.setText("Change Color")
btn3.setFrame(0, btnH*4.0, scrW, btnH)
btn3.setCallback(function()
	   print("stop");
	   if( index%2 == 0 ) then
       		loading.color(0xff0000);
       else 
       		loading.color(0xff00);
       end
       index = index+1;
end)



loading.setCenter(scrW/2, btnH*6);