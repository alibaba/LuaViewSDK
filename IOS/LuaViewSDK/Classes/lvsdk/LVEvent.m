//
//  LVEvent.m
//  LuaViewSDK
//
//  Created by 董希成 on 2016/12/9.
//  Copyright © 2016年 dongxicheng. All rights reserved.
//

#import "LVEvent.h"

@interface LVEvent ()
@property(nonatomic,strong) NSArray <UITouch *> * touches;
@property(nonatomic,assign) CGPoint point;
@property(nonatomic,strong) UITouch* touch;
@end

@implementation LVEvent

-(id) init:(lv_State *)l{
    self = [super init];
    if( self ){
        self.lv_lview = (__bridge LView *)(l->lView);
    }
    return self;
}

-(void) setEvent:(UIEvent *)event{
    _event = event;
    self.touches = event.allTouches.allObjects;
    self.touch = self.touches.firstObject;
    self.point = [self.touch locationInView:nil];
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
            lvEvent.lv_lview = nil;
            lvEvent.event = nil;
            lvEvent.touches = nil;
        }
    }
}

static int lvEventGC (lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    releaseEventUserData(user);
    return 0;
}

static int lvNewEvent (lv_State *L) {
    Class c = [LVUtil upvalueClass:L defaultClass:[LVEvent class]];
    
    LVEvent* lvEvent = [[c alloc] init:L];
    
    {
        NEW_USERDATA(userData, Event);
        userData->object = CFBridgingRetain(lvEvent);
        lvEvent.lv_userData = userData;
        
        lvL_getmetatable(L, META_TABLE_Event );
        lv_setmetatable(L, -2);
    }
    return 1;
}

+(LVEvent*) createLuaEvent:(lv_State *)L  event:(UIEvent*) event{
    LVEvent* lvEvent = [[LVEvent alloc] init:L];
    lvEvent.event = event;
    {
        NEW_USERDATA(userData, Event);
        userData->object = CFBridgingRetain(lvEvent);
        lvEvent.lv_userData = userData;
        
        lvL_getmetatable(L, META_TABLE_Event );
        lv_setmetatable(L, -2);
    }
    return lvEvent;
}

static int nativeObj (lv_State *L) {
    LVUserDataInfo * userData = (LVUserDataInfo *)lv_touserdata(L, 1);
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

static int action (lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user ){
        LVEvent* lvEvent = (__bridge LVEvent *)(user->object);
        if( lvEvent.eventType ) {
            lv_pushnumber(L, lvEvent.eventType );
        } else {
            UIEventType type = lvEvent.event.type;
            lv_pushnumber(L, type );
        }
        return 1;
    }
    return 0;
}

static int pointer (lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user ){
        LVEvent* lvEvent = (__bridge LVEvent *)(user->object);
        CGPoint point = lvEvent.point;
        lv_pushnumber(L, point.x );
        lv_pushnumber(L, point.y );
        return 1;
    }
    return 0;
}

static int x (lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user ){
        LVEvent* lvEvent = (__bridge LVEvent *)(user->object);
        CGPoint point = lvEvent.point;
        lv_pushnumber(L, point.x );
        return 1;
    }
    return 0;
}

static int y (lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user ){
        LVEvent* lvEvent = (__bridge LVEvent *)(user->object);
        CGPoint point = lvEvent.point;
        lv_pushnumber(L, point.y );
        return 1;
    }
    return 0;
}

static int event_id (lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user ){
        LVEvent* lvEvent = (__bridge LVEvent *)(user->object);
        lv_pushnumber(L, lvEvent.touch.timestamp );
        return 1;
    }
    return 0;
}

+(int) lvClassDefine:(lv_State *)L globalName:(NSString*) globalName{
    [LVUtil reg:L clas:self cfunc:lvNewEvent globalName:globalName defaultName:@"Event"];
    
    const struct lvL_reg memberFunctions [] = {
        {"__gc", lvEventGC },
        {"nativeObj", nativeObj},
        
        {"id", event_id},
        {"action", action},
        {"pointer", pointer},
        {"x", x},
        {"y", y},
        
        {NULL, NULL}
    };
    
    lv_createClassMetaTable(L, META_TABLE_Event);
    lvL_openlib(L, NULL, memberFunctions, 0);
    {
        
        NSDictionary* v = nil;
        v = @{
              @"DOWN":@(UIEventTypeTouches),
              @"MOVE":@(UIEventTypeMotion),
              @"OUTSIDE":@(UIEventTypeRemoteControl),
              @"PRESSES":@(UIEventTypePresses),
              @"UP":@(LVTouchEventType_UP),
              @"CANCEL":@(LVTouchEventType_CANCEL),
              };
        [LVUtil defineGlobal:@"TouchEvent" value:v L:L];
    }
    return 0;
}

@end
