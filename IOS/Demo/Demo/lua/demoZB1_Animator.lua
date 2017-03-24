-- 简单动画
startBtn = Button();
startBtn:frame(10, 10, 60, 120)

resumeBtn = Button()
resumeBtn:frame(80, 10, 60, 120)

stateLabel = Label()
stateLabel:frame(150, 10, 60, 120)

local function isRunning()
    return translation:isRunning()
end

local function isPaused()
    return translation:isPaused()
end

local function updateControlBtns()
    local running, paused = isRunning(), isPaused()

    startBtn:text(running and "Cancel" or "Start")
    resumeBtn:enabled(running)
    resumeBtn:text(paused and "Resume" or "Pause")
end

translation = Animation():translation(100, 100):duration(3):interpolator(Interpolator.ACCELERATE_DECELERATE):callback({
    onStart = function()
        stateLabel:text("Running")
    end,
    onCancel = function()
        stateLabel:text("Canceled")
        updateControlBtns()
    end,
    onEnd = function()
        stateLabel:text("End")
        updateControlBtns()
    end,
    onPause = function()
        stateLabel:text("Paused")
    end,
    onResume = function()
        stateLabel:text("Running")
    end,
})
scale = Animation():scale(2, 0.5):duration(2):delay(1)

local startAnimations, cancelAnimations, pauseAnimations, resumeAnimations

function startAnimations()
    if animationView then
        animationView:removeFromSuper()
    end
    if isRunning() then
        cancelAnimations()
    end

    animationView = View()
    animationView:frame(50, 300, 100, 100)
    animationView:backgroundColor(0xff0000, 1)
    animationView:callback({
        onClick = function()
            print("scale:", animationView:scale())
            print("translation:", animationView:translation())
        end
    })

    translation:with(animationView):start()
    scale:with(animationView):start()
end

function cancelAnimations()
    translation:cancel()
    scale:cancel()
end

function pauseAnimations()
    if isRunning() and not isPaused() then
        translation:pause()
        scale:pause()
    end
end

function resumeAnimations()
    if isRunning() and isPaused() then
        translation:resume()
        scale:resume()
    end
end

startBtn:callback({
    onClick = function()
        if isRunning() then
            cancelAnimations()
        else
            startAnimations()
        end
        updateControlBtns()
    end
})

resumeBtn:callback({
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
