
print("测试调试器")
function myfunction ()
	print("测试调试器1");
	print("测试调试器2");
	print("测试调试器3");
    return 10
end

myfunction()

print("初始化窗口大小, 默认窗口大小是(0,0)");
w,h = System.screenSize();
window.setFrame(0, 0, w, h);
window.setBackgroundColor(0,0);
window.setUserInteractionEnabled(true);
window.setBackgroundColor(0xff0000,1);



print("创建按钮");
button = UIButton();
button.setFrame( 0, h/2, w, 40);
button.setTitle("关闭");
button.setBackgroundColor(0xff,1);


button.delegate =  ^(){
    UIAlertView("标题", "内容",
                "确定","取消",
	                ^(index){
                        print("按钮ID:",index)
                        if index==0 then
                            window.release();
                        end
	                }
                );
};

print("结束");



function cellInit(cell , identifier)
    if( cell.imageView==nil ) then
        cell.imageView = UIImageView();
        print("viewInitWithIdentifier ",cell, identifier);
    end
end

function cellLayout(cell , identifier, model)
    --设置美女背景图片
    cell.imageView.setImage("http://img4.duitang.com/uploads/item/201306/25/20130625045508_sairr.thumb.600_0.jpeg");
    cell.imageView.setFrame(0,0,w,h);
    print("viewLayoutWithIdentifierAndJson ",cell, identifier, model);
end



