//
//  LVCustomView.m
//  LuaViewSDK
//
//  Created by 董希成 on 2016/12/5.
//  Copyright © 2016年 dongxicheng. All rights reserved.
//

#import "LVCustomView.h"
#import "LVBaseView.h"
#import "LView.h"

@implementation LVCustomView

#pragma -mark CustomView
static int lvNewButton (lv_State *L) {
    Class c = [LVUtil upvalueClass:L defaultClass:[LVCustomView class]];
    
    {
        LVCustomView* customView = [[c alloc] init:L];
        {
            NEW_USERDATA(userData, View);
            userData->object = CFBridgingRetain(customView);
            customView.lv_userData = userData;
            
            lvL_getmetatable(L, META_TABLE_CustomView );
            lv_setmetatable(L, -2);
        }
        LView* father = (__bridge LView *)(L->lView);
        if( father ){
            [father containerAddSubview:customView];
        }
    }
    return 1; /* new userdatum is already on the stack */
}


+(int) lvClassDefine:(lv_State *)L globalName:(NSString*) globalName{
    [LVUtil reg:L clas:self cfunc:lvNewButton globalName:globalName defaultName:@"CustomView"];
    
    const struct lvL_reg memberFunctions [] = {
        
        {NULL, NULL}
    };
    
    lv_createClassMetaTable(L,META_TABLE_CustomView);
    
    lvL_openlib(L, NULL, [LVBaseView baseMemberFunctions], 0);
    lvL_openlib(L, NULL, memberFunctions, 0);
    
    return 1;
}

@end
