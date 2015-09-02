function setText()
	return "huang"
end

function onClick(view) 
	print("luatest")
end

-- Simple lua script that is executed when the activity start.
--
-- Arguments are the activity and the view.
local activity, view = ...
print('activity', activity, 'view', view)

-- forward declarations of local functions and variables
local animate,render
local chars = {}
local W, H = 600, 800

-- called on key down event
function onKeyDown(keyCode, event)
	print('onKeyDown', keyCode, event)
end

-- called on key up event
function onKeyUp(keyCode, event)
	print('onKeyUp', keyCode, event)
end

-- called on touch event
function onTouchEvent(event)
	-- print('onTouchEvent', event)
	local x1, y1 = event:getX(), event:getY()
	local i,j = math.floor(x1*256/W), math.floor(y1*256/H)
	local u = i + j * 256
	local c = u < 0x80 and string.char(u) or
	          u < 0x800 and string.char(0xc0 + math.floor(u/0x80) % 0x20, 
	                                    u % 0x80 ) or
	          string.char(0xe0 + math.floor(u/0x2000) % 0x8, 
	                      0x80 + math.floor(u/0x80) % 0x80, 
	                      u % 0x80)
	while #chars > 4 do table.remove(chars, 1) end
	table.insert(chars, {
		n=255, 
		x=-4+12*math.random(), 
		y=-8+12*math.random(), 
		text=c})
	return true
end

-- called on trackball event
function onTrackballEvent(event)
	print('onTrackballEvent', event)
	return true
end

function onWindowFocusChanged(hasWindowFocus)
	print('onWindowFocusChanged', hasWindowFocus)
end

function onWindowSystemUiVisibilityChanged(visible)
	print('onWindowSystemUiVisibilityChanged', visible)
end

-- called to draw the lua view
local prev, interval = os.time(), 1/60
local Thread = luajava.bindClass('java.lang.Thread')
function draw(canvas)
	view:invalidate()
	local curr = os.time()
	local diff = curr - prev
	if diff >= interval then
		pcall(animate, canvas)
		local wait = math.floor(1000 * (prev + interval - os.time()))
		if wait > 0 then pcall(Thread.sleep, Thread, wait) end
		prev = os.time()
	end
	pcall(render, canvas)
end

-- the animation step moves the line endpoints
local x1,y1,x2,y2,xi,yi = 160,240,480,240,0,0
local vx1,vy1,vx2,vy2,vxi,vyi = -5,-6,7,8,3,1
local w, h = W, H
local advance = function(x,vx,max,rnd)
	x = x + vx
	if x < 0 then
		return 0, math.random(2,10)
	elseif x > max then
		return max, math.random(-10,-2)
	end
	return x, vx
end
animate = function(canvas)
	x1,y1,x2,y2 = x1+1,y1+1,x2-1,y2-1
	x1,vx1 = advance(x1,vx1,w)
	y1,vy1 = advance(y1,vy1,h)
	x2,vx2 = advance(x2,vx2,w)
	y2,vy2 = advance(y2,vy2,h)
	xi,vxi = advance(xi,vxi,w-100)
	yi,vyi = advance(yi,vyi,h-100)
	while #chars > 0 and chars[1].n <= 1 do
		table.remove(chars, 1)
	end
	for i,c in pairs(chars) do
		c.n = c.n - 1
	end
end

-- classes that we need for rendering
local Color = luajava.bindClass('android.graphics.Color')
--local Paint = luajava.bindClass('android.graphics.Paint')
local Style = luajava.bindClass('android.graphics.Paint$Style')
print('Color, Paint, Style', Color, Paint, Style)

-- colors that we use
local text_color, line_color = Paint.new(), Paint.new()
text_color:setColor(0xffffff33)
text_color:setTextSize(8.0)
line_color:setColor(0xffffaa33)
line_color:setStrokeWidth(1.5)
line_color:setStyle(Style.STROKE)
print('text_color, line_color', text_color, line_color)

-- load the logo image
local istream = view:findResource('logo.gif')
print('istream', istream)
local BitmapFactory = luajava.bindClass('android.graphics.BitmapFactory')
local logo = BitmapFactory:decodeStream(istream)
print('logo', logo)

-- the render step draws the scene
render = function(canvas)
	-- scale the drawing to approximagely 600 x 800
 	W, H = canvas:getWidth(), canvas:getHeight();
 	local scale = (W + H) / (600 + 800)
 	canvas:scale(scale, scale)
 	w, h = W / scale, H / scale

	-- redraw the canvas
	canvas:drawColor(0xff112244)

	-- line
	canvas:drawLine(x1, y1, x2, y2, line_color)
	
	-- text
	canvas:translate(w/2,h/2)
	for i,c in pairs(chars) do
		local s = 200 / (256-c.n)
		canvas:scale(s, s)
		canvas:drawText(c.text, c.x-4, c.y+6, text_color)
		canvas:scale(1/s, 1/s)
	end
	canvas:translate(-w/2,-h/2)

	-- image
	canvas:drawBitmap(logo,xi,yi)
end


