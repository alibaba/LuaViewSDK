scrW, scrH = System.screenSize()
optionErrorView = Button()
optionErrorView.text("xxxx")
optionErrorView.frame(0, 0, scrW, scrH - 50);
optionErrorView.backgroundColor(0xffff00)
optionErrorView.callback(function()
    hiddenOptionErrorView();
end);

function showOptionErrorView()
    window.addView(optionErrorView);
end

function hiddenOptionErrorView()
    optionErrorView.removeFromSuper();
end

hiddenOptionErrorView();
showOptionErrorView();