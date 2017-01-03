//
//  LVFunctionRegister
//  lv5.1.4
//
//  Created by dongxicheng on 11/27/14.
//  Copyright (c) 2014 dongxicheng. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "LVExGlobalFunc.h"
#import "LVUtil.h"
#import "LVHeads.h"
#import "LVNativeObjBox.h"
#import "LVDebuger.h"


//------------------------------------------------------------------------
@implementation LVExGlobalFunc


#pragma -mark registryApi
// 全局静态常量 和 静态方法
+(void) registryStaticMethod:(lua_State *)L lView:(LView *)lView{
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
+(void) registryApi:(lua_State*)L  lView:(LView*)lView{
    
    
    return;
}

// 注册系统对象 window
+(void) registryWindow:(lua_State*)L  lView:(LView*)lView{
    NEW_USERDATA(userData, View);
    userData->object = CFBridgingRetain(lView);
    lView.lv_userData = userData;
    
    lvL_getmetatable(L, META_TABLE_LuaView );
    lv_setmetatable(L, -2);
    
    lv_setglobal(L, "window");
}
//------------------------------------------------------------------------------------

static int loadJson (lua_State *L) {
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

static int unicode(lua_State *L) {
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

static int loaderForLuaView (lua_State *L) {
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


+(int) lvClassDefine:(lua_State *)L globalName:(NSString*) globalName{
    LView* lView = (__bridge  LView *)(L->lView);
    // 注册静态全局方法和常量
    [LVExGlobalFunc registryStaticMethod:L lView:lView];
    
    // 注册 系统对象window
    [LVExGlobalFunc registryWindow:L lView:lView];
    
    //外链注册器
    [LVNativeObjBox lvClassDefine:L globalName:nil];
    // 调试
    [LVDebuger lvClassDefine:L globalName:nil];
    //清理栈
    lv_settop(L, 0);
    return 0;
}

@end
