-- 1
Timer(function()
    print("Timer1 Run")
end).start(1, true).cancel()

--2
timer2 = Timer()
timer2.callback(function() print("Timer2 Run") end)
timer2.start(2, true)

--3
timer3 = Timer(function()
    timer2.cancel()
end)
timer3.start(10.1)