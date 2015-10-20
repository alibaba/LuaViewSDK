
window.backgroundColor(0xeeddee);

local scrW, scrH = System.screenSize()



totalPageNum = 1;

pagerIndicator = PagerIndicator();
pagerIndicator.pageCount(totalPageNum);

btnH = scrH/10;

btn1 = Button()
btn1.text("totalPageNum")
btn1.frame(0, btnH*1.0, scrW, btnH)
btn1.backgroundColor(0xCCCCCC);
btn1.click(function()
    totalPageNum = totalPageNum + 1;
    pagerIndicator.pageCount(totalPageNum)
end)


index = 0;
btn3 = Button()
btn3.backgroundColor(0xCCCCCC);
btn3.text("Change Page")
btn3.frame(0, btnH*4.0, scrW, btnH)
btn3.click(function()
       index = index+1;
       pagerIndicator.currentPage(index %totalPageNum + 1);
end)



pagerIndicator.center(scrW/2, btnH*6);