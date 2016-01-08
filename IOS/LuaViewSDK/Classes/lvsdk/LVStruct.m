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

static int lvNewStruct (lv_State *L) {
    NEW_USERDATA(userData, Struct);
    LVStruct* lvstruct = [[LVStruct alloc] init];
    userData->object = CFBridgingRetain(lvstruct);
    lvstruct.lv_userData = userData;
    
    int num = lv_gettop(L);
    
    for (int i=1,index=0; (i<=num) && (index<LV_STRUCT_MAX_LEN); i++ ) {
        CGFloat value = lv_tonumber(L, i);
        [lvstruct setIndex:index++ byValue:value];
    }
    
    lvL_getmetatable(L, META_TABLE_Struct );
    lv_setmetatable(L, -2);
    return 1; /* new userdatum is already on the stack */
}

+(int) pushStructToLua:(lv_State*)L data:(void*)data{
    NEW_USERDATA(userData, Struct);
    LVStruct* lvstruct = [[LVStruct alloc] init];
    userData->object = CFBridgingRetain(lvstruct);
    lvstruct.lv_userData = userData;
    
    memcpy( [lvstruct dataPointer], data, LV_STRUCT_MAX_LEN*sizeof(CGFloat) );
    lvL_getmetatable(L, META_TABLE_Struct );
    lv_setmetatable(L, -2);
    return 1;
}


static int setValue (lv_State *L) {
    int argNum = lv_gettop(L);
    if( argNum>=3 ) {
        LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
        unsigned int index = lv_tonumber(L, 2);
        CGFloat value = lv_tonumber(L, 3);
        if ( index>=LV_STRUCT_MAX_LEN ) {
            LVError(@"LVStruct.set index:%d", index );
            return 0;
        }
        if( LVIsType(user, Struct) ){
            LVStruct* lvstruct = (__bridge LVStruct *)(user->object);
            if ( argNum>=4 ) {
                int type = lv_tonumber(L, 4);
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

static int getValue (lv_State *L) {
    int argNum = lv_gettop(L);
    if( argNum>=2 ) {
        LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
        unsigned int index = lv_tonumber(L, 2);
        if ( index>=LV_STRUCT_MAX_LEN ) {
            LVError(@"LVStruct.get index:%d", index );
            return 0;
        }
        if( LVIsType(user, Struct) ){
            LVStruct* stru = (__bridge LVStruct *)(user->object);
            if ( argNum>=3 ) {
                int type = lv_tonumber(L, 3);
                CGFloat value = lv_getValueWithType(  [stru dataPointer], index, type);
                lv_pushnumber(L, value);
                return 1;
            } else {
                CGFloat value = [stru getValueByIndex:index];
                lv_pushnumber(L, value);
                return 1;
            }
        }
    }
    return 0;
}

static int __eq (lv_State *L) {
    if( lv_gettop(L)==2 ) {
        LVUserDataInfo * user1 = (LVUserDataInfo *)lv_touserdata(L, 1);
        LVUserDataInfo * user2 = (LVUserDataInfo *)lv_touserdata(L, 2);
        if( LVIsType(user1, Struct) && LVIsType(user2, Struct) ){
            LVStruct* s1 = (__bridge LVStruct *)(user1->object);
            LVStruct* s2 = (__bridge LVStruct *)(user2->object);
            int size = LV_STRUCT_MAX_LEN;
            BOOL yes = NO;
            if( [s1 dataPointer] && [s2 dataPointer] ) {
                yes = !memcmp( [s1 dataPointer], [s2 dataPointer], size);
            }
            lv_pushboolean(L, (yes?1:0) );
            return 1;
        }
    }
    return 0;
}

static int __tostring (lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( LVIsType(user, Struct) ){
        NSString* s = [NSString stringWithFormat:@"LVUserDataStruct: %d", (int)user ];
        lv_pushstring(L, s.UTF8String);
        return 1;
    }
    return 0;
}

static int __index (lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
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
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
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
