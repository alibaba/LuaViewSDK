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
#import "LView.h"
#import <UIKit/UIGestureRecognizerSubclass.h>
#import "LVEvent.h"

@implementation LVGesture

-(id) init:(lv_State*) l{
    self = [super initWithTarget:self action:@selector(handleGesture:)];
    if( self ){
        self.lv_lview = (__bridge LView *)(l->lView);
    }
    return self;
}

-(void) handleGesture:(LVGesture*)sender {
    [self handleGesture:sender event:nil eventType:0];
}

-(void) handleGesture:(LVGesture*)sender event:(UIEvent*) event eventType:(NSInteger) eventType{
    lv_State* l = self.lv_lview.l;
    if ( l ){
        lv_settop(l, 0);
        lv_checkStack32(l);
        
        if( self.onTouchEventCallback ) {
            LVEvent* lvEvent = nil;
            lvEvent = [LVEvent createLuaEvent:l event:event];
            lvEvent.eventType = eventType;
            self.onTouchEventCallback(self,1);
            lvEvent.event = nil;
        } else {
            lv_pushUserdata(l,self.lv_userData);
        }
        [LVUtil call:l lightUserData:self key1:"callback" key2:NULL nargs:1];
    }
}

- (void)touchesBegan:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event{
    [super touchesBegan:touches withEvent:event];
    [self handleGesture:self event:event eventType:LVTouchEventType_DOWN];
}

- (void)touchesMoved:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event{
    [super touchesMoved:touches withEvent:event];
    [self handleGesture:self event:event eventType:LVTouchEventType_MOVE];
}

- (void)touchesEnded:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event{
    [super touchesEnded:touches withEvent:event];
    [self handleGesture:self event:event eventType:LVTouchEventType_UP];
    
}

- (void)touchesCancelled:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event{
    [super touchesCancelled:touches withEvent:event];
    [self handleGesture:self event:event eventType:LVTouchEventType_CANCEL];
}


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

static int nativeGesture (lv_State *L) {
    LVUserDataInfo * userData = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( userData ){
        UIGestureRecognizer* gesture = (__bridge UIGestureRecognizer *)(userData->object);
        if( gesture && [gesture isKindOfClass:[UIGestureRecognizer class]] ){
            lv_pushNativeObjectWithBox(L, gesture);
            return 1;
        }
    }
    return 0;
}

static const struct lvL_reg baseMemberFunctions [] = {
    {"nativeGesture", nativeGesture },
    
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

static int lvNewGesture (lv_State *L) {
    {
        Class c = [LVUtil upvalueClass:L defaultClass:[LVGesture class]];
        
        LVGesture* gesture = [[c alloc] init:L];
        
        if( lv_type(L, 1) != LV_TNIL ) {
            [LVUtil registryValue:L key:gesture stack:1];
        }
        
        {
            NEW_USERDATA(userData, Gesture);
            gesture.lv_userData = userData;
            userData->object = CFBridgingRetain(gesture);
            
            lvL_getmetatable(L, META_TABLE_Gesture );
            lv_setmetatable(L, -2);
        }
    }
    return 1; /* new userdatum is already on the stack */
}

+(int) lvClassDefine:(lv_State *)L globalName:(NSString*) globalName{
    
    [LVUtil reg:L clas:self cfunc:lvNewGesture globalName:globalName defaultName:@"Gesture"];
    
    lv_createClassMetaTable(L, META_TABLE_Gesture);
    
    lvL_openlib(L, NULL, [LVGesture baseMemberFunctions], 0);
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

    return 0;
}
@end
