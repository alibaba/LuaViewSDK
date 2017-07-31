/**
  * Created by LuaView.
  * Copyright (c) 2017, Alibaba Group. All rights reserved.
  *
  * This source code is licensed under the MIT.
  * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
  */

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
            timer.lv_luaviewCore = nil;
        }
    }
}

-(void) dealloc{
    releaseUserDataTimer(_lv_userData);
}

-(id) init:(lua_State*) l{
    self = [super init];
    if( self ){
        self.lv_luaviewCore = LV_LUASTATE_VIEW(l);
        self.delay = 0;     // 默认延时
        self.repeat = NO;   // 默认重复次数
        self.interval = 1;  // 默认间隔1秒
    }
    return self;
}

-(void) timerCallBack{
    lua_State* l = self.lv_luaviewCore.l;
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
        [[NSRunLoop mainRunLoop] addTimer:timer forMode:NSRunLoopCommonModes];
    } else {
        timer = [NSTimer scheduledTimerWithTimeInterval:self.interval target:self selector:@selector(timerCallBack) userInfo:nil repeats:self.repeat];
        [[NSRunLoop mainRunLoop] addTimer:timer forMode:NSRunLoopCommonModes];
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
        
        luaL_getmetatable(L, META_TABLE_Timer );
        lua_setmetatable(L, -2);
    }
    if( lua_type(L, 1) == LUA_TFUNCTION ) {
        lua_pushvalue(L, 1);
        lv_udataRef(L, USERDATA_KEY_DELEGATE);
    }
    return 1;
}

static int setCallback (lua_State *L) {
    if( lua_type(L, 2) == LUA_TFUNCTION ) {
        lua_pushvalue(L, 1);
        lua_pushvalue(L, 2);
        lv_udataRef(L, USERDATA_KEY_DELEGATE);
    }
    lua_settop(L, 1);
    return 1;
}

static int start (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    
    if ( user ) {
        LVTimer* timer = (__bridge LVTimer *)(user->object);
        if( lua_gettop(L)>=2 ) {
            timer.interval = lua_tonumber(L, 2);
        }
        if( lua_gettop(L)>=3 ) {
            timer.repeat = lua_toboolean(L, 3);
        }
        if( timer ){
            [timer startTimer];
            lua_pushvalue(L,1);
            return 1;
        }
    }
    return 0;
}

static int cancel (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( user ){
        LVTimer* timer = (__bridge LVTimer *)(user->object);
        if( timer ){
            [timer cancel];
        }
    }
    return 0;
}

static int delay (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    double delay = lua_tonumber(L, 2);
    if( user ){
        LVTimer* timer = (__bridge LVTimer *)(user->object);
        if( timer ){
            timer.delay = delay;
            lua_pushvalue(L,1);
            return 1;
        }
    }
    return 0;
}

static int repeat (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    BOOL repeat = lua_toboolean(L, 2);
    if( user ){
        LVTimer* timer = (__bridge LVTimer *)(user->object);
        if( timer ){
            timer.repeat = repeat;
            lua_pushvalue(L,1);
            return 1;
        }
    }
    return 0;
}

static int interval (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    double interval = lua_tonumber(L, 2);
    if( user ){
        LVTimer* timer = (__bridge LVTimer *)(user->object);
        if( timer ){
            timer.interval = interval;
            lua_pushvalue(L,1);
            return 1;
        }
    }
    return 0;
}

static int __gc (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    releaseUserDataTimer(user);
    return 0;
}

static int __tostring (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
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
        {"stop", cancel }, //__deprecated_msg("Use hidden")
        
        
        {"delay", delay },
        {"repeat", repeat },
        {"repeatCount", repeat }, //__deprecated_msg("Use hidden")
        {"interval", interval },
        
        {"__gc", __gc },
        
        {"__tostring", __tostring },
        {NULL, NULL}
    };
    
    lv_createClassMetaTable(L ,META_TABLE_Timer);
    
    luaL_openlib(L, NULL, memberFunctions, 0);
    return 1;
}


@end
