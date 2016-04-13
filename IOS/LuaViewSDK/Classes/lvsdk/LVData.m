//
//  LVData.m
//  LVSDK
//
//  Created by dongxicheng on 2/6/15.
//  Copyright (c) 2015 dongxicheng. All rights reserved.
//

#import "LVData.h"
#import "lV.h"
#import "lVauxlib.h"
#import "lVlib.h"
#import "lVstate.h"
#import "lVgc.h"

@interface LVData ()
@end

@implementation LVData

-(id) init:(lv_State *)l{
    self = [super init];
    if( self ){
        self.lv_lview = (__bridge LView *)(l->lView);
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
            data.lv_lview = nil;
            data.data = nil;
        }
    }
}

static int lvDataGC (lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    releaseUserDataData(user);
    return 0;
}

static int lvNewData (lv_State *L) {
    LVData* data = [[LVData alloc] init:L];
    int argN = lv_gettop(L);
    if( argN>0 ) {
        if ( lv_type(L, 1)==LV_TSTRING ) {// 支持字符串转 NSData
            NSString* s = lv_paramString(L, 1);
            const char* chars = s.UTF8String;
            [data.data appendBytes:chars length:strlen(chars) ];
        } else {
            int num = lv_tonumber(L, 1);
            if( num>0 ){
                [data.data setLength:num];
            }
        }
    }
    
    {
        NEW_USERDATA(userData, Data);
        userData->object = CFBridgingRetain(data);
        data.lv_userData = userData;
        
        lvL_getmetatable(L, META_TABLE_Data );
        lv_setmetatable(L, -2);
    }
    return 1;
}

+(int) createDataObject:(lv_State *)L  data:(NSData*) data{
    LVData* ldata = [[LVData alloc] init:L];
    if( data ) {
        [ldata.data setData:data];
    }
    
    {
        NEW_USERDATA(userData, Data);
        userData->object = CFBridgingRetain(ldata);
        ldata.lv_userData = userData;
        
        lvL_getmetatable(L, META_TABLE_Data );
        lv_setmetatable(L, -2);
    }
    return 1;
}

static int __tostring (lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user ){
        LVData* data =  (__bridge LVData *)(user->object);
        NSStringEncoding encode = NSUTF8StringEncoding;
        if( lv_gettop(L)>=2 && lv_type(L, 2)==LV_TNUMBER ) {
            encode = lv_tonumber(L, 2);
        }
        NSString* s = [[NSString alloc] initWithData:data.data encoding:encode];
        if( s==nil ){
            NSStringEncoding gbkEncoding = CFStringConvertEncodingToNSStringEncoding(kCFStringEncodingGB_18030_2000);
            s = [[NSString alloc] initWithData:data.data encoding:gbkEncoding];
            if ( s==nil ) {
                s = [[NSString alloc] initWithFormat:@"{ UserDataType=data, length=%ld }",(long)data.data.length];
            }
        }
        lv_pushstring(L, s.UTF8String);
        return 1;
    }
    return 0;
}

static int __index (lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    LVData* lvData = (__bridge LVData *)(user->object);
    NSMutableData* data = lvData.data;
    if( lvData && lvData.data){
        if( lv_type(L, 2)==LV_TNUMBER ){
            int index = lv_tonumber(L, 2);
            if( index>0 && index<data.length ){
                char cs[8] = {0};
                NSRange range;
                range.length = 1;
                range.location = index;
                [data getBytes:cs range:range];
                lv_pushnumber(L, cs[0] );
                return 1;
            }
        } else if( lv_type(L, 2)==LV_TSTRING ){
            NSString* key = lv_paramString(L, 2);
            if( [@"length" isEqualToString:key] ){
                lv_pushnumber(L, data.length );
                return 1;
            }
        } else {
            
        }
    }
    return 0; /* new userdatum is already on the stack */
}

static int __newindex (lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    LVData* lvData = (__bridge LVData *)(user->object);
    NSMutableData* data = lvData.data;
    if( lvData && lvData.data){
        if( lv_type(L, 2)==LV_TNUMBER ){
            int index = lv_tonumber(L, 2);
            int value = lv_tonumber(L, 3);
            if( index>0 && index<data.length ){
                char cs[8] = {0};
                cs[0] = value;
                NSRange range;
                range.length = 1;
                range.location = index;
                [data replaceBytesInRange:range withBytes:cs ];
                return 0;
            }
        } else if( lv_type(L, 2)==LV_TSTRING ){
            NSString* key = lv_paramString(L, 2);
            int value = lv_tonumber(L, 3);
            if( [@"length" isEqualToString:key] ){
                data.length = value;
                return 0;
            }
        }
    }
    return 0; /* new userdatum is already on the stack */
}

static int __add (lv_State *L) {
    LVUserDataInfo * user1 = (LVUserDataInfo *)lv_touserdata(L, 1);
    LVUserDataInfo * user2 = (LVUserDataInfo *)lv_touserdata(L, 2);
    LVData* lvData1 = (__bridge LVData *)(user1->object);
    LVData* lvData2 = (__bridge LVData *)(user2->object);
    if( LVIsType(user1, Data) && LVIsType(user2, Data) && lvData1.data && lvData2.data ){
        [lvData1.data appendData:lvData2.data];
        return 1;
    }
    return 0;
}

+(int) classDefine:(lv_State *)L {
    {
        lv_pushcfunction(L, lvNewData);
        lv_setglobal(L, "Data");
    }
    const struct lvL_reg memberFunctions [] = {
        {"__index", __index },
        {"__newindex", __newindex },
        
        {"__add", __add },
        
        {"__gc", lvDataGC },
        
        {"__tostring", __tostring },
        {NULL, NULL}
    };
    
    lv_createClassMetaTable(L, META_TABLE_Data);
    
    lvL_openlib(L, NULL, memberFunctions, 0);
    return 0;
}


@end
