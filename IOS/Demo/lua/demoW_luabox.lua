
data1 = Data("12345");
data2 = Data("87897");
-- data1.append(data2);


TYPE_CHAR = string:byte("c");

function createObject( )
	local obj = {
		__obj = true,
	    test = "测试结果",
	    name = "脚本名字"
	};

    function obj.testPointer( s )
    	ViewController.printPointer(s);
    end
    function obj.testRect( rect )
    	rect.set(2,789);
    	ViewController.testRect(rect);
    end
    function obj.testStruct( struct )
    	struct[1] = 123; 
    	print(  struct.get(2,TYPE_CHAR)  );
    	struct.set( 2, string:byte("A"), TYPE_CHAR );
    	ViewController.testStruct( struct );
    end
	return obj;
end





ViewController.setLuaBox( createObject() );