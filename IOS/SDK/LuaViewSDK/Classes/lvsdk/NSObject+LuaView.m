/**
  * Created by LuaView.
  * Copyright (c) 2017, Alibaba Group. All rights reserved.
  *
  * This source code is licensed under the MIT.
  * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
  */

#import "NSObject+LuaView.h"
#import "LuaViewCore.h"

@implementation NSObject(NSObjectLuaView)

- (LuaViewCore*) lv_luaviewCore{
    return nil;
}

- (void) setLv_luaviewCore:(LuaViewCore *)lv_luaviewCore{
}

- (LVUserDataInfo*)lv_userData{
    return nil;
}

- (void) setLv_userData:(LVUserDataInfo *)userData{
}

- (void) lv_callLuaCallback:(NSString*) key1{
    [self lv_callLuaCallback:key1 key2:nil argN:0];
}

- (void) lv_callLuaCallback:(NSString*) key1 key2:(NSString*) key2 argN:(int)argN{
    lua_State* l = self.lv_luaviewCore.l;
    if( l && self.lv_userData && key1){
        lua_checkstack32(l);
        lv_pushUserdata(l, self.lv_userData);
        lv_pushUDataRef(l, USERDATA_KEY_DELEGATE);
        if( lua_type(l, -1) == LUA_TTABLE ) {
            lua_getfield(l, -1, STR_CALLBACK);
            if( lua_type(l, -1)==LUA_TNIL ) {
                lua_remove(l, -1);
            } else {
                lua_remove(l, -2);
            }
        }
        [LVUtil call:l key1:key1.UTF8String key2:key2.UTF8String key3:NULL nargs:argN nrets:0 retType:LUA_TNONE];
    }
}

-(NSString*) lv_callLuaFunc:(NSString*) functionName args:(NSArray*) args{
    lua_State* L = self.lv_luaviewCore.l;
    if( L ){
        lua_checkstack(L, (int)args.count*2 + 2);
        
        for( int i=0; i<args.count; i++ ){
            id obj = args[i];
            lv_pushNativeObject(L,obj);
        }
        lua_getglobal(L, functionName.UTF8String);// function
        return lv_runFunctionWithArgs(L, (int)args.count, 0);
    }
    return nil;
}

-(void) lv_buttonCallBack{
    lua_State* L = self.lv_luaviewCore.l;
    if( L && self.lv_userData ){
        int num = lua_gettop(L);
        lv_pushUserdata(L, self.lv_userData);
        lv_pushUDataRef(L, USERDATA_KEY_DELEGATE );
        if( lua_type(L, -1)==LUA_TTABLE ) {
            lua_getfield(L, -1, STR_ON_CLICK);
        }
        lv_runFunction(L);
        lua_settop(L, num);
    }
}

- (id) lv_nativeObject{
    return self;
}


@end
