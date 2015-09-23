print("System.ios = " , System.ios )
print("System.android = ", System.android )
print("System.vmVersion = ", System.vmVersion)
print("System.osVersion = ", System.osVersion)	--获取手机操作系统版本号
print("System.platform = ", System.platform )	--获取平台信息
print("System.scale = ", System.scale )	--获取屏幕分辨率
print("System.device = ", System.device )	--获取设备信息
print("System.screenSize() = ", System.screenSize() );
System.gc ()


button = UIButton();
button.setTitle("测试");
button.setFrame(30,50,100,100);
button.backgroundColor(0xff);
button.delegate =^(){
    button.setAnchorPoint(0,0);
};