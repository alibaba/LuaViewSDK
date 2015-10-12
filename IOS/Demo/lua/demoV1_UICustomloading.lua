
customLoading = UICustomLoading();
customLoading.setFrame(10,10,200,200 );
customLoading.backgroundColor(0xff0000);
customLoading.callback = ^(tag){
    print(tag);
}
