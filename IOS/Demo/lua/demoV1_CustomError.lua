


customError = UICustomPanel( "UICustomError", 10,10,200,200 );
customError.backgroundColor(0xff0000);
customError.callback = ^(tag){
    print("customError", tag);
}




customError2 = UICustomError(10,230,200,200 );
customError2.backgroundColor(0xff0000);
customError2.callback = ^(tag){
    print("customError2", tag);
}