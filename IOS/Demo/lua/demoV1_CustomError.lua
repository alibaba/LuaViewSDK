


customError = CustomPanel( "CustomError", 10,10,200,200 );
customError.backgroundColor(0xff0000);
customError.callback( function(tag)
    print("customError", tag);
end);




customError2 = CustomError(10,230,200,200 );
customError2.backgroundColor(0xff0000);
customError2.callback( function(tag)
    print("customError2", tag);
end)