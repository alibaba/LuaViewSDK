

print("摇一摇测试脚本");

window:callback{
	ShakeBegin =  function(){
		window:backgroundColor(0xff00ff);
		print("window.shakeBegin");
	},
	ShakeCanceled = function(){
		window:backgroundColor(0xff);
		print("window.shakeCancelled");
	},
	ShakeEnded = function(){
		window:backgroundColor(0xffFFff);
		print("window.shakeEnded");
	}
}

label  = Label();
label:text ( "  摇一摇变色  " );
label:frame(0,0,320,60);