/**
  * Created by LuaView.
  * Copyright (c) 2017, Alibaba Group. All rights reserved.
  *
  * This source code is licensed under the MIT.
  * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
  */

#import "LVSystem.h"
#import "LView.h"
#import "LVPkgManager.h"
#import <AudioToolbox/AudioToolbox.h>
#import "LVHeads.h"
#import "LVNetworkStatus.h"
#import "LVAnimator.h"

@implementation LVSystem


// lv 扩展API
static int vmVersion (lua_State *L) {
    lua_pushstring(L, LUAVIEW_VERSION ) ;
    return 1; /* number of results */
}

// lv 扩展API
static int sdkVersion (lua_State *L) {
    lua_pushstring(L, LUAVIEW_SDK_VERSION ) ;
    return 1; /* number of results */
}

// lv 扩展API
static int osVersion (lua_State *L) {
    NSString* v = [[UIDevice currentDevice] systemVersion];
    lua_pushstring(L, v.UTF8String);
    return 1; /* number of results */
}

static int ios (lua_State *L) {
    lua_pushboolean(L, 1);
    return 1;
}

static int android (lua_State *L) {
    lua_pushboolean(L, 0);
    return 1;
}


+(NSString*) netWorkType{
    return [[LVNetworkStatus shareInstance] currentNetworkStatusString];
}

static int netWorkType (lua_State *L) {
    NSString* type = [LVSystem netWorkType];
    lua_pushstring(L, type.UTF8String);
    return 1;
}

static int layerMode (lua_State *L) {
    if( lua_gettop(L)>0 ){
        BOOL yes = lua_toboolean(L, -1);
        LuaViewCore* luaview = LV_LUASTATE_VIEW(L);
        luaview.closeLayerMode = !yes;
    }
    return 0;
}

// 屏幕常亮
static int keepScreenOn (lua_State *L) {
    if( lua_gettop(L)>0 ){
        BOOL yes = lua_toboolean(L, -1);
        [[UIApplication sharedApplication] setIdleTimerDisabled:yes] ;
    }
    return 0;
}

static int scale (lua_State *L) {
    CGFloat s = [UIScreen mainScreen].scale;
    lua_pushnumber( L, s);
    return 1; /* number of results */
}


// lv 扩展API
static int platform (lua_State *L) {
    NSString* name = [[UIDevice currentDevice] systemName];
    NSString* version = [[UIDevice currentDevice] systemVersion];
    NSString* buf = [NSString stringWithFormat:@"%@;%@",name,version];
    lua_pushstring(L, [buf UTF8String] ) ;
    return 1; /* number of results */
}

static int device (lua_State *L) {
    NSString* name = [[UIDevice currentDevice] localizedModel];
    NSString* version = [[UIDevice currentDevice] model];
    NSString* buf = [NSString stringWithFormat:@"%@;%@",name,version];
    lua_pushstring(L, [buf UTF8String] ) ;
    return 1; /* number of results */
}

// lv 扩展API
static int screenSize (lua_State *L) {
    CGSize s = [UIScreen mainScreen].bounds.size;
    lua_pushnumber(L, s.width );
    lua_pushnumber(L, s.height );
    return 2; /* number of results */
}

static int static_gc (lua_State *L) {
    lua_gc(L, 2, 0);
    return 0;
}

static int vibrate(lua_State*L){
    AudioServicesPlaySystemSound(kSystemSoundID_Vibrate);
    return 1;
}

static int lvClassLoader(lua_State*L){
    NSString* s = lv_paramString(L, -1);
    id obj = NSClassFromString(s);
    lv_pushNativeObject(L, obj);
    return 1;
}

static int stringToTable(lua_State*L){
    if( lua_type(L, -1) == LUA_TSTRING ) {
        NSString* s = lv_paramString(L, -1);
        if( s ) {
            id obj = [LVUtil stringToObject:s];
            lv_pushNativeObject(L, obj);
            return 1;
        }
    }
    return 0;
}

static int tableToString(lua_State*L){
    if( lua_type(L, -1) == LUA_TTABLE ) {
        id obj = lv_luaValueToNativeObject(L,-1);
        NSString* s = [LVUtil objectToString:obj];
        lua_pushstring(L, s.UTF8String);
        return 1;
    }
    return 0;
}

+(int) lvClassDefine:(lua_State *)L globalName:(NSString*) globalName{
    {
        // System API
        const struct luaL_Reg staticFunctions [] = {
            {"screenSize", screenSize},
            {"gc",static_gc},
            {"osVersion", osVersion},
            {"vmVersion", vmVersion},
            {"sdkVersion", sdkVersion},
            {"scale", scale},
            {"platform",platform},
            {"device",device},
            {"ios", ios},
            {"android", android},
            {"network", netWorkType},
            {"keepScreenOn", keepScreenOn},// 保持屏幕常亮接口
            {"layerMode", layerMode},// (for IOS) 是否开启layer模式
            {NULL, NULL}
        };
        luaL_openlib(L, "System", staticFunctions, 0);
    }
    {
        // Json Table相互转换
        const struct luaL_Reg fs [] = {
            {"toString", tableToString},
            {"toJson", tableToString},
            {"toTable",stringToTable},
            {NULL, NULL}
        };
        luaL_openlib(L, "Json", fs, 0);
    }
    // ----  常量注册 ----
    {
        // Align 常量
        lua_settop(L, 0);
        NSDictionary* v = nil;
        v = @{
              @"LEFT":    @(LV_ALIGN_LEFT),
              @"RIGHT":   @(LV_ALIGN_RIGHT),
              @"TOP":     @(LV_ALIGN_TOP),
              @"BOTTOM":  @(LV_ALIGN_BOTTOM),
              @"H_CENTER":@(LV_ALIGN_H_CENTER),// 水平居中
              @"V_CENTER":@(LV_ALIGN_V_CENTER),// 垂直居中
              @"CENTER":  @(LV_ALIGN_H_CENTER|LV_ALIGN_V_CENTER),// 上下左右都居中
              };
        [LVUtil defineGlobal:@"Align" value:v L:L];
    }
    {
        lua_settop(L, 0);
        NSDictionary* v = nil;
        v = @{
              @"LEFT":@(NSTextAlignmentLeft),
              @"RIGHT":@(NSTextAlignmentRight),
              @"CENTER":@(NSTextAlignmentCenter),// 上下左右都居中
              };
        [LVUtil defineGlobal:@"TextAlign" value:v L:L];
    }
    {
        //文本太多 "..." 出现的问题
        lua_settop(L, 0);
        NSDictionary* v = nil;
        v = @{
              @"START":@(NSLineBreakByTruncatingHead),
              @"MIDDLE":@(NSLineBreakByTruncatingMiddle),
              @"END":@(NSLineBreakByTruncatingTail),
              @"MARQUEE":@(NSLineBreakByCharWrapping),
              };
        [LVUtil defineGlobal:@"Ellipsize" value:v L:L];
    }
    {
        //字体Style
        lua_settop(L, 0);
        NSDictionary* v = nil;
        v = @{
              @"NORMAL":@"normal",//正常
              @"ITALIC":@"italic",//斜体
              @"OBLIQUE":@"oblique",//倾斜 //__deprecated_msg("")
              // BOLD IOS 不支持
              // 
              };
        [LVUtil defineGlobal:@"FontStyle" value:v L:L];
    }
    {
        //字体Weight（粗体、正常）
        lua_settop(L, 0);
        NSDictionary* v = nil;
        v = @{
              @"NORMAL":@"normal",
              @"BOLD":@"bold",
              };
        [LVUtil defineGlobal:@"FontWeight" value:v L:L];
    }
    {
        //图片缩放常量
        lua_settop(L, 0);
        NSDictionary* v = nil;
        v = @{
              @"CENTER":@(UIViewContentModeCenter),//按图片的原来size居中显示，当图片长/宽超过View的长/宽，则截取图片的居中部分显示
              @"CENTER_CROP":@(UIViewContentModeScaleAspectFill),//将图片等比居中显示，完全覆盖view，尽可能小；
              @"CENTER_INSIDE":@(UIViewContentModeScaleAspectFit),//将图片的内容完整居中显示，尽可能的大
              @"FIT_CENTER":@(UIViewContentModeScaleAspectFit),//将图片的内容完整居中显示，尽可能的大
              @"FIT_END":@(UIViewContentModeScaleAspectFill),//把图片按比例扩大(缩小)到View的宽度，显示在View的下部分位置
              @"FIT_START":@(UIViewContentModeScaleAspectFill),//把图片按比例扩大(缩小)到View的宽度，显示在View的上部分位置
              @"FIT_XY":@(UIViewContentModeScaleToFill),//把图片按照指定的大小在View中显示
              @"MATRIX":@(UIViewContentModeScaleAspectFill),//用matrix来绘制
              };
        [LVUtil defineGlobal:@"ScaleType" value:v L:L];
    }
    {
        lua_settop(L, 0);
        NSDictionary* v = nil;
        v = @{
              @"LINEAR" : @(LVLinearInterpolator),
              @"ACCELERATE" : @(LVAccelerateInterpolator),
              @"DECELERATE" : @(LVDecelerateInterpolator),
              @"ACCELERATE_DECELERATE" : @(LVAccelerateDecelerateInterpolator),
              @"ANTICIPATE": @(LVAnticipateInterpolator),
              @"OVERSHOOT": @(LVOvershootInterpolator),
              @"ANTICIPATE_OVERSHOOT": @(LVAnticipateOvershootInterpolator),
              };
        [LVUtil defineGlobal:@"Interpolator" value:v L:L];
    }
    {
        // 坑位浮动Pinned
        lua_settop(L, 0);
        NSDictionary* v = nil;
        v = @{
              @"YES":@(YES),
              @"Yes":@(YES),//__deprecated_msg("Use YES")
              @"yes":@(YES),//__deprecated_msg("Use YES")
              };
        [LVUtil defineGlobal:@"Pinned" value:v L:L];
    }
    
    {
        // ViewEffect define
        lua_settop(L, 0);
        NSDictionary* v = nil;
        v = @{
              @"NONE":@(EFFECT_NONE),
              @"CLICK":@(EFFECT_CLICK),
              @"PARALLAX":@(EFFECT_PARALLAX),
              };
        [LVUtil defineGlobal:@"ViewEffect" value:v L:L];
    }
    
    // 震动 完全不兼容安卓, 安卓是类, vabrate(数组) cancel() hasVabrate();
    lv_defineGlobalFunc("Vibrate", vibrate, L);
    
    // create class api
    lv_defineGlobalFunc("__class__", lvClassLoader, L);
    return 0;
}

@end
