/**
  * Created by LuaView.
  * Copyright (c) 2017, Alibaba Group. All rights reserved.
  *
  * This source code is licensed under the MIT.
  * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
  */

#import "LVGesture.h"
#import "LView.h"
#import <UIKit/UIGestureRecognizerSubclass.h>
#import "LVEvent.h"
#import "LVHeads.h"

@implementation LVGesture

-(id) init:(lua_State*) l{
    self = [super initWithTarget:self action:@selector(handleGesture:)];
    if( self ){
        self.lv_luaviewCore = LV_LUASTATE_VIEW(l);
        self.delegate = self;
    }
    return self;
}

-(void) handleGesture:(LVGesture*)sender {
    [self handleGesture:sender event:nil eventType:0];
}

-(void) handleGesture:(LVGesture*)sender event:(UIEvent*) event eventType:(NSInteger) eventType{
    lua_State* l = self.lv_luaviewCore.l;
    if ( l ){
        lua_settop(l, 0);
        lua_checkstack32(l);
        
        if( self.onTouchEventCallback ) {
            LVEvent* lvEvent = nil;
            lvEvent = [LVEvent createLuaEvent:l event:event gesture:self];
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
    [self handleGesture:self event:event eventType:LVTouchEventType_MOVE];
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
            gesture.lv_luaviewCore = nil;
            gesture.lv_userData = NULL;
        }
    }
}

static int __GC (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    releaseUserData(user);
    return 0;
}


static int __tostring (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( user ){
        UIGestureRecognizer* gesture =  (__bridge UIGestureRecognizer *)(user->object);
        NSString* s = [NSString stringWithFormat:@"LVUserDataGesture: %@", gesture ];
        lua_pushstring(L, s.UTF8String);
        return 1;
    }
    return 0;
}

static int location (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( user ){
        UIGestureRecognizer* gesture =  (__bridge UIGestureRecognizer *)(user->object);
        CGPoint p = [gesture locationInView:gesture.view];
        lua_pushnumber(L, p.x);
        lua_pushnumber(L, p.y);
        return 2;
    }
    return 0;
}

static int state (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( user ){
        UIGestureRecognizer* gesture =  (__bridge UIGestureRecognizer *)(user->object);
        NSInteger state = gesture.state;
        lua_pushnumber(L, state);
        return 1;
    }
    return 0;
}

static int nativeGesture (lua_State *L) {
    LVUserDataInfo * userData = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( userData ){
        UIGestureRecognizer* gesture = (__bridge UIGestureRecognizer *)(userData->object);
        if( gesture && [gesture isKindOfClass:[UIGestureRecognizer class]] ){
            lv_pushNativeObjectWithBox(L, gesture);
            return 1;
        }
    }
    return 0;
}

static const struct luaL_Reg baseMemberFunctions [] = {
    {"nativeGesture", nativeGesture },
    
    {"location", location },
    {"state", state },
    {"__gc", __GC },
    {"__tostring", __tostring },
    {NULL, NULL}
};

+(const luaL_Reg*) baseMemberFunctions{
    return baseMemberFunctions;
}

+(void) releaseUD:(LVUserDataInfo *) user{
    releaseUserData(user);
}

static int lvNewGesture (lua_State *L) {
    {
        Class c = [LVUtil upvalueClass:L defaultClass:[LVGesture class]];
        
        LVGesture* gesture = [[c alloc] init:L];
        
        if( lua_type(L, 1) != LUA_TNIL ) {
            [LVUtil registryValue:L key:gesture stack:1];
        }
        
        {
            NEW_USERDATA(userData, Gesture);
            gesture.lv_userData = userData;
            userData->object = CFBridgingRetain(gesture);
            
            luaL_getmetatable(L, META_TABLE_Gesture );
            lua_setmetatable(L, -2);
        }
    }
    return 1; /* new userdatum is already on the stack */
}

+(int) lvClassDefine:(lua_State *)L globalName:(NSString*) globalName{
    
    [LVUtil reg:L clas:self cfunc:lvNewGesture globalName:globalName defaultName:@"Gesture"];
    
    lv_createClassMetaTable(L, META_TABLE_Gesture);
    
    luaL_openlib(L, NULL, [LVGesture baseMemberFunctions], 0);
    lua_settop(L, 0);
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

// delegate

- (BOOL)gestureRecognizerShouldBegin:(UIGestureRecognizer *)gestureRecognizer{
    if( gestureRecognizer ==self ) {
        return YES;
    }
    return NO;
}

- (BOOL)gestureRecognizer:(UIGestureRecognizer *)gestureRecognizer shouldRecognizeSimultaneouslyWithGestureRecognizer:(UIGestureRecognizer *)otherGestureRecognizer{
    return YES;
}

- (BOOL)gestureRecognizer:(UIGestureRecognizer *)gestureRecognizer shouldReceiveTouch:(UITouch *)touch{
    if( gestureRecognizer ==self ) {
        return YES;
    }
    return NO;
}

- (BOOL)gestureRecognizer:(UIGestureRecognizer *)gestureRecognizer shouldReceivePress:(UIPress *)press{
    if( gestureRecognizer ==self ) {
        return YES;
    }
    return NO;
}


@end
