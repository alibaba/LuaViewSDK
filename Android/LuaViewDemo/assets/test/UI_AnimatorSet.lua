-- Created by LuaView.
-- Copyright (c) 2017, Alibaba Group. All rights reserved.
--
-- This source code is licensed under the MIT.
-- For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.

-- 简单动画
startBtn = Button();
startBtn.frame(10, 10, 80, 50)

resumeBtn = Button()
resumeBtn.frame(100, 10, 80, 50)

stateLabel = Label()
stateLabel.frame(200, 10, 100, 50)

local function isRunning()
    return translation.isRunning()
end

local function isPaused()
    return translation.isPaused()
end

local function updateControlBtns()
    local running, paused = isRunning(), isPaused()
    print("Running=", running, " Paused=", paused)

    startBtn.text(running and "Cancel" or "Start")
    resumeBtn.enabled(running)
    resumeBtn.text(paused and "Resume" or "Pause")
end

translation = AnimationSet("Attention.Bounce").duration(1).interpolator(Interpolator.ACCELERATE_DECELERATE).callback({
    onStart = function()
        stateLabel.text("Running")
    end,
    onCancel = function()
        stateLabel.text("Canceled")
        updateControlBtns()
    end,
    onEnd = function()
        stateLabel.text("End")
        updateControlBtns()
    end,
    onPause = function()
        stateLabel.text("Paused")
    end,
    onResume = function()
        stateLabel.text("Running")
    end,
})
scale = AnimationSet("Fade.In").duration(1).delay(1)

local startAnimations, cancelAnimations, pauseAnimations, resumeAnimations

function startAnimations()
    if animationView then
        animationView.removeFromSuper()
    end
    if isRunning() then
        cancelAnimations()
    end

    animationView = View()
    animationView.frame(50, 300, 100, 100)
    animationView.backgroundColor(0xff0000, 1)
    child = Label()
    child.frame(10, 10, 80, 80)
    child.backgroundColor(0x00ff00)
    child.text("child view")
    animationView.addView(child)
    animationView.callback({
        onClick = function()
            print("scale:", animationView.scale())
            print("translation:", animationView.translation())
        end
    })

    translation.with(animationView).start()
    scale.with(animationView).start()
end

function cancelAnimations()
    translation.cancel()
    scale.cancel()
end

function pauseAnimations()
    if isRunning() and not isPaused() then
        translation.pause()
        scale.pause()
    end
end

function resumeAnimations()
    if isRunning() and isPaused() then
        translation.resume()
        scale.resume()
    end
end

startBtn.callback({
    onClick = function()
        if isRunning() then
            cancelAnimations()
        else
            startAnimations()
        end
        updateControlBtns()
    end
})

resumeBtn.callback({
    onClick = function()
        if isPaused() then
            resumeAnimations()
        else
            pauseAnimations()
        end
        updateControlBtns()
    end
})

updateControlBtns()
