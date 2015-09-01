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

@implementation LVFile

static int file_save (lv_State *L) {
    int num = lv_gettop(L);
    if( num>=2 ) {
        LVUserDataData * userData = NULL;
        NSString* fileName = nil;
        for( int i=1; i<=num; i++ ) {
            if( lv_type(L, i)==LV_TUSERDATA  && userData==nil ) {
                userData = (LVUserDataData *)lv_touserdata(L, i);
            }
            if( lv_type(L, i)==LV_TSTRING  && fileName==nil ) {
               fileName = lv_paramString(L, i);
            }
        }
        if ( userData ) {
            LVData* lvData1 = (__bridge LVData *)(userData->data);
            if( fileName && LVIsType(userData,LVUserDataData) && lvData1.data){
                if(  [LVUtil saveData:lvData1.data toFile:fileName] ){
                    lv_pushboolean(L, 1);
                    return 1;
                }
            }
        }
    }
    lv_pushboolean(L, 0);
    return 1;
}

static int file_read(lv_State *L){
    if( lv_gettop(L)>=1 ){
        NSString* fileName = lv_paramString(L, 1);
        NSData* data = [LVUtil dataReadFromFile:fileName];
        if( data ){
            [LVData createDataObject:L data:data];
            return 1;
        }
    }
    return 0;
}

static int file_exist(lv_State *L){
    if( lv_gettop(L)>=1 ){
        NSString* fileName = lv_paramString(L, 1);
        if(  [LVUtil cachesPath:fileName] ){
            lv_pushboolean(L, 1);
            return 1;
        }
    }
    lv_pushboolean(L, 0);
    return 1;
}

+(int) classDefine:(lv_State *)l {
    const struct lvL_reg function [] = {
        {"save", file_save},
        {"read", file_read},
        {"exist", file_exist},
        {NULL, NULL}
    };
    lvL_openlib(l, "File", function, 0);
    return 0;
}

@end
