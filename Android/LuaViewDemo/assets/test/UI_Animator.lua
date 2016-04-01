-- 简单动画
btn = Button();
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
btn2.xy(100, 300)



animator1 = Animation().alpha(0.3).duration(3);
animator2 = Animation().translationX(200).duration(2).delay(1);
animator3 = Animation().rotation(100).duration(2);


--btn.startAnimation(animator1, animator2)
--btn.startAnimation(animator1)

--animator1.with(btn).start();
--animator2.with(btn).start();
--animator3.with(btn).start();

btn.startAnimation(animator1, animator2, animator3)
btn2.startAnimation(animator1, animator2, animator3)

print(btn.xy())
btn.callback(function()
    Toast("btn1 clicked")
    local anim = Animation().translationX(100).duration(2).delay(1);
    print(btn.xy())
    btn.startAnimation(anim)
end)

btn2.callback(function()
    btn2.stopAnimation()
    Toast("btn2 clicked")
end)


tipsView = View()
tipsView.backgroundColor(0xff0000)
tipsView.frame(50, 50, 50, 50)
tipsView.scale(0, 0)

local anim1 = Animation().duration(3).scaleX(1)
local anim2 = Animation().duration(3).scaleY(1)

tipsView.startAnimation(anim1, anim2);


print("load finish")



image = Image()
image.url("")