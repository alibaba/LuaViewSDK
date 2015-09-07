//
//  LVSwipeGestureRecognizer.m
//  LVSDK
//
//  Created by 城西 on 15/3/9.
//  Copyright (c) 2015年 dongxicheng. All rights reserved.
//

#import "LVSwipeGestureRecognizer.h"
#import "LVGestureRecognizer.h"
#import "LView.h"

@implementation LVSwipeGestureRecognizer


-(void) dealloc{
    LVLog(@"LVSwipeGestureRecognizer.dealloc");
    [LVGestureRecognizer releaseUD:_lv_userData];
}

-(id) init:(lv_State*) l{
    self = [super initWithTarget:self action:@selector(handleGesture:)];
    if( self ){
        self.lv_lview = (__bridge LView *)(l->lView);
    }
    return self;
}

-(void) handleGesture:(LVSwipeGestureRecognizer*)sender {
    lv_State* l = self.lv_lview.l;
    if ( l ){
        lv_checkStack32(l);
        lv_pushUserdata(l,self.lv_userData);
        [LVUtil call:l lightUserData:self key:"callback" nargs:1];
    }
}

static int lvNewGestureRecognizer (lv_State *L) {
    {
        LVSwipeGestureRecognizer* gesture = [[LVSwipeGestureRecognizer alloc] init:L];
        
        if( lv_type(L, 1) == LV_TFUNCTION ) {
            [LVUtil registryValue:L key:gesture stack:1];
        }
        
        {
            NEW_USERDATA(userData, LVUserDataGesture);
            gesture.lv_userData = userData;
            userData->gesture = CFBridgingRetain(gesture);
            
            lvL_getmetatable(L, META_TABLE_SwipeGesture );
            lv_setmetatable(L, -2);
        }
    }
    return 1; /* new userdatum is already on the stack */
}

static int numberOfTouchesRequired (lv_State *L) {
    LVUserDataGesture * user = (LVUserDataGesture *)lv_touserdata(L, 1);
    if( LVIsType(user,LVUserDataGesture) ){
        LVSwipeGestureRecognizer* gesture =  (__bridge LVSwipeGestureRecognizer *)(user->gesture);
        float num = gesture.numberOfTouchesRequired;
        lv_pushnumber(L, num);
        return 1;
    }
    return 0;
}

static int setNumberOfTouchesRequired (lv_State *L) {
    LVUserDataGesture * user = (LVUserDataGesture *)lv_touserdata(L, 1);
    if( LVIsType(user,LVUserDataGesture) && lv_gettop(L)>=2 ){
        float num = lv_tonumber(L, 2);
        LVSwipeGestureRecognizer* gesture =  (__bridge LVSwipeGestureRecognizer *)(user->gesture);
        gesture.numberOfTouchesRequired = num;
        lv_pushvalue(L, 1);
        return 1;
    }
    return 0;
}
static int direction (lv_State *L) {
    LVUserDataGesture * user = (LVUserDataGesture *)lv_touserdata(L, 1);
    if( LVIsType(user,LVUserDataGesture) ){
        LVSwipeGestureRecognizer* gesture =  (__bridge LVSwipeGestureRecognizer *)(user->gesture);
        float direction = gesture.direction;
        lv_pushnumber(L, direction);
        return 1;
    }
    return 0;
}

static int setDirection (lv_State *L) {
    LVUserDataGesture * user = (LVUserDataGesture *)lv_touserdata(L, 1);
    if( LVIsType(user,LVUserDataGesture) && lv_gettop(L)>=2 ){
        float num = lv_tonumber(L, 2);
        LVSwipeGestureRecognizer* gesture =  (__bridge LVSwipeGestureRecognizer *)(user->gesture);
        gesture.direction = num;
        lv_pushvalue(L, 1);
        return 1;
    }
    return 0;
}

+(int) classDefine:(lv_State *)L {
    {
        lv_pushnumber(L, 1);
        lv_setglobal(L, "UISwipeGestureRecognizerDirectionRight");
        lv_pushnumber(L, 2);
        lv_setglobal(L, "UISwipeGestureRecognizerDirectionLeft");
        lv_pushnumber(L, 3);
        lv_setglobal(L, "UISwipeGestureRecognizerDirectionUp");
        lv_pushnumber(L, 4);
        lv_setglobal(L, "UISwipeGestureRecognizerDirectionDown");
    }
    {
        lv_pushcfunction(L, lvNewGestureRecognizer);
        lv_setglobal(L, "UISwipeGestureRecognizer");
    }
    
    lv_createClassMetaTable(L ,META_TABLE_SwipeGesture);
    
    lvL_openlib(L, NULL, [LVGestureRecognizer baseMemberFunctions], 0);
    
    {
        const struct lvL_reg memberFunctions [] = {
            {"numberOfTouchesRequired", numberOfTouchesRequired},
            {"setNumberOfTouchesRequired", setNumberOfTouchesRequired},
            {"direction", direction},
            {"setDirection", setDirection},
            {NULL, NULL}
        };
        lvL_openlib(L, NULL, memberFunctions, 0);
    }
    return 1;
}



@end
