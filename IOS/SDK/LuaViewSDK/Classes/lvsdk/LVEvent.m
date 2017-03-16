/**
  * Created by LuaView.
  * Copyright (c) 2017, Alibaba Group. All rights reserved.
  *
  * This source code is licensed under the MIT.
  * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
  */

#import "LVEvent.h"

@interface LVEvent ()
@property(nonatomic,strong) NSArray <UITouch *> * touches;
@property(nonatomic,assign) CGPoint point;
@property(nonatomic,strong) UITouch* touch;
@end

@implementation LVEvent

-(id) init:(lua_State *)l gesture:(UIGestureRecognizer*) gesture{
    self = [super init];
    if( self ){
        self.lv_luaviewCore = LV_LUASTATE_VIEW(l);
        self.gesture = gesture;
    }
    return self;
}

-(void) setEvent:(UIEvent *)event{
    _event = event;
    self.touches = event.allTouches.allObjects;
    self.touch = self.touches.firstObject;
    self.point = [self.touch locationInView:self.gesture.view];
}

-(id) lv_nativeObject{
    return self;
}

static void releaseEventUserData(LVUserDataInfo* user){
    if( user && user->object ){
        LVEvent* lvEvent = CFBridgingRelease(user->object);
        user->object = NULL;
        if( lvEvent ){
            lvEvent.lv_userData = NULL;
            lvEvent.lv_luaviewCore = nil;
            lvEvent.event = nil;
            lvEvent.touches = nil;
        }
    }
}

static int lvEventGC (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    releaseEventUserData(user);
    return 0;
}

static int lvNewEvent (lua_State *L) {
    Class c = [LVUtil upvalueClass:L defaultClass:[LVEvent class]];
    
    LVEvent* lvEvent = [[c alloc] init:L gesture:nil];
    
    {
        NEW_USERDATA(userData, Event);
        userData->object = CFBridgingRetain(lvEvent);
        lvEvent.lv_userData = userData;
        
        luaL_getmetatable(L, META_TABLE_Event );
        lua_setmetatable(L, -2);
    }
    return 1;
}

+(LVEvent*) createLuaEvent:(lua_State *)L  event:(UIEvent*) event gesture:(UIGestureRecognizer*) gesture{
    LVEvent* lvEvent = [[LVEvent alloc] init:L gesture:gesture];
    lvEvent.event = event;
    {
        NEW_USERDATA(userData, Event);
        userData->object = CFBridgingRetain(lvEvent);
        lvEvent.lv_userData = userData;
        
        luaL_getmetatable(L, META_TABLE_Event );
        lua_setmetatable(L, -2);
    }
    return lvEvent;
}

static int nativeObj (lua_State *L) {
    LVUserDataInfo * userData = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( userData ){
        LVEvent* lvEvent = (__bridge LVEvent *)(userData->object);
        if( lvEvent ){
            id object = lvEvent.event;
            lv_pushNativeObjectWithBox(L, object);
            return 1;
        }
    }
    return 0;
}

static int action (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( user ){
        LVEvent* lvEvent = (__bridge LVEvent *)(user->object);
        if( lvEvent.eventType ) {
            lua_pushnumber(L, lvEvent.eventType );
        } else {
            UIEventType type = lvEvent.event.type;
            lua_pushnumber(L, type );
        }
        return 1;
    }
    return 0;
}

static int pointer (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( user ){
        LVEvent* lvEvent = (__bridge LVEvent *)(user->object);
        CGPoint point = lvEvent.point;
        NSDictionary* dic = @{
            @"x":@(point.x),
            @"y":@(point.y)
        };
        lv_pushNativeObject(L, dic );
        return 1;
    }
    return 0;
}

static int x (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( user ){
        LVEvent* lvEvent = (__bridge LVEvent *)(user->object);
        CGPoint point = lvEvent.point;
        lua_pushnumber(L, point.x );
        return 1;
    }
    return 0;
}

static int y (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( user ){
        LVEvent* lvEvent = (__bridge LVEvent *)(user->object);
        CGPoint point = lvEvent.point;
        lua_pushnumber(L, point.y );
        return 1;
    }
    return 0;
}

static int event_id (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( user ){
        LVEvent* lvEvent = (__bridge LVEvent *)(user->object);
        lua_pushnumber(L, lvEvent.touch.timestamp );
        return 1;
    }
    return 0;
}

static int __index (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( user ){
        if ( lua_type(L, 2)==LUA_TSTRING ){
            lua_checkstack(L, 4);
            lua_settop(L, 2);
            luaL_getmetatable(L, META_TABLE_EventFunc );
            lua_pushvalue(L, 2);
            lua_gettable(L, -2);
            lua_remove(L, -2);
            lua_remove(L, -2);
            lua_CFunction cfunc = lua_tocfunction(L, -1);
            if( cfunc ) {
                lua_settop(L, 1);
                return cfunc(L);
            }
        }
    }
    return 0; /* new userdatum is already on the stack */
}

+(int) lvClassDefine:(lua_State *)L globalName:(NSString*) globalName{
    [LVUtil reg:L clas:self cfunc:lvNewEvent globalName:globalName defaultName:@"Event"];
    
    {
        const struct luaL_Reg memberFunctions [] = {
            {"__gc", lvEventGC },
            {"nativeObj", nativeObj},
            {"__index", __index },
            
            {NULL, NULL}
        };
        
        lv_createClassMetaTable(L, META_TABLE_Event);
        luaL_openlib(L, NULL, memberFunctions, 0);
    }
    {
        
        const struct luaL_Reg memberFunctions [] = {
            {"id", event_id},
            {"action", action},
            {"pointer", pointer},
            {"x", x},
            {"y", y},
            {NULL, NULL}
        };
        
        lv_createClassMetaTable(L, META_TABLE_EventFunc);
        luaL_openlib(L, NULL, memberFunctions, 0);
    }
    {
        
        NSDictionary* v = nil;
        v = @{
              @"DOWN":@(LVTouchEventType_DOWN),
              @"MOVE":@(LVTouchEventType_MOVE),
              @"OUTSIDE":@(UIEventTypeRemoteControl),
              @"PRESSES":@(UIEventTypePresses),// for IOS
              @"UP":@(LVTouchEventType_UP),
              @"CANCEL":@(LVTouchEventType_CANCEL),
              };
        [LVUtil defineGlobal:@"TouchEvent" value:v L:L];
    }
    return 0;
}

@end
