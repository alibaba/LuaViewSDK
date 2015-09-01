--[[
STEP = 10
SCR_W, SCR_H = screenSize()
BOX_W, BOX_H = 40, 40

man = button.new( 160, 100, BOX_W, BOX_H, "", "box",   nil );
box = button.new( 160, 160, BOX_W, BOX_H, "", "box",   nil );

function boxMoveX( )
	manX, manY = man:center();
	boxX, boxY = box:center();
	dx = boxX-manX;
	dy = boxY-manY;
	if math.abs(dx)<BOX_W and math.abs(dy)<BOX_H then
		if dx>0 then
			box:setCenter( manX + BOX_W, boxY);
		else
			box:setCenter( manX - BOX_W, boxY);
		end
	end
end

function boxMoveY( )
	manX,manY = man:center();
	boxX,boxY = box:center();
	dx = boxX-manX;
	dy = boxY-manY;
	if math.abs(dx)<BOX_W and math.abs(dy)<BOX_H then
		if  dy>0 then
			box:setCenter( boxX, manY+BOX_H);
		else
			box:setCenter( boxX, manY-BOX_H);
		end
	end
end

function manMove( dx, dy )
	x,y = man:center();
	man:setCenter( x+dx, y+dy );
end

up = button.new(SCR_W/2-35,0,     70,70,"上",nil, function ( )
	manMove( 0, -STEP );
	boxMoveY();
end);

down = button.new(SCR_W/2-35,SCR_H-70,70,70,"下",nil, function ( )
	manMove( 0, STEP );
	boxMoveY();
end);

left = button.new(0,SCR_H/2,         70,70,"左",nil, function ( )
	manMove(-STEP, 0 );
	boxMoveX();
end);

right = button.new(SCR_W-70,SCR_H/2,   70,70,"右",nil, function ( )
	manMove( STEP, 0 );
	boxMoveX();
end);

--]]
