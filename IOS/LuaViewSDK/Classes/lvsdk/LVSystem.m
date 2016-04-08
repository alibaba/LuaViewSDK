//
//  LVSystem.m
//  LVSDK
//
//  Created by dongxicheng on 1/15/15.
//  Copyright (c) 2015 dongxicheng. All rights reserved.
//

#import "LVSystem.h"
#import "LView.h"
#import "LVPkgManager.h"
#import <AudioToolbox/AudioToolbox.h>
#import "lV.h"
#import "lVauxlib.h"
#import "lVlib.h"
#import "lVstate.h"
#import "lVgc.h"
#import "LVNetworkStatus.h"
#import "LVAnimator.h"

@implementation LVSystem


// // lv 扩展API
static int vmVersion (lv_State *L) {
    lv_pushstring(L, LUAVIEW_VERSION ) ;
    return 1; /* number of results */
}

// // lv 扩展API
static int osVersion (lv_State *L) {
    NSString* v = [[UIDevice currentDevice] systemVersion];
    lv_pushstring(L, v.UTF8String);
    return 1; /* number of results */
}

static int ios (lv_State *L) {
    lv_pushboolean(L, 1);
    return 1;
}

static int android (lv_State *L) {
    lv_pushboolean(L, 0);
    return 1;
}


+(NSString*) netWorkType{
    return [[LVNetworkStatus shareInstance] currentNetworkStatusString];
}

static int netWorkType (lv_State *L) {
    NSString* type = [LVSystem netWorkType];
    lv_pushstring(L, type.UTF8String);
    return 1;
}

// 屏幕常亮
static int keepScreenOn (lv_State *L) {
    if( lv_gettop(L)>0 ){
        BOOL yes = lv_toboolean(L, -1);
        [[UIApplication sharedApplication] setIdleTimerDisabled:yes] ;
    }
    return 0;
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

static int vibrate(lv_State*L){
    AudioServicesPlaySystemSound(kSystemSoundID_Vibrate);
    return 1;
}



static int stringToTable(lv_State*L){
    if( lv_type(L, -1) == LV_TSTRING ) {
        NSString* s = lv_paramString(L, -1);
        if( s ) {
            id obj = [LVUtil stringToObject:s];
            lv_pushNativeObject(L, obj);
            return 1;
        }
    }
    return 0;
}

static int tableToString(lv_State*L){
    if( lv_type(L, -1) == LV_TTABLE ) {
        id obj = lv_luaValueToNativeObject(L,-1);
        NSString* s = [LVUtil objectToString:obj];
        lv_pushstring(L, s.UTF8String);
        return 1;
    }
    return 0;
}

+(int) classDefine:(lv_State *)L {
    {
        // System
        const struct lvL_reg staticFunctions [] = {
            {"screenSize", screenSize},
            {"gc",static_gc},
            {"osVersion", osVersion},
            {"vmVersion", vmVersion},
            {"scale", scale},
            {"platform",platform},
            {"device",device},
            {"ios", ios},
            {"android", android},
            {"network", netWorkType},
            {"keepScreenOn", keepScreenOn},// 保持屏幕常亮接口
            {NULL, NULL}
        };
        lvL_openlib(L, "System", staticFunctions, 0);
    }
    {
        // Json
        const struct lvL_reg fs [] = {
            {"toString", tableToString},
            {"toTable",stringToTable},
            {NULL, NULL}
        };
        lvL_openlib(L, "Json", fs, 0);
    }
    // ----  常量注册 ----
    {
        // Align 常量
        lv_settop(L, 0);
        const struct lvL_reg lib [] = {
            {NULL, NULL}
        };
        lvL_register(L, "Align", lib);
        
        lv_pushnumber(L, LV_ALIGN_LEFT);
        lv_setfield(L, -2, "LEFT");
        
        lv_pushnumber(L, LV_ALIGN_RIGHT);
        lv_setfield(L, -2, "RIGHT");
        
        lv_pushnumber(L, LV_ALIGN_TOP);
        lv_setfield(L, -2, "TOP");
        
        lv_pushnumber(L, LV_ALIGN_BOTTOM);
        lv_setfield(L, -2, "BOTTOM");
        
        lv_pushnumber(L, LV_ALIGN_H_CENTER);
        lv_setfield(L, -2, "H_CENTER");// 水平居中
        lv_pushnumber(L, LV_ALIGN_V_CENTER);// 垂直居中
        lv_setfield(L, -2, "V_CENTER");
        
        lv_pushnumber(L, LV_ALIGN_H_CENTER | LV_ALIGN_V_CENTER);
        lv_setfield(L, -2, "CENTER");// 上下左右都居中
    }
    {
        // TextAlign常量. LEFT RIGHT CENTER
        lv_settop(L, 0);
        const struct lvL_reg lib [] = {
            {NULL, NULL}
        };
        lvL_register(L, "TextAlign", lib);
        
        lv_pushnumber(L, NSTextAlignmentLeft);
        lv_setfield(L, -2, "LEFT");
        
        lv_pushnumber(L, NSTextAlignmentRight);
        lv_setfield(L, -2, "RIGHT");
        
        lv_pushnumber(L, NSTextAlignmentCenter);
        lv_setfield(L, -2, "CENTER");// 上下左右都居中
    }
    {
        // TextAlign常量. LEFT RIGHT CENTER
        lv_settop(L, 0);
        const struct lvL_reg lib [] = {
            {NULL, NULL}
        };
        lvL_register(L, "Ellipsize", lib);// 文本太多 "..." 出现的问题
        
        lv_pushnumber(L, NSLineBreakByTruncatingHead);
        lv_setfield(L, -2, "START");
        
        lv_pushnumber(L, NSLineBreakByTruncatingMiddle);
        lv_setfield(L, -2, "MIDDLE");
        
        lv_pushnumber(L, NSLineBreakByTruncatingTail);
        lv_setfield(L, -2, "END");
        
        lv_pushnumber(L, NSLineBreakByCharWrapping);
        lv_setfield(L, -2, "MARQUEE");
    }
    {
        lv_settop(L, 0);
        const struct lvL_reg lib [] = {
            {NULL, NULL}
        };
        lvL_register(L, "FontStyle", lib);
        
        lv_pushstring(L, "normal");
        lv_setfield(L, -2, "NORMAL");
        
        lv_pushstring(L, "italic");
        lv_setfield(L, -2, "ITALIC");
        
        lv_pushstring(L, "oblique");
        lv_setfield(L, -2, "OBLIQUE");// 上下左右都居中
    }
    {
        lv_settop(L, 0);
        const struct lvL_reg lib [] = {
            {NULL, NULL}
        };
        lvL_register(L, "FontWeight", lib);
        
        lv_pushstring(L, "normal");
        lv_setfield(L, -2, "NORMAL");
        
        lv_pushstring(L, "bold");
        lv_setfield(L, -2, "BOLD");
    }
    {
        lv_settop(L, 0);
        const struct lvL_reg lib [] = {
            {NULL, NULL}
        };
        lvL_register(L, "ScaleType", lib);
        
        //ScaleType.CENTER 按图片的原来size居中显示，当图片长/宽超过View的长/宽，则截取图片的居中部分显示
        lv_pushnumber(L, UIViewContentModeCenter);
        lv_setfield(L, -2, "CENTER");
        
        //ScaleType.CENTER_CROP 按比例扩大图片的size居中显示，使得图片长 (宽)等于或大于View的长(宽)
        lv_pushnumber(L, UIViewContentModeScaleAspectFill);
        lv_setfield(L, -2, "CENTER_CROP");
        
        //ScaleType.CENTER_INSIDE 将图片的内容完整居中显示，通过按比例缩小或原来的size使得图片长(宽)等于或小于View的长(宽)
        lv_pushnumber(L, UIViewContentModeScaleAspectFit);
        lv_setfield(L, -2, "CENTER_INSIDE");
        
        //ScaleType.FIT_CENTER 把图片按比例扩大(缩小)到View的宽度，居中显示
        lv_pushnumber(L, UIViewContentModeScaleAspectFill);
        lv_setfield(L, -2, "FIT_CENTER");
        
        //ScaleType.FIT_END 把图片按比例扩大(缩小)到View的宽度，显示在View的下部分位置
        lv_pushnumber(L, UIViewContentModeScaleAspectFill);
        lv_setfield(L, -2, "FIT_END");
        
        //ScaleType.FIT_START 把图片按比例扩大(缩小)到View的宽度，显示在View的上部分位置
        lv_pushnumber(L, UIViewContentModeScaleAspectFill);
        lv_setfield(L, -2, "FIT_START");
        
        //ScaleType.FIT_XY 把图片按照指定的大小在View中显示
        lv_pushnumber(L, UIViewContentModeScaleToFill);
        lv_setfield(L, -2, "FIT_XY");
        
        //ScaleType.MATRIX 用matrix来绘制
        lv_pushnumber(L, UIViewContentModeScaleAspectFill);
        lv_setfield(L, -2, "MATRIX");
    }
    {
        lv_settop(L, 0);
        const struct lvL_reg lib [] = {
            {NULL, NULL}
        };
        lvL_register(L, "Interpolator", lib);
        
        lv_pushnumber(L, LVLinearInterpolator);
        lv_setfield(L, -2, "LINEAR");
        
        lv_pushnumber(L, LVAccelerateInterpolator);
        lv_setfield(L, -2, "ACCELERATE");
        
        lv_pushnumber(L, LVDecelerateInterpolator);
        lv_setfield(L, -2, "DECELERATE");
        
        lv_pushnumber(L, LVAccelerateDecelerateInterpolator);
        lv_setfield(L, -2, "ACCELERATE_DECELERATE");
        
        lv_pushnumber(L, LVAnticipateInterpolator);
        lv_setfield(L, -2, "ANTICIPATE");
        
        lv_pushnumber(L, LVOvershootInterpolator);
        lv_setfield(L, -2, "OVERSHOOT");
        
        lv_pushnumber(L, LVAnticipateOvershootInterpolator);
        lv_setfield(L, -2, "ANTICIPATE_OVERSHOOT");
    }
    {
        // 震动
        lv_pushcfunction(L, vibrate);
        lv_setglobal(L, "Vibrate");
    }
    return 0;
}

@end
