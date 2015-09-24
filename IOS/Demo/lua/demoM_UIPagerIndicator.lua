
window.setBackgroundColor(0xeeddee);

local scrW, scrH = System.screenSize()



totalPageNum = 1;

pagerIndicator = UIPagerIndicator();
pagerIndicator.setPageCount(totalPageNum);

btnH = scrH/10;

btn1 = UIButton()
btn1.setText("totalPageNum")
btn1.setFrame(0, btnH*1.0, scrW, btnH)
btn1.setBackgroundColor(0xCCCCCC);
btn1.setCallback(function()
    totalPageNum = totalPageNum + 1;
    pagerIndicator.setPageCount(totalPageNum)
end)


index = 0;
btn3 = UIButton()
btn3.setBackgroundColor(0xCCCCCC);
btn3.setText("Change Page")
btn3.setFrame(0, btnH*4.0, scrW, btnH)
btn3.setCallback(function()
       index = index+1;
       pagerIndicator.setCurrentPage(index %totalPageNum + 1);
end)



pagerIndicator.setCenter(scrW/2, btnH*6);