//
//  LVLongPressGestureRecognizer.m
//  LVSDK
//
//  Created by 城西 on 15/3/9.
//  Copyright (c) 2015年 dongxicheng. All rights reserved.
//

#import "LVLongPressGestureRecognizer.h"
#import "LVGestureRecognizer.h"
#import "LView.h"
#import "lV.h"
#import "lVauxlib.h"
#import "lVlib.h"
#import "lVstate.h"
#import "lVgc.h"

@implementation LVLongPressGestureRecognizer


-(void) dealloc{
    LVLog(@"LVLongPressGestureRecognizer.dealloc");
    [LVGestureRecognizer releaseUD:_lv_userData];
}

-(id) init:(lv_State*) l{
    self = [super initWithTarget:self action:@selector(handleGesture:)];
    if( self ){
        self.lv_lview = (__bridge LView *)(l->lView);
    }
    return self;
}

-(void) handleGesture:(LVLongPressGestureRecognizer*)sender {
    lv_State* l = self.lv_lview.l;
    if ( l ){
        lv_checkStack32(l);
        lv_pushUserdata(l,self.lv_userData);
        [LVUtil call:l lightUserData:self key1:"callback" key2:NULL nargs:1];
    }
}


static int lvNewGestureRecognizer (lv_State *L) {
    {
        LVLongPressGestureRecognizer* gesture = [[LVLongPressGestureRecognizer alloc] init:L];
        
        if( lv_type(L, 1) != LV_TNIL ) {
            [LVUtil registryValue:L key:gesture stack:1];
        }
        
        {
            NEW_USERDATA(userData, Gesture);
            gesture.lv_userData = userData;
            userData->object = CFBridgingRetain(gesture);
            
            lvL_getmetatable(L, META_TABLE_LongPressGesture );
            lv_setmetatable(L, -2);
        }
    }
    return 1; /* new userdatum is already on the stack */
}

static int setTouchCount (lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( LVIsType(user, Gesture) ){
        LVLongPressGestureRecognizer* gesture =  (__bridge LVLongPressGestureRecognizer *)(user->object);
        if( lv_gettop(L)>=2 ){
            float num = lv_tonumber(L, 2);
            gesture.numberOfTouchesRequired = num;
            return 0;
        } else {
            float num = gesture.numberOfTouchesRequired;
            lv_pushnumber(L, num);
            return 1;
        }
    }
    return 0;
}

+(int) classDefine:(lv_State *)L {
    {
        lv_pushcfunction(L, lvNewGestureRecognizer);
        lv_setglobal(L, "LongPressGesture");
    }
    
    lv_createClassMetaTable(L, META_TABLE_LongPressGesture);
    
    lvL_openlib(L, NULL, [LVGestureRecognizer baseMemberFunctions], 0);
    
    {
        const struct lvL_reg memberFunctions [] = {
            {"touchCount",     setTouchCount},
            {NULL, NULL}
        };
        lvL_openlib(L, NULL, memberFunctions, 0);
    }
    return 1;
}


@end
