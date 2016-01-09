//
//  LVDate.m
//  LVSDK
//
//  Created by dongxicheng on 1/13/15.
//  Copyright (c) 2015 dongxicheng. All rights reserved.
//

#import "LVHeads.h"
#import "LVDate.h"
#import "lV.h"
#import "lVauxlib.h"
#import "lVlib.h"
#import "lVstate.h"
#import "lVgc.h"



@implementation LVDate

-(id) init:(NSDate*) d{
    self = [super init];
    if( self ) {
        self.date = d;
    }
    return self;
}

-(id) lv_nativeObject{
    return self.date;
}

static NSString* string09(NSString* s, NSUInteger len){
    NSMutableString* buf = [[NSMutableString alloc] init];
    for( int i=0; i<s.length && buf.length<len; i++ ){
        int c = [s characterAtIndex:i];
        if( '0'<=c && c<='9' ){
            [buf appendFormat:@"%c",c];
        }
    }
    for( ;buf.length<len ; ){
        [buf appendString:@"0"];
    }
    return buf;
}

static int lvNewDate (lv_State *L) {
    {
        NSString* string = lv_paramString(L, 1);
        NSString* format = lv_paramString(L, 2);
        
        NSDate* date = nil;
        if( string==nil && format==nil ){
            if( lv_type(L, 1)==LV_TNUMBER ){
                double time = lv_tonumber(L, 1);
                date = [[NSDate alloc] initWithTimeIntervalSince1970:time];
            } else {
                date = [NSDate date];
            }
        } else if( string && format ){
            NSDateFormatter *dateformatter=[[NSDateFormatter alloc] init];
            [dateformatter setDateFormat:format];
            date = [dateformatter dateFromString:string];
        } else if( string ){
            format = @"yyyyMMddHHmmss";
            string = string09(string, format.length);
            NSDateFormatter *dateformatter=[[NSDateFormatter alloc] init];
            [dateformatter setDateFormat:format];
            date = [dateformatter dateFromString:string];
        }
        {
            LVDate* d = [[LVDate alloc] init:date];
            NEW_USERDATA(userData, Date );
            userData->object = CFBridgingRetain(d);
            
            lvL_getmetatable(L, META_TABLE_Date );
            lv_setmetatable(L, -2);
        }
    }
    return 1; /* new userdatum is already on the stack */
}


static int __GC (lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( LVIsType(user, Date) && user->object ){
        CFBridgingRelease(user->object);
        user->object = NULL;
    }
    return 0;
}

static int __tostring (lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( LVIsType(user, Date) ){
        LVDate* date =  (__bridge LVDate *)(user->object);
        NSString* s = [NSString stringWithFormat:@"%@", date.date ];
        lv_pushstring(L, s.UTF8String);
        return 1;
    }
    return 0;
}

static int format (lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    NSString* formatString = lv_paramString(L, 2);
    if( LVIsType(user, Date) ){
        LVDate* date =  (__bridge LVDate *)(user->object);
        if( date ) {
            NSString* ret = nil;
            if( formatString ){
                NSDateFormatter *formatter = [[NSDateFormatter alloc] init];
                [formatter setDateFormat:formatString];
                ret = [formatter stringFromDate:date.date];
            } else {
                ret = [NSString stringWithFormat:@"%@", date.date ];
            }
            lv_pushstring(L, ret.UTF8String);
            return 1;
        }
    }
    return 0;
}

static int __sub (lv_State *L) {
    LVUserDataInfo * user1 = (LVUserDataInfo *)lv_touserdata(L, 1);
    LVUserDataInfo * user2 = (LVUserDataInfo *)lv_touserdata(L, 2);
    if( LVIsType(user1, Date) && LVIsType(user2, Date) ){
        LVDate* date1 = (__bridge LVDate *)(user1->object);
        LVDate* date2 = (__bridge LVDate *)(user2->object);
        double time = [date1.date timeIntervalSinceDate:date2.date];
        lv_pushnumber(L, time);
        return 1;
    }
    return 0;
}

static int __eq (lv_State *L) {
    LVUserDataInfo * user1 = (LVUserDataInfo *)lv_touserdata(L, 1);
    LVUserDataInfo * user2 = (LVUserDataInfo *)lv_touserdata(L, 2);
    if( LVIsType(user1, Date) && LVIsType(user2, Date) ){
        LVDate* date1 = (__bridge LVDate *)(user1->object);
        LVDate* date2 = (__bridge LVDate *)(user2->object);
        BOOL yes = [date1.date isEqualToDate:date2.date];
        lv_pushboolean(L, (yes?1:0) );
        return 1;
    }
    return 0;
}

static int timeInterval (lv_State *L) {
    LVUserDataInfo * user1 = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( LVIsType(user1, Date) ){
        LVDate* date1 = (__bridge LVDate *)(user1->object);
        double time = [date1.date timeIntervalSince1970];
        lv_pushnumber(L, time);
        return 1;
    }
    return 0;
}

+(int) classDefine:(lv_State *)L {
    {
        lv_pushcfunction(L, lvNewDate);
        lv_setglobal(L, "Date");
    }
    const struct lvL_reg memberFunctions [] = {
        {"__gc",  __GC },
        {"__sub", __sub},
        {"__eq",  __eq},
        {"timeInterval",  timeInterval },
        
        {"__tostring",  __tostring },
        {"format",      format },
        {NULL, NULL}
    };
    
    lv_createClassMetaTable(L, META_TABLE_Date);
    
    lvL_openlib(L, NULL, memberFunctions, 0);
    return 1;
}


@end



