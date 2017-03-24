sw, sh = System:screenSize()

bg = Label()
bg:text("我是背景，重复进入页面，尝试不同状态。")
bg:frame(0, 0, sw, sh)
bg:callback(function()
    Toast("bg clicked")
end)
bg:effects(ViewEffect.CLICK)


view = CustomView()
view:frame(0, 0, sw,sh)
view:backgroundColor(0xff0000, 0.5)

lastX = nil
lastY = nil

x = 0
y = 0
w = 4
dx = 0
dy = 0

isTouchable = math.random() > 0.5

bg:text(bg:text() .. (isTouchable and " 不可点击" or " 可以点击"))

view:onTouch(function(event)
    print(event.id, event.pointer, "action:", event.action, event.x, event.y)
    dx = lastX and event.x - lastX or 0
    dy = lastY and event.y - lastY or 0


    x = x + dx
    y = y + dy

    if(x < 0) then
        x = 0
    end

    if(y < 0) then
        y = 0
    end

    if(x > sw) then
        x = sw
    end

    if (y > sh) then
        y = sh
    end

    lastX = event.action == TouchEvent.MOVE and event.x or nil
    lastY = event.action == TouchEvent.MOVE and event.y or nil

    view:invalidate()

    return isTouchable;
end)

rect = {}

count = 20

for i = 0, count, 1 do
    rect[i + 1] = {}
    rect[i + 1][1] = x
    rect[i + 1][2] = y
    rect[i + 1][3] = w
    rect[i + 1][4] = w

    x = x + 20

    w = 3 + math.random(10)
end

view:onDraw(function(canvas)
    print(x, y, w, h)

    for i = 0, count, 1 do
        canvas:drawRect(rect[i+1][1], rect[i+1][2], rect[i+1][3], rect[i+1][4])
    end

    for i = 0, count, 1 do
        rect[i+1][2] = (rect[i+1][2] + math.random()) * 1.005 - dy

        if(rect[i+1][2] + rect[i+1][4] > 500) then
            rect[i+1][2] = 500 - rect[i + 1][4]
        end
    end

    canvas:drawLine(0, 500 - dy, sw, 500-dy)

end)

timer = Timer();
timer:callback(function()
    view:invalidate()
end):start(0.01, true)