local candle = require("candle")

function addSwitch(instance)
    instance.onOff = false

    instance.switch = Button()
    instance.switch.title("开")
    instance.switch.backgroundColor(0xff0000,1)
    instance.switch.frame(
        (instance.view.width() - instance.body.width()) / 2,
        120,
        instance.body.width(),
        40
    )
    instance.switch.callback( function()
        if( instance.onOff ) then
            instance.onOff = false
            instance.switch.title("开")
            instance.stop()
        else
            instance.onOff = true
            instance.switch.title("关")
            instance.start()
        end
    end)

    instance.view.addView(instance.switch)

    return instance
end

lazhu1 = addSwitch(candle.new(50,200))
lazhu2 = addSwitch(candle.new(160,200))
lazhu3 = addSwitch(candle.new(260,200))

dragGesture = PanGesture(
    function( g )
        local state = g.state()
        if( state == GestureState.BEGIN ) then
            gestureX, gestureY = g.location()
        elseif( state == GestureState.CHANGED ) then
            local x, y = g.location()
            local dx = x- gestureX
            local dy = y- gestureY
            gestureX = x
            gestureY = y
            lazhu2.move(dx,dy)
        end
    end
)

window.addGesture(dragGesture)

