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
#import "LVHeads.h"

@interface LVTimer ()
@property(nonatomic,assign) BOOL repeat;
@property(nonatomic,assign) NSTimeInterval delay;
@property(nonatomic,assign) NSTimeInterval interval;
@end

@implementation LVTimer{
    NSTimer* timer;
}

static void releaseUserDataTimer(LVUserDataInfo* user){
    if( user && user->object ){
        LVTimer* timer = CFBridgingRelease(user->object);
        user->object = NULL;
        if( timer ){
            [timer cancel];
            timer.lv_userData = nil;
            timer.lv_lview = nil;
        }
    }
}

-(void) dealloc{
    releaseUserDataTimer(_lv_userData);
}

-(id) init:(lua_State*) l{
    self = [super init];
    if( self ){
        self.lv_lview = (__bridge LView *)(l->lView);
        self.delay = 0;     // 默认延时
        self.repeat = NO;   // 默认重复次数
        self.interval = 1;  // 默认间隔1秒
    }
    return self;
}

-(void) timerCallBack{
    lua_State* l = self.lv_lview.l;
    if( l && self.lv_userData ){
        lv_pushUserdata(l, self.lv_userData);
        lv_pushUDataRef(l, USERDATA_KEY_DELEGATE );
        lv_runFunction(l);
    }
}


-(void) startTimer{
    [self cancel];
    if( self.delay>0 ) {
        NSDate* date = [[NSDate alloc] initWithTimeIntervalSinceNow:self.delay];
        timer = [[NSTimer alloc] initWithFireDate:date interval:self.interval target:self selector:@selector(timerCallBack) userInfo:nil repeats:self.repeat];
        [[NSRunLoop currentRunLoop] addTimer:timer forMode:NSRunLoopCommonModes];
    } else {
        timer = [NSTimer scheduledTimerWithTimeInterval:self.interval target:self selector:@selector(timerCallBack) userInfo:nil repeats:self.repeat];
        [[NSRunLoop currentRunLoop] addTimer:timer forMode:NSRunLoopCommonModes];
    }
}

-(void) cancel {
    [timer invalidate];
    timer = nil;
}

-(id) lv_nativeObject{
    return timer;
}


#pragma -mark Timer

static int lvNewTimer (lua_State *L) {
    Class c = [LVUtil upvalueClass:L defaultClass:[LVTimer class]];
    
    LVTimer* timer = [[c alloc] init:L];
    {
        NEW_USERDATA(userData, Timer);
        userData->object = CFBridgingRetain(timer);
        timer.lv_userData = userData;
        
        lvL_getmetatable(L, META_TABLE_Timer );
        lv_setmetatable(L, -2);
    }
    if( lv_type(L, 1) == LUA_TFUNCTION ) {
        lv_pushvalue(L, 1);
        lv_udataRef(L, USERDATA_KEY_DELEGATE);
    }
    return 1;
}

static int setCallback (lua_State *L) {
    if( lv_type(L, 2) == LUA_TFUNCTION ) {
        lv_pushvalue(L, 1);
        lv_pushvalue(L, 2);
        lv_udataRef(L, USERDATA_KEY_DELEGATE);
    }
    lv_settop(L, 1);
    return 1;
}

static int start (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    LVTimer* timer = (__bridge LVTimer *)(user->object);
    if( lv_gettop(L)>=2 ) {
        timer.interval = lua_tonumber(L, 2);
    }
    if( lv_gettop(L)>=3 ) {
        timer.repeat = lv_toboolean(L, 3);
    }
    if( user ){
        if( timer ){
            [timer startTimer];
            lv_pushvalue(L,1);
            return 1;
        }
    }
    return 0;
}

static int cancel (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user ){
        LVTimer* timer = (__bridge LVTimer *)(user->object);
        if( timer ){
            [timer cancel];
        }
    }
    return 0;
}

static int delay (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    double delay = lua_tonumber(L, 2);
    if( user ){
        LVTimer* timer = (__bridge LVTimer *)(user->object);
        if( timer ){
            timer.delay = delay;
            lv_pushvalue(L,1);
            return 1;
        }
    }
    return 0;
}

static int repeat (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    BOOL repeat = lv_toboolean(L, 2);
    if( user ){
        LVTimer* timer = (__bridge LVTimer *)(user->object);
        if( timer ){
            timer.repeat = repeat;
            lv_pushvalue(L,1);
            return 1;
        }
    }
    return 0;
}

static int interval (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    double interval = lua_tonumber(L, 2);
    if( user ){
        LVTimer* timer = (__bridge LVTimer *)(user->object);
        if( timer ){
            timer.interval = interval;
            lv_pushvalue(L,1);
            return 1;
        }
    }
    return 0;
}

static int __gc (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    releaseUserDataTimer(user);
    return 0;
}

static int __tostring (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user ){
        LVTimer* timer =  (__bridge LVTimer *)(user->object);
        NSString* s = [NSString stringWithFormat:@"LVUserDataTimer: %@", timer ];
        lua_pushstring(L, s.UTF8String);
        return 1;
    }
    return 0;
}

+(int) lvClassDefine:(lua_State *)L globalName:(NSString*) globalName{
    [LVUtil reg:L clas:self cfunc:lvNewTimer globalName:globalName defaultName:@"Timer"];
    
    const struct luaL_Reg memberFunctions [] = {
        {"callback",setCallback},
        
        {"start", start },
        {"cancel", cancel },
        {"stop", cancel },
        
        
        {"delay", delay },
        {"repeat", repeat },
        {"repeatCount", repeat },
        {"interval", interval },
        
        {"__gc", __gc },
        
        {"__tostring", __tostring },
        {NULL, NULL}
    };
    
    lv_createClassMetaTable(L ,META_TABLE_Timer);
    
    lvL_openlib(L, NULL, memberFunctions, 0);
    return 1;
}


@end
