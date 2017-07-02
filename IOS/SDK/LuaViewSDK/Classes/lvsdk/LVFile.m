/**
  * Created by LuaView.
  * Copyright (c) 2017, Alibaba Group. All rights reserved.
  *
  * This source code is licensed under the MIT.
  * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
  */

#import "LVFile.h"
#import "LVHeads.h"
#import "LVData.h"
#import "LView.h"
#import "LVHeads.h"

@implementation LVFile

static void saveCallback(lua_State *L, int callbackIndex, NSString* fileName, int errorInfo ) {
    if( callbackIndex ) {
        lua_checkstack32(L);
        lua_pushboolean(L, errorInfo);
        lua_pushvalue(L,callbackIndex);
        lv_runFunctionWithArgs(L, 1, 0);
    }
}

static BOOL readCallback(lua_State *L, int callbackIndex, NSString* fileName, NSData* data) {
    if( callbackIndex ) {
        lua_checkstack32(L);
        [LVData createDataObject:L data:data];
        lua_pushvalue(L,callbackIndex);
        lv_runFunctionWithArgs(L, 1, 0);
        return YES;
    }
    return NO;
}

//参数顺序
//File.save(name, data, callback)
//name: string
//data: string or data
//callback: function (optional)
static int file_save (lua_State *L) {
    int num = lua_gettop(L);
    if( num>=2 ) {
        LVUserDataInfo * contentData = NULL;
        NSString * contentString = nil;
        NSString * fileName = nil;
        int callbackIndex = 0;
        for( int i=1; i<=num; i++ ) {
            if (i == 1 && lua_type(L, i) == LUA_TSTRING){
                fileName = lv_paramString(L, i);
            }else if (i == 2){
                if (lua_type(L, i) == LUA_TSTRING){
                    contentString = lv_paramString(L, i);
                }else if (lua_type(L, i) == LUA_TUSERDATA){
                    contentData = (LVUserDataInfo *)lua_touserdata(L, i);
                }
            }else if (i == 3 && lua_type(L, i) == LUA_TFUNCTION){
                callbackIndex = i;
            }
        }
        
        NSData *content;
        
        if (contentData){
            LVData* lvData1 = (__bridge LVData *)(contentData->object);
            if( LVIsType(contentData, Data) && lvData1.data){
                content = lvData1.data;
            }
        }else if (contentString){
            content = [contentString dataUsingEncoding:NSUTF8StringEncoding];
        }
        
        if ( fileName && content ) {
            if( [LVUtil saveData:content toFile:[LVUtil PathForCachesResource:fileName]] ){
                saveCallback(L, callbackIndex, fileName, YES);
                lua_pushboolean(L, 1);
                return 1;
            } else {
                saveCallback(L, callbackIndex, fileName, NO);
            }
        }
    }
    lua_pushboolean(L, 0);
    return 1;
}

static int file_read(lua_State *L){
    int num = lua_gettop(L);
    if( L && num>=1 ){
        NSString* fileName = nil;
        int callbackIndex = 0;
        for( int i=1; i<=num; i++ ) {
            if( lua_type(L, i)==LUA_TSTRING  && fileName==nil ) {
                fileName = lv_paramString(L, i);
            }
            if( lua_type(L,i)==LUA_TFUNCTION ) {
                callbackIndex = i;
            }
        }
        if ( fileName ) {
            LuaViewCore* lview = LV_LUASTATE_VIEW(L);
            NSData* data = [lview.bundle resourceWithName:fileName];
            if( data ){
                if( readCallback(L, callbackIndex, fileName, data) ){
                } else {
                    [LVData createDataObject:L data:data];
                }
                return 1;
            } else {
                readCallback(L, callbackIndex, fileName, nil);
            }
        }
    }
    return 0;
}

static int file_exist(lua_State *L){
    if( L && lua_gettop(L)>=1 ){
        NSString* fileName = lv_paramString(L, -1);
        LuaViewCore* lview = LV_LUASTATE_VIEW(L);
        if(  [lview.bundle resourcePathWithName:fileName] ){
            lua_pushboolean(L, 1);
            return 1;
        }
    }
    lua_pushboolean(L, 0);
    return 1;
}

static int file_path (lua_State *L) {
    NSString* fileName = lv_paramString(L, -1);
    LuaViewCore* lview = LV_LUASTATE_VIEW(L);
    NSString* path = [lview.bundle resourcePathWithName:fileName];
    lua_pushstring(L, path.UTF8String);
    return 1;
}

+(int) lvClassDefine:(lua_State *)L globalName:(NSString*) globalName{
    const struct luaL_Reg function [] = {
        {"save", file_save},
        {"read", file_read},
        {"exist", file_exist},
        {"path", file_path},
        {NULL, NULL}
    };
    luaL_openlib(L, "File", function, 0);
    return 0;
}

@end
