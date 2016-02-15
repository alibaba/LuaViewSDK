//
//  LVFunctionRegister
//  lv5.1.4
//
//  Created by dongxicheng on 11/27/14.
//  Copyright (c) 2014 dongxicheng. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "LVRegisterManager.h"
#import "LVButton.h"
#import "LVScrollView.h"
#import "LView.h"
#import "LVTimer.h"
#import "LVUtil.h"
#import "LVPagerIndicator.h"
#import "LVLoadingIndicator.h"
#import "LVImage.h"
#import "LVLabel.h"
#import "LVBaseView.h"
#import "LVTransform3D.h"
#import "LVTextField.h"
#import "LVAnimate.h"
#import "LVAnimator.h"
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
#import "LVStyledString.h"
#import "LVExternalLinker.h"
#import "LVNativeObjBox.h"
#import "LVCollectionView.h"
#import "LVStruct.h"
#import "LVNavigation.h"
#import "LVCustomPanel.h"
#import "LVPagerView.h"
#import "LVHeads.h"
#import "lV.h"
#import "lVauxlib.h"
#import "lVlib.h"
#import "lVstate.h"
#import "lVgc.h"


//------------------------------------------------------------------------
@implementation LVRegisterManager


#pragma -mark registryApi
// 全局静态常量 和 静态方法
+(void) registryStaticMethod:(lv_State *)L lView:(LView *)lView{
    lv_pushcfunction(L, loadJson);
    lv_setglobal(L, "loadJson");
    
    lv_pushcfunction(L, unicode);
    lv_setglobal(L, "Unicode");
    
    // 替换pakcage.loaders中的loader_lv
    
    lv_getglobal(L, LV_LOADLIBNAME);
    lv_getfield(L, 1, "loaders");
    if (!lv_istable(L, -1)) {
        return;
    }
    
    lv_pushnumber(L, 2);
    lv_pushcfunction(L, loaderForLuaView);
    lv_settable(L, -3);
}

// 注册函数
+(void) registryApi:(lv_State*)L  lView:(LView*)lView{
    //清理栈
    lv_settop(L, 0);
    lv_checkstack(L, 128);
    
    // 注册静态全局方法和常量
    [LVRegisterManager registryStaticMethod:L lView:lView];
    
    // 注册System对象
    [LVSystem classDefine:L];
    
    // 基础数据结构data
    [LVData classDefine:L];
    [LVStruct classDefine:L];
    
    // 注册UI类
    lv_settop(L, 0);
    [LVBaseView classDefine:L];
    [LVButton    classDefine:L ];
    [LVImage classDefine:L];
    [LVLabel     classDefine:L ];
    [LVScrollView classDefine:L];
    [LVTableView classDefine:L];
    [LVCollectionView classDefine:L];
    [LVPagerView classDefine:L];
    [LVTimer       classDefine:L];
    [LVPagerIndicator classDefine:L];
    [LVCustomPanel classDefine:L];
    [LVTransform3D classDefine:L];
    [LVAnimator classDefine:L];
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
    [LVStyledString classDefine:L];
    
    // 注册 系统对象window
    [LVRegisterManager registryWindow:L lView:lView];
    
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
    NEW_USERDATA(userData, View);
    userData->object = CFBridgingRetain(lView);
    lView.lv_userData = userData;
    
    lvL_getmetatable(L, META_TABLE_LuaView );
    lv_setmetatable(L, -2);
    
    lv_setglobal(L, "window");
}
//------------------------------------------------------------------------------------

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

static int loaderForLuaView (lv_State *L) {
    static NSString *pathFormats[] = { @"%@.%@", @"%@/init.%@" };

    NSString* moduleName = lv_paramString(L, 1);
    if( moduleName ){
        // submodule
        moduleName = [moduleName stringByReplacingOccurrencesOfString:@"." withString:@"/"];
        
        LView* lview = (__bridge LView *)(L->lView);
        if( lview ) {
            __block NSString *fullName = nil, *format = nil, *ext = nil;
            BOOL(^findFile)() = ^BOOL() { // set fullName and return YES if found
                NSString *name = [NSString stringWithFormat:format, moduleName, ext];
                
                if( [lview.bundle scriptPathWithName:name] ) {
                    fullName = name;
                    return YES;
                } else {
                    return NO;
                }
            };

            for( int i = 0; i < sizeof(pathFormats) / sizeof(pathFormats[0]); ++i ) {
                format = pathFormats[i];
                
                if( lview.runInSignModel ) {
                    ext = LVScriptExts[LVSignedScriptExtIndex];
                    if (findFile()) {
                        return [lview loadSignFile:fullName] == nil ? 1 : 0;
                    }
                }
                
                ext = LVScriptExts[!LVSignedScriptExtIndex];
                if (findFile()) {
                    return [lview loadFile:fullName] == nil ? 1 : 0;
                }
            }
        }
    }
    
    // not found
    return 0;
}

@end
