//
//  LVGestureRecognizer.m
//  LVSDK
//
//  Created by dongxicheng on 1/22/15.
//  Copyright (c) 2015 dongxicheng. All rights reserved.
//

#import "LVGesture.h"
#import "lV.h"
#import "lVauxlib.h"
#import "lVlib.h"
#import "lVstate.h"
#import "lVgc.h"

@implementation LVGesture


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

+(int) lvClassDefine:(lv_State *)L globalName:(NSString*) globalName{
    //    typedef NS_ENUM(NSInteger, UIGestureRecognizerState) {
    //        UIGestureRecognizerStatePossible,
    //        UIGestureRecognizerStateBegan,
    //        UIGestureRecognizerStateChanged,
    //        UIGestureRecognizerStateEnded,
    //        UIGestureRecognizerStateCancelled,
    //        UIGestureRecognizerStateFailed
    //    };
    lv_settop(L, 0);
    {
        NSDictionary* v = nil;
        v = @{
              @"POSSIBLE":@(UIGestureRecognizerStatePossible),
              @"BEGIN":@(UIGestureRecognizerStateBegan),
              @"CHANGED":@(UIGestureRecognizerStateChanged),
              @"END":@(UIGestureRecognizerStateEnded),
              @"CANCEL":@(UIGestureRecognizerStateCancelled),
              @"FAILED":@(UIGestureRecognizerStateFailed),
              };
        [LVUtil defineGlobal:@"GestureState" value:v L:L];
    }
    {
        
        NSDictionary* v = nil;
        v = @{
              @"DOWN":@(0),
              @"UP":@(1),
              @"MOVE":@(2),
              @"CANCEL":@(3),
              @"OUTSIDE":@(4),
              };
        [LVUtil defineGlobal:@"TouchEvent" value:v L:L];
    }
    return 0;
}
@end
