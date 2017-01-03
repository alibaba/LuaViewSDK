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
#import "LVHeads.h"

@interface LVAlert ()
@property(nonatomic,strong) NSArray* cmdArray;
@property(nonatomic,assign) int argNum;
@property(nonatomic,assign) int functionNum;
@end

@implementation LVAlert{
}

-(void) dealloc{
    lua_State* L = self.lv_lview.l;
    for ( int i=0; i<self.functionNum; i++ ) {
        if( lv_type(L, i) == LUA_TFUNCTION ) {
            NSString* tag = self.cmdArray[i];
            [LVUtil unregistry:L key:tag];
        }
    }
}


-(id) init:(lua_State*) l argNum:(int)num{
    NSString* cancel = getArgs(l, 3, num);
    NSString* ok = getArgs(l, 4, num);
    if( cancel==nil && ok==nil ){
        ok = @"确定";
    }
    self = [super initWithTitle:getArgs(l, 1, num)
                        message:getArgs(l, 2, num) delegate:self
              cancelButtonTitle:cancel
              otherButtonTitles:ok,
                                getArgs(l, 5, num),
                                getArgs(l, 6, num),
                                getArgs(l, 7, num),
                                getArgs(l, 8, num),
                                getArgs(l, 9, num),nil];
    if( self ){
        self.argNum = num;
        self.lv_lview = (__bridge LView *)(l->lView);
        self.delegate = self;
        self.backgroundColor = [UIColor clearColor];
        NSMutableArray* mutArray = [[NSMutableArray alloc] init];
        for( int i=0; i<=9; i++ ){
            NSString* tag = [[NSMutableString alloc] init];
            [mutArray addObject:tag];
        }
        self.cmdArray = mutArray;
    }
    return self;
}

-(void) alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex{
    lua_State* l = self.lv_lview.l;
    if( l ) {
        lv_checkStack32(l);
        lv_pushnumber(l, buttonIndex);
        [LVUtil call:l lightUserData:self.cmdArray[buttonIndex] key1:NULL key2:NULL nargs:1];
        self.lv_lview = nil;
    }
}

static NSString* getArgs(lua_State* L, int index, int max){
    if( index>=1 && index<=max ){
        return lv_paramString(L, index);
    }
    return nil;
}

static int lvNewAlertView (lua_State *L) {
    int num = lv_gettop(L);
    LVAlert* alertView = [[LVAlert alloc] init:L argNum:num];
    if( num>0 ){
        int argID= 0;
        for ( int i=1; i<=num; i++ ) {
            if( lv_type(L, i) == LUA_TFUNCTION ) {
                NSString* tag = alertView.cmdArray[argID++];
                [LVUtil registryValue:L key:tag stack:i];
                alertView.argNum = argID;
            }
        }
        [alertView show];
    }
    return 0;
}


static int toast (lua_State *L) {
    int num = lv_gettop(L);
    if( num>0 ){
        NSString* s = lv_paramString(L, 1);
        if( s ==nil ) {
            s = @"      ";
        }
        // CGSize size = [UIScreen mainScreen].bounds.size;
        [LVToast showWithText:s duration:2];
    }
    return 0;
}


+(int) lvClassDefine:(lua_State *)L globalName:(NSString*) globalName{
    
    {// 自动消失的提示框
        lv_pushcfunction(L, toast);
        lv_setglobal(L, "Toast");
    }

    // 系统Alert提示框
    [LVUtil reg:L clas:self cfunc:lvNewAlertView globalName:globalName defaultName:@"Alert"];
    const struct luaL_Reg memberFunctions [] = {
        {NULL, NULL}
    };
    
    lv_createClassMetaTable(L, META_TABLE_UIAlertView);
    
    //lvL_openlib(L, NULL, [LVBaseView lvMemberFunctions], 0);
    lvL_openlib(L, NULL, memberFunctions, 0);
    return 1;
}



@end
