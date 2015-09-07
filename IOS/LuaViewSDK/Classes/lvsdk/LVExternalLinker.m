//
//  ExternalLinker.m
//  LVSDK
//
//  Created by dongxicheng on 4/23/15.
//  Copyright (c) 2015 dongxicheng. All rights reserved.
//
#import <objc/message.h>
#import "LVExternalLinker.h"
#import "LVNativeObjBox.h"

@implementation LVExternalLinker

//static int __index (lv_State *L) {
//    if( lv_type(L, 2)==LV_TSTRING ){
//        NSString* key = lv_paramString(L, 2);
//        NS Log(@"%@",key);
//    }
//    return 0;
//}

+(int) classDefine:(lv_State *)L {
    //    // 创建_G的metatable
    //    [LVUtil classNewMetaTable:L name:META_TABLE_Global];
    //    const struct lvL_reg functions [] = {
    //        {"__index", __index },
    //        
    //        {NULL, NULL}
    //    };
    //    lvL_openlib(L, NULL, functions, 0);
    //    
    //    // 设置_G的metatable
    //    lv_getglobal(L, "_G");
    //    lvL_getmetatable(L, META_TABLE_Global );
    //    lv_setmetatable(L, -2);
    
    //
    [LVNativeObjBox classDefine:L];
    return 0;
}




@end
