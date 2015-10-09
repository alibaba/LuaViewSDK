//
//  LVCustomErrorView.m
//  LVSDK
//
//  Created by dongxicheng on 7/20/15.
//  Copyright (c) 2015 dongxicheng. All rights reserved.
//

#import "LVCustomError.h"
#import "LVBaseView.h"
#import "LView.h"

@implementation LVCustomError

- (void) callLuaFuncToReloadData {
    lv_State* L = self.lv_lview.l;
    if( L && self.lv_userData ){
        int num = lv_gettop(L);
        lv_pushUserdata(L, self.lv_userData);
        lv_pushUDataRef(L, USERDATA_KEY_DELEGATE );
        lv_runFunction(L);
        lv_settop(L, num);
    }
}

static Class g_class = nil;
+ (void) setDefaultStyle:(Class) c{
    if( [c isSubclassOfClass:[LVCustomError class]] ){
        g_class = c;
    }
}

static int lvNewErrorView (lv_State *L) {
    if( g_class == nil ) {
        g_class = [LVCustomError class];
    }
    CGRect r = CGRectMake(0, 0, 0, 0);
    if( lv_gettop(L)>=4 ) {
        r = CGRectMake(lv_tonumber(L, 1), lv_tonumber(L, 2), lv_tonumber(L, 3), lv_tonumber(L, 4));
    }
    LVCustomError* errorNotice = [[g_class alloc] initWithFrame:r];
    {
        NEW_USERDATA(userData, LVUserDataView);
        userData->view = CFBridgingRetain(errorNotice);
        errorNotice.lv_userData = userData;
        errorNotice.lv_lview = (__bridge LView *)(L->lView);
        
        lvL_getmetatable(L, META_TABLE_ErrorView );
        lv_setmetatable(L, -2);
    }
    LView* view = (__bridge LView *)(L->lView);
    if( view ){
        [view containerAddSubview:errorNotice];
    }
    return 1; /* new userdatum is already on the stack */
}

+(int) classDefine:(lv_State *)L {
    {
        lv_pushcfunction(L, lvNewErrorView);
        lv_setglobal(L, "UICustomError");
    }
    const struct lvL_reg memberFunctions [] = {
        {NULL, NULL}
    };
    
    lv_createClassMetaTable(L, META_TABLE_ErrorView);
    
    lvL_openlib(L, NULL, [LVBaseView baseMemberFunctions], 0);
    lvL_openlib(L, NULL, memberFunctions, 0);
    return 1;
}

@end
