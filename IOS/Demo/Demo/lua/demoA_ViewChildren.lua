
scrW, scrH = System:screenSize();
window:backgroundColor(0x777777,1);

w = scrW/3;
X0 = scrW/10;
Y0 = scrH/5;



view = View();
view:backgroundColor(0xff);
view:frame(0,0,200,200);
view:children( function ()
	subView = View();
	subView:frame(50,50,100,100)
	subView:backgroundColor(0xff00)
	subView:children( function ()
		subsubView1 = View();
		subsubView1:frame(25,25,50,50)
		subsubView1:backgroundColor(0xff0000)
		subsubView1:children( function ()
			-- body
		end)
		subsubView2 = View();
		subsubView2:frame(0,75+25/2,50,2)
		subsubView2:backgroundColor(0xff0000)
	end)
	subView2 = View();
	subView2:frame(0,175,100,2)
	subView2:backgroundColor(0xff00)

end)

view:onClick( function()
        print("onClick")
        local x,y,w,h = view:frame();
        view:frame(x+1,y+1,w-1,h-1);
end);

view:onLayout( function()
        print("onLayout")
    end)

local c = view:callback();
print(c)


view2 = View();
view2:backgroundColor(0xff00FF);
view2:frame(0,200,200,200);