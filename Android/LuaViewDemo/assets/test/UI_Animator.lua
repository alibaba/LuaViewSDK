-- 简单动画
btn = Button();
btn.text("按钮");
btn.xy(0, 0)

btn.callback(function()
    Toast("btn1 clicked")
    local anim = Animation().translationX(100).duration(2).delay(1);
    btn.startAnimation(anim)
end)

animator1 = Animation().alpha(0.3).duration(3);
animator2 = Animation().translationX(100).duration(2).delay(1);
animator3 = Animation().rotation(100).duration(2);

btn2 = Button();
btn2.text("按钮2")
btn2.xy(0, 100)
btn2.callback(function()
    btn2.startAnimation(animator1, animator2, animator3)
    Toast("btn2 clicked")
end)


local anim1 = Animation().duration(2).scale(0.3, 0.5).alpha(1).callback({
    onStart=function()
        print("anim1-start")
    end,
    onEnd=function()
        print("anim1-end")
    end
})
local anim2 = Animation().delay(1).duration(2).translation(100, 150).alpha(0.5).interpolator(Interpolator.ANTICIPATE).callback({
    onStart=function()
        print("anim2-start")
    end,
    onEnd=function()
        print("anim2-end")
    end,
    onCancel=function()
        print("anim2-cancel")
    end,
    onPause=function()
        print("anim2-pause")
    end,
    onResume=function()
        print("anim2-resume")
    end
})

btn3 = View()
btn3.backgroundColor(0xff0000)
btn3.frame(0, 200, 50, 50)
btn3.callback(function()
--    anim2.with(btn3)
--    anim2.start()
    btn3.startAnimation(anim2)
end)

btn4 = Button()
btn4.xy(50, 200)
btn4.text("stop")
btn4.onClick(function()
--    anim1.with(btn3)
--    anim1.start()
--    anim2.cancel()
    print(btn3.x())
    print(btn3.alpha())
    btn3.stopAnimation()
end)

btn5 = Button()
btn5.xy(150, 200)
btn5.text("pause/resume")
btn5.onClick(function()
    if(anim2.isPaused())then
        anim2.resume()
    else
        anim2.pause()
    end
end)