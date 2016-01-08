//
//  LVGestureRecognizer.m
//  LVSDK
//
//  Created by dongxicheng on 1/22/15.
//  Copyright (c) 2015 dongxicheng. All rights reserved.
//

#import "LVGestureRecognizer.h"
#import "lV.h"
#import "lVauxlib.h"
#import "lVlib.h"
#import "lVstate.h"
#import "lVgc.h"

@implementation LVGestureRecognizer


static void releaseUserData(LVUserDataInfo * user){
    if( user && user->object ){
        UIGestureRecognizer<LVProtocal>* gesture = CFBridgingRelease(user->object);
        user->object = NULL;
        if( gesture ){
            gesture.lv_lview = nil;
            gesture.lv_userData = NULL;
        }
    }
}

static int __GC (lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    releaseUserData(user);
    return 0;
}


static int __tostring (lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user ){
        UIGestureRecognizer* gesture =  (__bridge UIGestureRecognizer *)(user->object);
        NSString* s = [NSString stringWithFormat:@"LVUserDataGesture: %@", gesture ];
        lv_pushstring(L, s.UTF8String);
        return 1;
    }
    return 0;
}

static int location (lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user ){
        UIGestureRecognizer* gesture =  (__bridge UIGestureRecognizer *)(user->object);
        CGPoint p = [gesture locationInView:gesture.view];
        lv_pushnumber(L, p.x);
        lv_pushnumber(L, p.y);
        return 2;
    }
    return 0;
}

static int state (lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user ){
        UIGestureRecognizer* gesture =  (__bridge UIGestureRecognizer *)(user->object);
        NSInteger state = gesture.state;
        lv_pushnumber(L, state);
        return 1;
    }
    return 0;
}

static const struct lvL_reg baseMemberFunctions [] = {
    {"location", location },
    {"state", state },
    {"__gc", __GC },
    {"__tostring", __tostring },
    {NULL, NULL}
};

+(const lvL_reg*) baseMemberFunctions{
    return baseMemberFunctions;
}
+(void) releaseUD:(LVUserDataInfo *) user{
    releaseUserData(user);
}
+(int) classDefine:(lv_State *)L {
    //    typedef NS_ENUM(NSInteger, UIGestureRecognizerState) {
    //        UIGestureRecognizerStatePossible,
    //        UIGestureRecognizerStateBegan,
    //        UIGestureRecognizerStateChanged,
    //        UIGestureRecognizerStateEnded,
    //        UIGestureRecognizerStateCancelled,
    //        UIGestureRecognizerStateFailed
    //    };
    lv_settop(L, 0);
    const struct lvL_reg lib [] = {
        {NULL, NULL}
    };
    lvL_register(L, "GestureState", lib);
    
    lv_pushnumber(L, UIGestureRecognizerStatePossible);
    lv_setfield(L, -2, "POSSIBLE");
    
    lv_pushnumber(L, UIGestureRecognizerStateBegan);
    lv_setfield(L, -2, "BEGIN");
    
    lv_pushnumber(L, UIGestureRecognizerStateChanged);
    lv_setfield(L, -2, "CHANGED");
    
    lv_pushnumber(L, UIGestureRecognizerStateEnded);
    lv_setfield(L, -2, "END");
    
    lv_pushnumber(L, UIGestureRecognizerStateCancelled);
    lv_setfield(L, -2, "CANCEL");
    
    lv_pushnumber(L, UIGestureRecognizerStateFailed);
    lv_setfield(L, -2, "FAILED");
    
    return 0;
}
@end
