//
//  LVUserDefaults.m
//  LVSDK
//
//  Created by dongxicheng on 1/15/15.
//  Copyright (c) 2015 dongxicheng. All rights reserved.
//

#import "LVDB.h"
#import "lV.h"
#import "lVauxlib.h"
#import "lVlib.h"
#import "lVstate.h"
#import "lVgc.h"

@implementation LVDB



static int db_get (lv_State *L) {
    lv_clearFirstTableValue(L);
    NSString* key = lv_paramString(L, 1);
    NSString* defaultValue = nil;
    if( lv_gettop(L)>=2 && lv_type(L, 2)==LV_TNUMBER ) {
        double v = lv_tonumber(L, 2);
        defaultValue = [NSString stringWithFormat:@"%lf",v];
    } else {
        defaultValue = lv_paramString(L, 2);
    }
    if( key.length>0 ) {
        NSString* value = [[NSUserDefaults standardUserDefaults] objectForKey:key];
        if( value == nil )
            value = defaultValue;
        lv_pushstring(L, value.UTF8String);
        return 1; /* number of results */
    } else {
        return 0;
    }
}

static BOOL needSub(NSString* s){
    if( [s rangeOfString:@"."].length>0 ){
        if( [s hasSuffix:@"0"] || [s hasSuffix:@"."] ){
            return YES;
        }
    }
    return NO;
}

static NSString* clearString(NSString* s){
    if( needSub(s) ){
        NSMutableString* buf = [[NSMutableString alloc] initWithString:s];
        for( ;buf.length>0; ){
            if( needSub(buf) ){
                NSRange range = {0};
                range.length = 1;
                range.location = buf.length-1;
                [buf deleteCharactersInRange:range];
            } else {
                break;
            }
        }
        return buf;
    }
    return s;
}

static int db_set (lv_State *L) {
    lv_clearFirstTableValue(L);
    if( lv_gettop(L)>=2 ) {
        NSString* key = lv_paramString(L, 1);
        NSString* value = lv_paramString(L, 2);
        if( value==nil && (lv_type(L, 2)==LV_TNUMBER) ){
            value = [NSString stringWithFormat:@"%f",lv_tonumber(L, 2)];
            value = clearString(value);
        }
        if( key.length>0 && value.length>0 ){
            [[NSUserDefaults standardUserDefaults] setObject:value forKey:key];
            [[NSUserDefaults standardUserDefaults] synchronize];
        }
    }
    return 0;
}

+(int) classDefine:(lv_State *)l {
    const struct lvL_reg functions [] = {
        {"get",  db_get},
        {"set",  db_set},
        {LUAVIEW_SYS_TABLE_KEY, db_set},
        {NULL,   NULL}
    };
    lvL_openlib(l, "DB", functions, 0);
    return 0;
}

@end
