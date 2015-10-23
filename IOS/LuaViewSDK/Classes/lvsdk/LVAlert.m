//
//  LVAlertView.m
//  LVSDK
//
//  Created by dongxicheng on 1/14/15.
//  Copyright (c) 2015 dongxicheng. All rights reserved.
//

#import "LVAlert.h"
#import "LVBaseView.h"
#import "LView.h"
#import "LVToast.h"


@implementation LVAlert


-(id) init:(lv_State*) l argNum:(int)num{
    self = [super initWithTitle:getArgs(l, 1, num)
                        message:getArgs(l, 2, num) delegate:self
              cancelButtonTitle:getArgs(l, 3, num)
              otherButtonTitles:getArgs(l, 4, num),
                                getArgs(l, 5, num),
                                getArgs(l, 6, num),
                                getArgs(l, 7, num),
                                getArgs(l, 8, num),
                                getArgs(l, 9, num),nil];
    if( self ){
        self.lv_lview = (__bridge LView *)(l->lView);
        self.delegate = self;
        self.backgroundColor = [UIColor clearColor];
    }
    return self;
}

-(void) alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex{
    lv_State* l = self.lv_lview.l;
    if( l ) {
        lv_checkStack32(l);
        lv_pushnumber(l, buttonIndex+1);//传参数
        [LVUtil call:l lightUserData:self key:STR_CALLBACK nargs:1];
        self.lv_lview = nil;
    }
}

static NSString* getArgs(lv_State* L, int index, int max){
    if( index>=1 && index<max ){
        return lv_paramString(L, index);
    }
    return nil;
}

static int lvNewAlertView (lv_State *L) {
    int num = lv_gettop(L);
    LVAlert* alertView = [[LVAlert alloc] init:L argNum:num];
    if( num>0 ){
        if( lv_type(L, -1) == LV_TFUNCTION ) {
            [LVUtil registryValue:L key:alertView stack:-1];
        }
        [alertView show];
    }
    return 0;
}


static int toast (lv_State *L) {
    int num = lv_gettop(L);
    if( num>0 ){
        NSString* s = lv_paramString(L, 1);
        if( s ==nil ) {
            s = @"      ";
        }
        CGSize size = [UIScreen mainScreen].bounds.size;
        [LVToast showWithText:s bottomOffset:size.height/4 duration:2];
    }
    return 0;
}


+(int) classDefine: (lv_State *)L {
    
    {// 自动消失的提示框
        lv_pushcfunction(L, toast);
        lv_setglobal(L, "Toast");
    }

    {// 系统Alert提示框
        lv_pushcfunction(L, lvNewAlertView);
        lv_setglobal(L, "Alert");
    }
    const struct lvL_reg memberFunctions [] = {
        {NULL, NULL}
    };
    
    lv_createClassMetaTable(L, META_TABLE_UIAlertView);
    
    //lvL_openlib(L, NULL, [LVBaseView lvMemberFunctions], 0);
    lvL_openlib(L, NULL, memberFunctions, 0);
    return 1;
}



@end
