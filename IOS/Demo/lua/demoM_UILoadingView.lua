
window.setBackgroundColor(0xeeddee);

local scrW, scrH = System.screenSize()


btn1 = UIButton()
btn1.setCallback(function()
	print("start");
    loading.start()
end)

btn2 = UIButton()
btn2.setCallback(function()
	print("stop");
       loading.stop()
end)

loading = UILoadingView();
loading.setCenter(scrW/2, scrH/2);

btnH = scrH/10;

btn1.setText("Start")
btn1.setFrame(0, btnH*1.0, scrW, btnH)

btn2.setText("Stop")
btn2.setFrame(0, btnH*2.5, scrW, btnH)


