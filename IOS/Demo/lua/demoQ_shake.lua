

print("摇一摇测试脚本");

window.delegate =   {
	shakeBegan=^(){
		window.backgroundColor(0xff00ff);
		print("window.shakeBegan");
	},
	shakeCancelled=^(){
		window.backgroundColor(0xff);
		print("window.shakeCancelled");
	},
	shakeEnded=^(){
		window.backgroundColor(0xffFFff);
		print("window.shakeEnded");
	}
};

label  = Label();
label.text ( "  摇一摇变色  " );
label.frame(0,0,320,60);