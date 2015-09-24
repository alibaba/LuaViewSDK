//
//  LVFunctionRegister
//  lv5.1.4
//
//  Created by dongxicheng on 11/27/14.
//  Copyright (c) 2014 dongxicheng. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <AudioToolbox/AudioToolbox.h>
#import "LVFunctionRegister.h"
#import "LVButton.h"
#import "LVScrollView.h"
#import "LView.h"
#import "LVTimer.h"
#import "LVUtil.h"
#import "LVPagerIndicator.h"
#import "LVLoadingIndicator.h"
#import "LVImageView.h"
#import "LVLabel.h"
#import "LVBaseView.h"
#import "LVTransform3D.h"
#import "LVTextField.h"
#import "LVAnimate.h"
#import "LVDate.h"
#import "LVAlert.h"
#import "LVSystem.h"
#import "LVDB.h"
#import "LVGestureRecognizer.h"
#import "LVTapGestureRecognizer.h"
#import "LVPanGestureRecognizer.h"
#import "LVPinchGestureRecognizer.h"
#import "LVRotationGestureRecognizer.h"
#import "LVHttp.h"
#import "LVData.h"
#import "LVSwipeGestureRecognizer.h"
#import "LVLongPressGestureRecognizer.h"
#import "LVDebuger.h"
#import "LVTableView.h"
#import "LVTableViewCell.h"
#import "LVDownloader.h"
#import "LVAudioPlayer.h"
#import "LVFile.h"
#import "LVAttributedString.h"
#import "LVExternalLinker.h"
#import "LVNativeObjBox.h"
#import "LVCollectionView.h"
#import "LVStruct.h"
#import "LVNavigation.h"
#import "LVCustomLoadingView.h"
#import "LVCustomErrorView.h"
#import "LVPageView.h"

//------------------------------------------------------------------------------------
#pragma -mark base
// 获取参数-》字符串类型
NSString* lv_paramString(lv_State* L, int idx ){
    if( lv_gettop(L)>=ABS(idx) && lv_type(L, idx) == LV_TSTRING ) {
        size_t n = 0;
        const char* chars = lvL_checklstring(L, idx, &n );
        NSString* s = @"";
        if( chars && n>0 ){
            s = [NSString stringWithUTF8String:chars];
        }
        return s;
    }
    return nil;
}

int lv_runFunction(lv_State* l){
    return lv_runFunctionWithArgs(l, 0, 0);
}

int lv_runFunctionWithArgs(lv_State* l, int nargs, int nret){
    if( lv_type(l, -1) == LV_TFUNCTION ) {
        if( nargs>0 ){
            lv_insert(l, -nargs-1);
        }
        int errorCode = lv_pcall( l, nargs, nret, 0);
        if ( errorCode != 0 ) {
            LVError( @"running function : %s", lv_tostring(l, -1) );
        }
        return errorCode;
    }
    return -1;
}

void lv_stopAndExitNow(lv_State* l){
    LView* view = (__bridge LView *)(l->lView);
    l->lView = NULL;
    [view releaseLuaView];
}

static int IsMethod (lv_State *L) {
    int argN = lv_gettop(L);
    for( int i=0; i<argN; i++) {
        if( lv_type(L, 1) == LV_TFUNCTION ) {
            lv_pushboolean(L, 1);
        } else {
            lv_pushboolean(L, 0);
        }
    }
    return argN; /* number of results */
}
static int IsBoolean (lv_State *L) {
    int argN = lv_gettop(L);
    for( int i=0; i<argN; i++) {
        if( lv_type(L, 1) == LV_TBOOLEAN ) {
            lv_pushboolean(L, 1);
        } else {
            lv_pushboolean(L, 0);
        }
    }
    return argN; /* number of results */
}
static int IsNumber (lv_State *L) {
    int argN = lv_gettop(L);
    for( int i=0; i<argN; i++) {
        if( lv_type(L, 1) == LV_TNUMBER ) {
            lv_pushboolean(L, 1);
        } else {
            lv_pushboolean(L, 0);
        }
    }
    return argN; /* number of results */
}
static int IsNil (lv_State *L) {
    int argN = lv_gettop(L);
    for( int i=0; i<argN; i++) {
        if( lv_type(L, 1) == LV_TNIL ) {
            lv_pushboolean(L, 1);
        } else {
            lv_pushboolean(L, 0);
        }
    }
    return argN; /* number of results */
}
static int IsUserData (lv_State *L) {
    int argN = lv_gettop(L);
    for( int i=0; i<argN; i++) {
        if( lv_type(L, 1) == LV_TUSERDATA ) {
            lv_pushboolean(L, 1);
        } else {
            lv_pushboolean(L, 0);
        }
    }
    return argN; /* number of results */
}
static int IsString (lv_State *L) {
    int argN = lv_gettop(L);
    for( int i=0; i<argN; i++) {
        if( lv_type(L, 1) == LV_TSTRING ) {
            lv_pushboolean(L, 1);
        } else {
            lv_pushboolean(L, 0);
        }
    }
    return argN; /* number of results */
}
static int IsTable (lv_State *L) {
    int argN = lv_gettop(L);
    for( int i=0; i<argN; i++) {
        if( lv_type(L, 1) == LV_TTABLE ) {
            lv_pushboolean(L, 1);
        } else {
            lv_pushboolean(L, 0);
        }
    }
    return argN; /* number of results */
}

static int vibrate(lv_State*L){
    AudioServicesPlaySystemSound(kSystemSoundID_Vibrate);
    return 1;
}

static int loadJson (lv_State *L) {
    NSString* json = lv_paramString(L, 1);
    if( json ){
        json = [NSString stringWithFormat:@"return %@",json];
        lvL_loadstring(L, json.UTF8String);
        if( lv_type(L, -1) == LV_TFUNCTION ) {
            int errorCode = lv_pcall( L, 0, 1, 0);
            if( errorCode == 0 ){
                return 1;
            } else {
                LVError( @"loadJson : %s", lv_tostring(L, -1) );
            }
        } else {
            LVError( @"loadJson : %s", lv_tostring(L, -1) );
        }
    }
    return 0; /* number of results */
}

static int unicode(lv_State *L) {
    int num = lv_gettop(L);
    NSMutableString* buf = [[NSMutableString alloc] init];
    for( int i=1; i<=num; i++ ) {
        if( lv_type(L, i) == LV_TNUMBER ) {
            unichar c = lv_tonumber(L, i);
            [buf appendFormat:@"%C",c];
        } else {
            break;
        }
    }
    if( buf.length>0 ) {
        lv_pushstring(L, buf.UTF8String);
        return 1;
    }
    return 0; /* number of results */
}
//------------------------------------------------------------------------

static int runFile (lv_State *L) {
    NSString* fileName = lv_paramString(L, 1);
    if( fileName ){
        LView* lview = (__bridge LView *)(L->lView);
        if( lview ) {
            int ret = 0;
            if ( lview.runInSignModel ) {
                fileName = [NSString stringWithFormat:@"%@.lv",fileName];
                ret = [lview runSignFile:fileName];
            } else {
                fileName = [NSString stringWithFormat:@"%@.lua",fileName];
                ret =[lview runFile:fileName];
            }
            lv_pushnumber(L, ret);
            return 1;
        }
    }
    return 0; /* number of results */
}
//------------------------------------------------------------------------
static int debug_log (lv_State *L) {
    switch ( lv_type(L, 1) ) {
        case LV_TNONE:
            LVLog(@"[none]");
            break;
        case LV_TNIL:
            LVLog(@"nil");
            break;
        case LV_TBOOLEAN:
            LVLog(@"%d", lv_toboolean(L, 1) );
            break;
        case LV_TLIGHTUSERDATA:
            LVLog(@"[lightuserdata]" );
            break;
        case LV_TNUMBER:
            LVLog(@"%.f", lv_tonumber(L, 1));
            break;
        case LV_TSTRING:
            LVLog(@"%@", lv_paramString(L, 1));
            break;
        case LV_TTABLE:
            LVLog(@"[table]");
            break;
        case LV_TFUNCTION:
            LVLog(@"[function]");
            break;
        case LV_TUSERDATA:
            LVLog(@"[userdata]");
            break;
        case LV_TTHREAD:
            LVLog(@"[thread]");
            break;
        default:
            LVError(@"unkown Lua Type");
            break;
    }
    return 0; /* number of results */
}


//------------------------------------------------------------------------
@implementation LVFunctionRegister


#pragma -mark registryApi
// 全局静态常量 和 静态方法
+(void) registryApiStaticMethod:(lv_State *)L lView:(LView *)lView{
    {
        lv_pushboolean(L, 1);
        lv_setglobal(L, "YES");
        lv_pushboolean(L, 1);
        lv_setglobal(L, "TRUE");
    }
    {
        lv_pushboolean(L, 0);
        lv_setglobal(L, "NO");
        lv_pushboolean(L, 0);
        lv_setglobal(L, "FALSE");
    }
    {
        lv_pushcfunction(L, debug_log);
        lv_setglobal(L, "Debug");
    }
    {
        lv_pushcfunction(L, unicode);
        lv_setglobal(L, "Unicode");
    }
    {
        lv_pushcfunction(L, IsMethod);
        lv_setglobal(L, "IsMethod");
        lv_pushcfunction(L, IsString);
        lv_setglobal(L, "IsString");
        lv_pushcfunction(L, IsTable);
        lv_setglobal(L, "IsTable");
        lv_pushcfunction(L, IsUserData);
        lv_setglobal(L, "IsUserData");
        lv_pushcfunction(L, IsNumber);
        lv_setglobal(L, "IsNumber");
        lv_pushcfunction(L, IsNil);
        lv_setglobal(L, "IsNil");
        lv_pushcfunction(L, IsBoolean);
        lv_setglobal(L, "IsBoolean");
        lv_pushcfunction(L, vibrate);
        lv_setglobal(L, "Vibrate");
        lv_pushcfunction(L, loadJson);
        lv_setglobal(L, "LoadJson");
    }
    lv_pushcfunction(L, runFile);
    lv_setglobal(L, "run");
    lv_pushcfunction(L, runFile);
    lv_setglobal(L, "require");
}
// 注册函数
+(void) registryApi:(lv_State*)L  lView:(LView*)lView{
    //清理栈
    lv_settop(L, 0);
    lv_checkstack(L, 128);
    
    // 注册静态全局方法和常量
    [LVFunctionRegister registryApiStaticMethod:L lView:lView];
    
    // 注册System对象
    [LVSystem classDefine:L];
    
    // 基础数据结构data
    [LVData classDefine:L];
    [LVStruct classDefine:L];
    
    // 注册UI类
    lv_settop(L, 0);
    [LVBaseView classDefine:L];
    [LVButton    classDefine:L ];
    [LVImageView classDefine:L];
    [LVLabel     classDefine:L ];
    [LVScrollView classDefine:L];
    [LVTableView classDefine:L];
    [LVCollectionView classDefine:L];
    [LVPageView classDefine:L];
    [LVTimer       classDefine:L];
    [LVPagerIndicator classDefine:L];
    [LVCustomLoadingView classDefine:L];
    [LVCustomErrorView classDefine:L];
    [LVTransform3D classDefine:L];
    [LVTextField classDefine:L];
    [LVAnimate classDefine:L];
    [LVDate classDefine:L];
    [LVAlert classDefine:L];
    // 注册DB
    [LVDB classDefine:L];
    
    //清理栈
    lv_settop(L, 0);
    
    // 注册手势
    [LVGestureRecognizer    classDefine:L];
    [LVTapGestureRecognizer classDefine:L];
    [LVPinchGestureRecognizer classDefine:L];
    [LVRotationGestureRecognizer classDefine:L];
    [LVSwipeGestureRecognizer classDefine:L];
    [LVLongPressGestureRecognizer classDefine:L];
    [LVPanGestureRecognizer classDefine:L];
    
    //清理栈
    lv_settop(L, 0);
    [LVLoadingIndicator classDefine:L];
    
    // http
    [LVHttp classDefine:L];
    
    // 文件下载
    [LVDownloader classDefine:L];
    
    // 文件
    [LVFile classDefine:L];
    
    
    // 声音播放
    [LVAudioPlayer classDefine:L];
    
    // 调试
    [LVDebuger classDefine:L];
    
    // attributedString
    [LVAttributedString classDefine:L];
    
    // 注册 系统对象window
    [LVFunctionRegister registryWindow:L lView:lView];
    
    // 导航栏按钮
    [LVNavigation classDefine:L];
    
    //清理栈
    lv_settop(L, 0);
    
    //外链注册器
    [LVExternalLinker classDefine:L];
    
    //清理栈
    lv_settop(L, 0);
    return;
}

// 注册系统对象 window
+(void) registryWindow:(lv_State*)L  lView:(LView*)lView{
    NEW_USERDATA(userData, LVUserDataView);
    userData->view = CFBridgingRetain(lView);
    lView.lv_userData = userData;
    
    lvL_getmetatable(L, META_TABLE_UIScrollView );
    lv_setmetatable(L, -2);
    
    lv_setglobal(L, "window");
}

@end
