-- 简单动画
btn = Button();
btn.size(60, 120)
btn.text("按钮");

btn.callback({
    onShow = function()
        print("show")
    end,
    onHide = function()
        print("hide")
    end
})


btn2 = Button();
btn2.text("按钮2")
btn2.size(60, 120)
btn2.xy(100, 300)



animator1 = Animation().alpha(0.1).duration(3);
animator2 = Animation().translationX(100).duration(2).delay(1);
animator3 = Animation().rotation().value(100).duration(2);


--btn.startAnimation(animator1, animator2)
--btn.startAnimation(animator1)

--animator1.with(btn).start();
--animator2.with(btn).start();
--animator3.with(btn).start();

btn.startAnimation(animator1, animator2, animator3)
btn2.startAnimation(animator1, animator2, animator3)
