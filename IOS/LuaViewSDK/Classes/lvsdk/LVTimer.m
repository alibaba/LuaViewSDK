//
//  LvTimer.m
//  lv5.1.4
//
//  Created by dongxicheng on 12/18/14.
//  Copyright (c) 2014 dongxicheng. All rights reserved.
//

#import "LVTimer.h"
#import "LView.h"
#import "LVHeads.h"


@implementation LVTimer{
    NSTimer* timer;
}

static void releaseUserDataTimer(LVUserDataTimer* user){
    if( user && user->timer ){
        LVTimer* timer = CFBridgingRelease(user->timer);
        user->timer = NULL;
        if( timer ){
            [timer cancel];
            timer.userData = nil;
            timer.lview = nil;
        }
    }
}

-(void) dealloc{
    releaseUserDataTimer(_userData);
}

-(id) init:(lv_State*) l{
    self = [super init];
    if( self ){
        self.lview = (__bridge LView *)(l->lView);
    }
    return self;
}

-(void) timerCallBack{
    lv_State* l = self.lview.l;
    if( l && self.userData ){
        lv_pushUserdata(l, self.userData);
        lv_pushUDataRef(l, USERDATA_KEY_DELEGATE );
        lv_runFunction(l);
    }
}


-(void) startTimer:(NSTimeInterval) timeInterval repeat:(BOOL) repeat {
    [self cancel];
    timer = [NSTimer scheduledTimerWithTimeInterval:timeInterval target:self selector:@selector(timerCallBack) userInfo:nil repeats:repeat];
}

-(void) cancel {
    [timer invalidate];
    timer = nil;
}


#pragma -mark Timer

static int lvNewTimer (lv_State *L) {
    LVTimer* timer = [[LVTimer alloc] init:L];
    {
        NEW_USERDATA(userData, LVUserDataTimer);
        userData->timer = CFBridgingRetain(timer);
        timer.userData = userData;
        
        lvL_getmetatable(L, META_TABLE_Timer );
        lv_setmetatable(L, -2);
    }
    if( lv_type(L, 1) == LV_TFUNCTION ) {
        lv_pushvalue(L, 1);
        lv_udataRef(L, USERDATA_KEY_DELEGATE);
    }
    return 1;
}

static int setCallback (lv_State *L) {
    if( lv_type(L, 2) == LV_TFUNCTION ) {
        lv_pushvalue(L, 1);
        lv_pushvalue(L, 2);
        lv_udataRef(L, USERDATA_KEY_DELEGATE);
    }
    lv_settop(L, 1);
    return 1;
}

static int start (lv_State *L) {
    LVUserDataTimer * user = (LVUserDataTimer *)lv_touserdata(L, 1);
    double time = lv_tonumber(L, 2);
    BOOL repeat = NO;
    if( lv_gettop(L)>=3 ) {
        repeat = lv_toboolean(L, 3);
    }
    if( user ){
        LVTimer* timer = (__bridge LVTimer *)(user->timer);
        if( timer ){
            [timer startTimer:time repeat:repeat];
            lv_pushvalue(L,1);
            return 1;
        }
    }
    return 0;
}

static int cancel (lv_State *L) {
    LVUserDataTimer * user = (LVUserDataTimer *)lv_touserdata(L, 1);
    if( user ){
        LVTimer* timer = (__bridge LVTimer *)(user->timer);
        if( timer ){
            [timer cancel];
        }
    }
    return 0;
}

static int __gc (lv_State *L) {
    LVUserDataTimer * user = (LVUserDataTimer *)lv_touserdata(L, 1);
    releaseUserDataTimer(user);
    return 0;
}

static int __tostring (lv_State *L) {
    LVUserDataTimer * user = (LVUserDataTimer *)lv_touserdata(L, 1);
    if( user ){
        LVTimer* timer =  (__bridge LVTimer *)(user->timer);
        NSString* s = [NSString stringWithFormat:@"LVUserDataTimer: %@", timer ];
        lv_pushstring(L, s.UTF8String);
        return 1;
    }
    return 0;
}

+(int) classDefine:(lv_State *)L {
    {
        lv_pushcfunction(L, lvNewTimer);
        lv_setglobal(L, "Timer");
    }
    const struct lvL_reg memberFunctions [] = {
        {"setCallback",setCallback},
        {"callback",setCallback},
        
        {"start", start },
        {"cancel", cancel },
        {"stop", cancel },
        
        {"__gc", __gc },
        
        {"__tostring", __tostring },
        {NULL, NULL}
    };
    
    lv_createClassMetaTable(L ,META_TABLE_Timer);
    
    lvL_openlib(L, NULL, memberFunctions, 0);
    return 1;
}


@end
