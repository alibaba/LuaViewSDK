
w = System.screenSize();

function createButton( text, x,y,w,h, callback)
    local button = UIButton();
    button.setFrame( x, y, w, h);
    button.setTitle( "按钮 " .. text );
    button.setBackgroundColor(0x777777,1);


    button.delegate =  ^(){
        if( callback ) {
            callback();
        }
    };
    return button;
end



button1 = createButton("关闭1", 0, 50, w, 40, ^(){
    UIAlertView("标题", "内容",
                "确定",
                    ^(index){
                        print("按钮ID:",index)
                    }
                );
} );



button2 = createButton("关闭2", 0, 100, w, 40, ^(){
    UIAlertView("标题", "内容",
                "确定","取消",
                    ^(index){
                        print("按钮ID:",index)
                    }
                );
} );



button3 = createButton("关闭3", 0, 150, w, 40, ^(){
    UIAlertView("标题", nil,
                "确定","取消",
                    ^(index){
                        print("按钮ID:",index)
                    }
                );
} );



button4 = createButton("关闭4", 0, 200, w, 40, ^(){
    UIAlertView(nil, "内容",
                "确定","取消",
                    ^(index){
                        print("按钮ID:",index)
                    }
                );
} );





