/**
  * Created by LuaView.
  * Copyright (c) 2017, Alibaba Group. All rights reserved.
  *
  * This source code is licensed under the MIT.
  * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
  */

#import "LVDB.h"
#import "LVHeads.h"

@implementation LVDB



static int db_get (lua_State *L) {
    lv_clearFirstTableValue(L);
    NSString* key = lv_paramString(L, 1);
    NSString* defaultValue = nil;
    if( lua_gettop(L)>=2 && lua_type(L, 2)==LUA_TNUMBER ) {
        double v = lua_tonumber(L, 2);
        defaultValue = [NSString stringWithFormat:@"%lf",v];
    } else {
        defaultValue = lv_paramString(L, 2);
    }
    if( key.length>0 ) {
        NSString* value = [[NSUserDefaults standardUserDefaults] objectForKey:key];
        if( value == nil )
            value = defaultValue;
        lua_pushstring(L, value.UTF8String);
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

static int db_set (lua_State *L) {
    lv_clearFirstTableValue(L);
    if( lua_gettop(L)>=2 ) {
        NSString* key = lv_paramString(L, 1);
        NSString* value = lv_paramString(L, 2);
        if( value==nil && (lua_type(L, 2)==LUA_TNUMBER) ){
            value = [NSString stringWithFormat:@"%f",lua_tonumber(L, 2)];
            value = clearString(value);
        }
        if( key.length>0 && value.length>0 ){
            [[NSUserDefaults standardUserDefaults] setObject:value forKey:key];
            [[NSUserDefaults standardUserDefaults] synchronize];
        }
    }
    return 0;
}

+(int) lvClassDefine:(lua_State *)L globalName:(NSString*) globalName{
    const struct luaL_Reg functions [] = {
        {"get",  db_get},
        {"set",  db_set},
        {LUAVIEW_SYS_TABLE_KEY, db_set},
        {NULL,   NULL}
    };
    luaL_openlib(L, "DB", functions, 0);
    return 0;
}

@end
