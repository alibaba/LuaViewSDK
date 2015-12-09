//
//  LVStruct.m
//  LVSDK
//
//  Created by dongxicheng on 7/2/15.
//  Copyright (c) 2015 dongxicheng. All rights reserved.
//

#import "LVStruct.h"
#import "LVTypeConvert.h"
#import "lV.h"
#import "lVauxlib.h"
#import "lVlib.h"
#import "lVstate.h"
#import "lVgc.h"


@implementation LVStruct

static int lvNewStruct (lv_State *L) {
    NEW_USERDATA(userData, LVUserDataStruct);
    int num = lv_gettop(L);
    
    for (int i=1,index=0; (i<=num) && (index<LV_STRUCT_MAX_LEN); i++ ) {
        CGFloat value = lv_tonumber(L, i);
        userData->data[index++] = value;
    }
    
    lvL_getmetatable(L, META_TABLE_Struct );
    lv_setmetatable(L, -2);
    return 1; /* new userdatum is already on the stack */
}

+(int) pushStructToLua:(lv_State*)L data:(void*)data{
    NEW_USERDATA(userData, LVUserDataStruct);
    memcpy(userData->data, data, sizeof(userData->data) );
    lvL_getmetatable(L, META_TABLE_Struct );
    lv_setmetatable(L, -2);
    return 1;
}


static int setValue (lv_State *L) {
    int argNum = lv_gettop(L);
    if( argNum>=3 ) {
        LVUserDataStruct * user = (LVUserDataStruct *)lv_touserdata(L, 1);
        unsigned int index = lv_tonumber(L, 2);
        CGFloat value = lv_tonumber(L, 3);
        if ( index>=LV_STRUCT_MAX_LEN ) {
            LVError(@"LVStruct.set index:%d", index );
            return 0;
        }
        if( LVIsType(user,LVUserDataStruct) ){
            if ( argNum>=4 ) {
                int type = lv_tonumber(L, 4);
                lv_setValueWithType(user->data, index, value, type);
            } else {
                user->data[index] = value;
            }
            return 0;
        }
    }
    return 0;
}

static int getValue (lv_State *L) {
    int argNum = lv_gettop(L);
    if( argNum>=2 ) {
        LVUserDataStruct * user = (LVUserDataStruct *)lv_touserdata(L, 1);
        unsigned int index = lv_tonumber(L, 2);
        if ( index>=LV_STRUCT_MAX_LEN ) {
            LVError(@"LVStruct.get index:%d", index );
            return 0;
        }
        if( LVIsType(user,LVUserDataStruct) ){
            if ( argNum>=3 ) {
                int type = lv_tonumber(L, 3);
                CGFloat value = lv_getValueWithType(user->data, index, type);
                lv_pushnumber(L, value);
                return 1;
            } else {
                CGFloat value = user->data[index];
                lv_pushnumber(L, value);
                return 1;
            }
        }
    }
    return 0;
}

static int __eq (lv_State *L) {
    if( lv_gettop(L)==2 ) {
        LVUserDataStruct * user1 = (LVUserDataStruct *)lv_touserdata(L, 1);
        LVUserDataStruct * user2 = (LVUserDataStruct *)lv_touserdata(L, 2);
        if( LVIsType(user1,LVUserDataStruct) && LVIsType(user2,LVUserDataStruct) ){
            int size = sizeof(user1->data);
            BOOL yes = !memcmp(user1->data, user2->data, size);
            lv_pushboolean(L, (yes?1:0) );
            return 1;
        }
    }
    return 0;
}

static int __tostring (lv_State *L) {
    LVUserDataStruct * user = (LVUserDataStruct *)lv_touserdata(L, 1);
    if( LVIsType(user,LVUserDataStruct) ){
        NSString* s = [NSString stringWithFormat:@"LVUserDataStruct: %d", (int)user ];
        lv_pushstring(L, s.UTF8String);
        return 1;
    }
    return 0;
}

static int __index (lv_State *L) {
    LVUserDataStruct * user = (LVUserDataStruct *)lv_touserdata(L, 1);
    if( user ){
        if ( lv_type(L, 2)==LV_TNUMBER ) {
            return  getValue(L);
        } else if ( lv_type(L, 2)==LV_TSTRING ){
            NSString* key = lv_paramString(L, 2);
            if ( [key isEqualToString:@"get"] ) {
                lv_pushcfunction(L, getValue);
                return 1;
            }
            if ( [key isEqualToString:@"set"] ) {
                lv_pushcfunction(L, setValue);
                return 1;
            }
        }
    }
    return 0; /* new userdatum is already on the stack */
}

static int __newindex (lv_State *L) {
    LVUserDataStruct * user = (LVUserDataStruct *)lv_touserdata(L, 1);
    if( user ){
        if( lv_type(L, 2)==LV_TNUMBER ){
            return setValue(L);
        } else if( lv_type(L, 3)==LV_TSTRING ){
            
        }
    }
    return 0; /* new userdatum is already on the stack */
}

+(int) classDefine:(lv_State *)L {
    {
        lv_pushcfunction(L, lvNewStruct);
        lv_setglobal(L, "Struct");
        lv_pushcfunction(L, lvNewStruct);
        lv_setglobal(L, "Rect");
        lv_pushcfunction(L, lvNewStruct);
        lv_setglobal(L, "Size");
        lv_pushcfunction(L, lvNewStruct);
        lv_setglobal(L, "Point");
    }
    const struct lvL_reg memberFunctions [] = {
        {"__index", __index },
        {"__newindex", __newindex },
        {"__eq", __eq},
        
        {"set", setValue},
        {"get", getValue},

        {"__tostring", __tostring},
        
        {NULL, NULL}
    };
    
    lv_createClassMetaTable(L ,META_TABLE_Struct);
    
    lvL_openlib(L, NULL, memberFunctions, 0);
    
//    {
//        const struct lvL_reg memberFunctions2 [] = {
//            {"__index", __index },
//            {"__newindex", __newindex },
//            {NULL, NULL}
//        };
//        lv_createClassMetaTable(L ,"META_TABLE_Struct" );
//        lvL_openlib(L, NULL, memberFunctions2, 0);
//        
//        lvL_getmetatable(L, META_TABLE_Struct );
//        lvL_getmetatable(L, "META_TABLE_Struct" );
//        lv_setmetatable(L, -2);
//    }
    return 1;
}

@end
