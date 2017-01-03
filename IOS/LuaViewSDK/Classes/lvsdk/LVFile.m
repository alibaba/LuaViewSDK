//
//  LVFile.m
//  LVSDK
//
//  Created by dongxicheng on 4/15/15.
//  Copyright (c) 2015 dongxicheng. All rights reserved.
//

#import "LVFile.h"
#import "LVHeads.h"
#import "LVData.h"
#import "LView.h"
#import "LVHeads.h"

@implementation LVFile

static void saveCallback(lua_State *L, int callbackIndex, NSString* fileName, int errorInfo ) {
    if( callbackIndex ) {
        lv_checkStack32(L);
        lv_pushboolean(L, errorInfo);
        lv_pushvalue(L,callbackIndex);
        lv_runFunctionWithArgs(L, 1, 0);
    }
}

static BOOL readCallback(lua_State *L, int callbackIndex, NSString* fileName, NSData* data) {
    if( callbackIndex ) {
        lv_checkStack32(L);
        [LVData createDataObject:L data:data];
        lv_pushvalue(L,callbackIndex);
        lv_runFunctionWithArgs(L, 1, 0);
        return YES;
    }
    return NO;
}

static int file_save (lua_State *L) {
    int num = lv_gettop(L);
    if( num>=2 ) {
        LVUserDataInfo * userData = NULL;
        NSString* fileName = nil;
        int callbackIndex = 0;
        for( int i=1; i<=num; i++ ) {
            if( lv_type(L, i)==LV_TUSERDATA  && userData==nil ) {
                userData = (LVUserDataInfo *)lv_touserdata(L, i);
            }
            if( lv_type(L, i)==LV_TSTRING  && fileName==nil ) {
               fileName = lv_paramString(L, i);
            }
            if( lv_type(L,i)==LV_TFUNCTION ) {
                callbackIndex = i;
            }
        }
        if ( fileName && userData ) {
            LVData* lvData1 = (__bridge LVData *)(userData->object);
            if( LVIsType(userData, Data) && lvData1.data){
                if( [LVUtil saveData:lvData1.data toFile:[LVUtil PathForCachesResource:fileName]] ){
                    saveCallback(L, callbackIndex, fileName, YES);
                    lv_pushboolean(L, 1);
                    return 1;
                } else {
                    saveCallback(L, callbackIndex, fileName, NO);
                }
            }
        }
    }
    lv_pushboolean(L, 0);
    return 1;
}

static int file_read(lua_State *L){
    int num = lv_gettop(L);
    if( L && num>=1 ){
        NSString* fileName = nil;
        int callbackIndex = 0;
        for( int i=1; i<=num; i++ ) {
            if( lv_type(L, i)==LV_TSTRING  && fileName==nil ) {
                fileName = lv_paramString(L, i);
            }
            if( lv_type(L,i)==LV_TFUNCTION ) {
                callbackIndex = i;
            }
        }
        if ( fileName ) {
            LView* lview = (__bridge LView *)(L->lView);
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
    if( L && lv_gettop(L)>=1 ){
        NSString* fileName = lv_paramString(L, -1);
        LView* lview = (__bridge LView *)(L->lView);
        if(  [lview.bundle resourcePathWithName:fileName] ){
            lv_pushboolean(L, 1);
            return 1;
        }
    }
    lv_pushboolean(L, 0);
    return 1;
}

static int file_path (lua_State *L) {
    NSString* fileName = lv_paramString(L, -1);
    LView* lview = (__bridge LView *)(L->lView);
    NSString* path = [lview.bundle resourcePathWithName:fileName];
    lv_pushstring(L, path.UTF8String);
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
    lvL_openlib(L, "File", function, 0);
    return 0;
}

@end
