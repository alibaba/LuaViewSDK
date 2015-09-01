//
//  LVGestureRecognizer.m
//  LVSDK
//
//  Created by dongxicheng on 1/22/15.
//  Copyright (c) 2015 dongxicheng. All rights reserved.
//

#import "LVGestureRecognizer.h"

@implementation LVGestureRecognizer


static void releaseUserData(LVUserDataGesture * user){
    if( user && user->gesture ){
        UIGestureRecognizer<LVProtocal>* gesture = CFBridgingRelease(user->gesture);
        user->gesture = NULL;
        if( gesture ){
            gesture.lv_lview = nil;
            gesture.lv_userData = NULL;
        }
    }
}

static int __GC (lv_State *L) {
    LVUserDataGesture * user = (LVUserDataGesture *)lv_touserdata(L, 1);
    releaseUserData(user);
    return 0;
}


static int __tostring (lv_State *L) {
    LVUserDataGesture * user = (LVUserDataGesture *)lv_touserdata(L, 1);
    if( user ){
        UIGestureRecognizer* gesture =  (__bridge UIGestureRecognizer *)(user->gesture);
        NSString* s = [NSString stringWithFormat:@"LVUserDataGesture: %@", gesture ];
        lv_pushstring(L, s.UTF8String);
        return 1;
    }
    return 0;
}

static int location (lv_State *L) {
    LVUserDataGesture * user = (LVUserDataGesture *)lv_touserdata(L, 1);
    if( user ){
        UIGestureRecognizer* gesture =  (__bridge UIGestureRecognizer *)(user->gesture);
        CGPoint p = [gesture locationInView:gesture.view];
        lv_pushnumber(L, p.x);
        lv_pushnumber(L, p.y);
        return 2;
    }
    return 0;
}

static int state (lv_State *L) {
    LVUserDataGesture * user = (LVUserDataGesture *)lv_touserdata(L, 1);
    if( user ){
        UIGestureRecognizer* gesture =  (__bridge UIGestureRecognizer *)(user->gesture);
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
+(void) releaseUD:(LVUserDataGesture *) user{
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
    {
        lv_pushnumber(L, 0);
        lv_setglobal(L, "UIGestureRecognizerStatePossible");
        lv_pushnumber(L, 1);
        lv_setglobal(L, "UIGestureRecognizerStateBegan");
        lv_pushnumber(L, 2);
        lv_setglobal(L, "UIGestureRecognizerStateChanged");
        lv_pushnumber(L, 3);
        lv_setglobal(L, "UIGestureRecognizerStateEnded");
        lv_pushnumber(L, 4);
        lv_setglobal(L, "UIGestureRecognizerStateCancelled");
        lv_pushnumber(L, 5);
        lv_setglobal(L, "UIGestureRecognizerStateFailed");
    }
    return 0;
}
@end
