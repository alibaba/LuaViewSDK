
w,h = System:screenSize();--获取屏幕尺寸

gesture = Gesture(
    function( event )
        print(event.id, event.pointer, event.action, event.x, event.y)
	end
);

window:addGesture(gesture);

