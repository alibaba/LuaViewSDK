//
//  LVSystem.m
//  LVSDK
//
//  Created by dongxicheng on 1/15/15.
//  Copyright (c) 2015 dongxicheng. All rights reserved.
//

#import "LVSystem.h"
#import "LView.h"

@implementation LVSystem


// // lv 扩展API
static int lv_version (lv_State *L) {
    lv_pushstring(L, "LV 1.0.0") ;
    return 1; /* number of results */
}

// // lv 扩展API
static int osVersion (lv_State *L) {
    NSString* v = [[UIDevice currentDevice] systemVersion];
    lv_pushstring(L, v.UTF8String);
    return 1; /* number of results */
}
static int scale (lv_State *L) {
    CGFloat s = [UIScreen mainScreen].scale;
    lv_pushnumber( L, s);
    return 1; /* number of results */
}


// // lv 扩展API
static int platform (lv_State *L) {
    NSString* name = [[UIDevice currentDevice] systemName];
    NSString* version = [[UIDevice currentDevice] systemVersion];
    NSString* buf = [NSString stringWithFormat:@"%@;%@",name,version];
    lv_pushstring(L, [buf UTF8String] ) ;
    return 1; /* number of results */
}

static int device (lv_State *L) {
    NSString* name = [[UIDevice currentDevice] localizedModel];
    NSString* version = [[UIDevice currentDevice] model];
    NSString* buf = [NSString stringWithFormat:@"%@;%@",name,version];
    lv_pushstring(L, [buf UTF8String] ) ;
    return 1; /* number of results */
}

// // lv 扩展API
static int screenSize (lv_State *L) {
    CGSize s = [UIScreen mainScreen].bounds.size;
    lv_pushnumber(L, s.width );
    lv_pushnumber(L, s.height );
    return 2; /* number of results */
}

//
static int static_gc (lv_State *L) {
    lv_gc(L, 2, 0);
    return 0;
}

+(int) classDefine:(lv_State *)l {
    const struct lvL_reg staticFunctions [] = {
        {"osVersion", osVersion},
        {"scale", scale},
        {"version", lv_version},
        {"platform",platform},
        {"device",device},
        {"screenSize", screenSize},
        {"gc",static_gc},
        {NULL, NULL}
    };
    lvL_openlib(l, "System", staticFunctions, 0);
    return 0;
}

@end
