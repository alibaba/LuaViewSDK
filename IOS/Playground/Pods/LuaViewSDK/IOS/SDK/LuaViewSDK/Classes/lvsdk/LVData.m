/**
  * Created by LuaView.
  * Copyright (c) 2017, Alibaba Group. All rights reserved.
  *
  * This source code is licensed under the MIT.
  * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
  */

#import "LVData.h"
#import "LVHeads.h"

@interface LVData ()
@end

@implementation LVData

-(id) init:(lua_State *)l{
    self = [super init];
    if( self ){
        self.lv_luaviewCore = LV_LUASTATE_VIEW(l);
        self.data = [[NSMutableData alloc] init];
    }
    return self;
}

-(id) lv_nativeObject{
    return self.data;
}

static void releaseUserDataData(LVUserDataInfo* user){
    if( user && user->object ){
        LVData* data = CFBridgingRelease(user->object);
        user->object = NULL;
        if( data ){
            data.lv_userData = NULL;
            data.lv_luaviewCore = nil;
            data.data = nil;
        }
    }
}

static int lvDataGC (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    releaseUserDataData(user);
    return 0;
}

static int lvNewData (lua_State *L) {
    Class c = [LVUtil upvalueClass:L defaultClass:[LVData class]];
    
    LVData* data = [[c alloc] init:L];
    int argN = lua_gettop(L);
    if( argN>0 ) {
        if ( lua_type(L, 1)==LUA_TSTRING ) {// 支持字符串转 NSData
            NSString* s = lv_paramString(L, 1);
            const char* chars = s.UTF8String;
            [data.data appendBytes:chars length:strlen(chars) ];
        } else {
            int num = lua_tonumber(L, 1);
            if( num>0 ){
                [data.data setLength:num];
            }
        }
    }
    
    {
        NEW_USERDATA(userData, Data);
        userData->object = CFBridgingRetain(data);
        data.lv_userData = userData;
        
        luaL_getmetatable(L, META_TABLE_Data );
        lua_setmetatable(L, -2);
    }
    return 1;
}

+(int) createDataObject:(lua_State *)L  data:(NSData*) data{
    return [self createDataObject:L data1:data data2:nil];
}

+(int) createDataObject:(lua_State *)L  data1:(NSData*) data1 data2:(NSData*) data2{
    LVData* lvdata = [[LVData alloc] init:L];
    if( data1 ) {
        [lvdata.data setData:data1];
    }
    if( data2 ) {
        [lvdata.data appendData:data2];
    }
    {
        NEW_USERDATA(userData, Data);
        userData->object = CFBridgingRetain(lvdata);
        lvdata.lv_userData = userData;
        
        luaL_getmetatable(L, META_TABLE_Data );
        lua_setmetatable(L, -2);
    }
    return 1;
}

static int __tostring (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( user ){
        LVData* data =  (__bridge LVData *)(user->object);
        NSStringEncoding encode = NSUTF8StringEncoding;
        if( lua_gettop(L)>=2 && lua_type(L, 2)==LUA_TNUMBER ) {
            encode = lua_tonumber(L, 2);
        }
        NSString* s = [[NSString alloc] initWithData:data.data encoding:encode];
        if( s==nil ){
            NSStringEncoding gbkEncoding = CFStringConvertEncodingToNSStringEncoding(kCFStringEncodingGB_18030_2000);
            s = [[NSString alloc] initWithData:data.data encoding:gbkEncoding];
            if ( s==nil ) {
                s = [[NSString alloc] initWithFormat:@"{ UserDataType=data, length=%ld }",(long)data.data.length];
            }
        }
        lua_pushstring(L, s.UTF8String);
        return 1;
    }
    return 0;
}

static int __index (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    LVData* lvData = (__bridge LVData *)(user->object);
    NSMutableData* data = lvData.data;
    if( lvData && lvData.data){
        if( lua_type(L, 2)==LUA_TNUMBER ){
            int index = lua_tonumber(L, 2)-1;
            if( index>=0 && index<data.length ){
                char cs[8] = {0};
                NSRange range;
                range.length = 1;
                range.location = index;
                [data getBytes:cs range:range];
                lua_pushnumber(L, cs[0] );
                return 1;
            }
        } else if( lua_type(L, 2)==LUA_TSTRING ){
            NSString* key = lv_paramString(L, 2);
            if( [@"length" isEqualToString:key] ){
                lua_pushnumber(L, data.length );
                return 1;
            }
        } else {
            
        }
    }
    return 0; /* new userdatum is already on the stack */
}

static int __newindex (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    LVData* lvData = (__bridge LVData *)(user->object);
    NSMutableData* data = lvData.data;
    if( lvData && lvData.data){
        if( lua_type(L, 2)==LUA_TNUMBER ){
            int index = lua_tonumber(L, 2)-1;
            int value = lua_tonumber(L, 3);
            if( index>=0 && index<data.length ){
                char cs[8] = {0};
                cs[0] = value;
                NSRange range;
                range.length = 1;
                range.location = index;
                [data replaceBytesInRange:range withBytes:cs ];
                return 0;
            }
        } else if( lua_type(L, 2)==LUA_TSTRING ){
            NSString* key = lv_paramString(L, 2);
            int value = lua_tonumber(L, 3);
            if( [@"length" isEqualToString:key] ){
                data.length = value;
                return 0;
            }
        }
    }
    return 0; /* new userdatum is already on the stack */
}

static int __add (lua_State *L) {
    LVUserDataInfo * user1 = (LVUserDataInfo *)lua_touserdata(L, 1);
    LVUserDataInfo * user2 = (LVUserDataInfo *)lua_touserdata(L, 2);
    LVData* lvData1 = (__bridge LVData *)(user1->object);
    LVData* lvData2 = (__bridge LVData *)(user2->object);
    if( LVIsType(user1, Data) && LVIsType(user2, Data) && lvData1.data && lvData2.data ){
        [LVData createDataObject:L data1:lvData1.data data2:lvData2.data];
        return 1;
    }
    return 0;
}

+(int) lvClassDefine:(lua_State *)L globalName:(NSString*) globalName{
    [LVUtil reg:L clas:self cfunc:lvNewData globalName:globalName defaultName:@"Data"];
    
    const struct luaL_Reg memberFunctions [] = {
        // toJson 判断是否json
        // toString 判断是否字符串
        // toTable
        // append
        
        {"__index", __index },
        {"__newindex", __newindex },
        
        {"__add", __add },
        
        {"__gc", lvDataGC },
        
        {"__tostring", __tostring },
        {NULL, NULL}
    };
    
    lv_createClassMetaTable(L, META_TABLE_Data);
    
    luaL_openlib(L, NULL, memberFunctions, 0);
    return 0;
}


@end
