

print("摇一摇测试脚本");

window.delegate =   {
	shakeBegan=^(){
		print("window.shakeBegan");
	},
	shakeCancelled=^(){
		print("window.shakeCancelled");
	},
	shakeEnded=^(){
		print("window.shakeEnded");
	}
};