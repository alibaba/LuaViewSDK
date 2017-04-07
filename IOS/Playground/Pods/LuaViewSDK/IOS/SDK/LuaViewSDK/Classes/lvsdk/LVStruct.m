/**
  * Created by LuaView.
  * Copyright (c) 2017, Alibaba Group. All rights reserved.
  *
  * This source code is licensed under the MIT.
  * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
  */

#import "LVStruct.h"
#import "LVTypeConvert.h"
#import "LVHeads.h"



@implementation LVStruct{
    CGFloat data[LV_STRUCT_MAX_LEN];
}

-(void) setIndex:(NSInteger)index byValue:(CGFloat) value{
    if( index>=0 && index<LV_STRUCT_MAX_LEN ) {
        data[index] = value;
    }
}

-(CGFloat) getValueByIndex:(NSInteger)index{
    if( index>=0 && index<LV_STRUCT_MAX_LEN ) {
        return data[index];
    }
    return 0;
}

-(CGFloat*) dataPointer{
    return data;
}

-(id) lv_nativeObject{
    return self;
}

static int lvNewStruct (lua_State *L) {
    NEW_USERDATA(userData, Struct);
    LVStruct* lvstruct = [[LVStruct alloc] init];
    userData->object = CFBridgingRetain(lvstruct);
    lvstruct.lv_userData = userData;
    
    int num = lua_gettop(L);
    
    for (int i=1,index=0; (i<=num) && (index<LV_STRUCT_MAX_LEN); i++ ) {
        CGFloat value = lua_tonumber(L, i);
        [lvstruct setIndex:index++ byValue:value];
    }
    
    luaL_getmetatable(L, META_TABLE_Struct );
    lua_setmetatable(L, -2);
    return 1; /* new userdatum is already on the stack */
}

+(int) pushStructToLua:(lua_State*)L data:(void*)data{
    NEW_USERDATA(userData, Struct);
    LVStruct* lvstruct = [[LVStruct alloc] init];
    userData->object = CFBridgingRetain(lvstruct);
    lvstruct.lv_userData = userData;
    
    memcpy( [lvstruct dataPointer], data, LV_STRUCT_MAX_LEN*sizeof(CGFloat) );
    luaL_getmetatable(L, META_TABLE_Struct );
    lua_setmetatable(L, -2);
    return 1;
}


static int setValue (lua_State *L) {
    int argNum = lua_gettop(L);
    if( argNum>=3 ) {
        LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
        unsigned int index = lua_tonumber(L, 2);
        CGFloat value = lua_tonumber(L, 3);
        if ( index>=LV_STRUCT_MAX_LEN ) {
            LVError(@"LVStruct.set index:%d", index );
            return 0;
        }
        if( LVIsType(user, Struct) ){
            LVStruct* lvstruct = (__bridge LVStruct *)(user->object);
            if ( argNum>=4 ) {
                int type = lua_tonumber(L, 4);
                if( [lvstruct dataPointer] ) {
                    lv_setValueWithType( [lvstruct dataPointer], index, value, type);
                }
            } else {
                [lvstruct setIndex:index byValue:value];
            }
            return 0;
        }
    }
    return 0;
}

static int getValue (lua_State *L) {
    int argNum = lua_gettop(L);
    if( argNum>=2 ) {
        LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
        unsigned int index = lua_tonumber(L, 2);
        if ( index>=LV_STRUCT_MAX_LEN ) {
            LVError(@"LVStruct.get index:%d", index );
            return 0;
        }
        if( LVIsType(user, Struct) ){
            LVStruct* stru = (__bridge LVStruct *)(user->object);
            if ( argNum>=3 ) {
                int type = lua_tonumber(L, 3);
                CGFloat value = lv_getValueWithType(  [stru dataPointer], index, type);
                lua_pushnumber(L, value);
                return 1;
            } else {
                CGFloat value = [stru getValueByIndex:index];
                lua_pushnumber(L, value);
                return 1;
            }
        }
    }
    return 0;
}

static int __eq (lua_State *L) {
    if( lua_gettop(L)==2 ) {
        LVUserDataInfo * user1 = (LVUserDataInfo *)lua_touserdata(L, 1);
        LVUserDataInfo * user2 = (LVUserDataInfo *)lua_touserdata(L, 2);
        if( LVIsType(user1, Struct) && LVIsType(user2, Struct) ){
            LVStruct* s1 = (__bridge LVStruct *)(user1->object);
            LVStruct* s2 = (__bridge LVStruct *)(user2->object);
            int size = LV_STRUCT_MAX_LEN;
            BOOL yes = NO;
            if( [s1 dataPointer] && [s2 dataPointer] ) {
                yes = !memcmp( [s1 dataPointer], [s2 dataPointer], size);
            }
            lua_pushboolean(L, (yes?1:0) );
            return 1;
        }
    }
    return 0;
}

static int __tostring (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( LVIsType(user, Struct) ){
        NSString* s = [NSString stringWithFormat:@"LVUserDataStruct: %d", (int)user ];
        lua_pushstring(L, s.UTF8String);
        return 1;
    }
    return 0;
}

static int __index (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( user ){
        if ( lua_type(L, 2)==LUA_TNUMBER ) {
            return  getValue(L);
        } else if ( lua_type(L, 2)==LUA_TSTRING ){
            NSString* key = lv_paramString(L, 2);
            if ( [key isEqualToString:@"get"] ) {
                lua_pushcfunction(L, getValue);
                return 1;
            }
            if ( [key isEqualToString:@"set"] ) {
                lua_pushcfunction(L, setValue);
                return 1;
            }
        }
    }
    return 0; /* new userdatum is already on the stack */
}

static int __newindex (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( user ){
        if( lua_type(L, 2)==LUA_TNUMBER ){
            return setValue(L);
        } else if( lua_type(L, 3)==LUA_TSTRING ){
            
        }
    }
    return 0; /* new userdatum is already on the stack */
}

+(int) lvClassDefine:(lua_State *)L globalName:(NSString*) globalName{
    lv_defineGlobalFunc("Struct", lvNewStruct, L);
    lv_defineGlobalFunc("Rect",   lvNewStruct, L);
    lv_defineGlobalFunc("Size",   lvNewStruct, L);
    lv_defineGlobalFunc("Point",  lvNewStruct, L);
    
    const struct luaL_Reg memberFunctions [] = {
        {"__index", __index },
        {"__newindex", __newindex },
        {"__eq", __eq},
        
        {"set", setValue},
        {"get", getValue},

        {"__tostring", __tostring},
        
        {NULL, NULL}
    };
    
    lv_createClassMetaTable(L ,META_TABLE_Struct);
    
    luaL_openlib(L, NULL, memberFunctions, 0);
    
//    {
//        const struct luaL_Reg memberFunctions2 [] = {
//            {"__index", __index },
//            {"__newindex", __newindex },
//            {NULL, NULL}
//        };
//        lv_createClassMetaTable(L ,"META_TABLE_Struct" );
//        luaL_openlib(L, NULL, memberFunctions2, 0);
//        
//        luaL_getmetatable(L, META_TABLE_Struct );
//        luaL_getmetatable(L, "META_TABLE_Struct" );
//        lua_setmetatable(L, -2);
//    }
    return 1;
}

@end
