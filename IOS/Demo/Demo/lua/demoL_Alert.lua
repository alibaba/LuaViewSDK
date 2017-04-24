
scrW,scrH = System:screenSize();

btnH = scrH / 12;

function createButton( text, x,y,w,h, callback)
    local button = Button();
    button:frame( x, y, w, h);
    button:title( "按钮 " .. text );
    button:backgroundColor(0x777777,1);


    button:callback( 
     function()
        if( callback ) then
            callback();
        end
    end );
    return button;
end

button1 = createButton("关闭1", 0, btnH*1, scrW, btnH, 
    function()
        Alert("标题", "内容",
                    "确定",
                        function(index)
                            print("按钮ID:",index)
                        end
                    );
    end );



button2 = createButton("关闭2", 20, btnH*3, scrW-40, btnH, 
    function()
        Alert("标题", "内容",
                    "确定","取消",
                        function(index)
                            print("按钮ID:",index)
                        end
                    );
    end );



button3 = createButton("关闭3", 0, btnH*5, scrW, btnH, 
    function()
        Alert("标题", nil,
                    "确定","取消",
                        function(index)
                            print("按钮ID:",index)
                        end
                    );
    end );



button4 = createButton("关闭4", 0, btnH*7, scrW, btnH, 
    function()
        Alert(nil, "内容",
                    "确定","取消",
                        function(index)
                            print("按钮ID:",index)
                        end
                    );
    end );





