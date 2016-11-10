//
//  LVCustomErrorView.m
//  LVSDK
//
//  Created by dongxicheng on 7/20/15.
//  Copyright (c) 2015 dongxicheng. All rights reserved.
//

#import "LVCustomPanel.h"
#import "LVBaseView.h"
#import "LView.h"
#import "lV.h"
#import "lVauxlib.h"
#import "lVlib.h"
#import "lVstate.h"
#import "lVgc.h"

@implementation LVCustomPanel

- (void) callLuaWithArgument:(NSString*) info {
    [self callLuaWithArguments:@[ (info?info:@"") ]];
}

- (void) callLuaWithArguments:(NSArray*) args{
    // 外部回调脚本一定要在主线程调用
    dispatch_block_t f = ^(){
        lv_State* L = self.lv_lview.l;
        if( L && self.lv_userData ){
            lv_checkstack(L,32);
            int num = lv_gettop(L);
            for( int i=0; i<args.count; i++ ) {
                lv_pushNativeObject(L, args[i]);
            }
            lv_pushUserdata(L, self.lv_userData);
            lv_pushUDataRef(L, USERDATA_KEY_DELEGATE );
            
            if( lv_type(L, -1)==LV_TTABLE ) {
                lv_getfield(L, -1, STR_CALLBACK);
                if( lv_type(L, -1)==LV_TNIL ) {
                    lv_remove(L, -1);
                    lv_getfield(L, -1, STR_ON_CLICK);
                }
                lv_remove(L, -2);
            }
            lv_runFunctionWithArgs(L, (int)args.count, 0);
            lv_settop(L, num);
        }
    };
    if ([NSThread isMainThread]) {
        f();
    } else {
        dispatch_sync(dispatch_get_main_queue(), f);
    }
}

-(void) layoutSubviews{
    [self lv_runCallBack:STR_ON_LAYOUT];
}

-(id)lv_getNativeView{
    NSArray* subviews = self.subviews;
    return subviews.firstObject;
}

static int lvNewCustomPanelView (lv_State *L) {
    Class c = [LVUtil upvalueClass:L defaultClass:[LVCustomPanel class]];
    CGRect r = CGRectMake(0, 0, 0, 0);
    if( lv_gettop(L)>=4 ) {
        r = CGRectMake(lv_tonumber(L, 1), lv_tonumber(L, 2), lv_tonumber(L, 3), lv_tonumber(L, 4));
    }
    LVCustomPanel* errorNotice = [[c alloc] initWithFrame:r];
    {
        NEW_USERDATA(userData, View);
        userData->object = CFBridgingRetain(errorNotice);
        errorNotice.lv_userData = userData;
        errorNotice.lv_lview = (__bridge LView *)(L->lView);
        
        lvL_getmetatable(L, META_TABLE_UICustomPanel );
        lv_setmetatable(L, -2);
    }
    LView* view = (__bridge LView *)(L->lView);
    if( view ){
        [view containerAddSubview:errorNotice];
    }
    return 1; /* new userdatum is already on the stack */
}

+(int) lvClassDefine:(lv_State *)L globalName:(NSString*) globalName{
    [LVUtil reg:L c:self cfunc:lvNewCustomPanelView globalName:globalName defaultName:@"CustomPanel"];
    
    const struct lvL_reg memberFunctions [] = {
        {NULL, NULL}
    };
    
    lv_createClassMetaTable(L, META_TABLE_UICustomPanel);
    
    lvL_openlib(L, NULL, [LVBaseView baseMemberFunctions], 0);
    lvL_openlib(L, NULL, memberFunctions, 0);
    return 1;
}

@end
