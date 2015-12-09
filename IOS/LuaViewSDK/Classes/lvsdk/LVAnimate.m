//
//  LVAnimate.m
//  JU
//
//  Created by dongxicheng on 1/7/15.
//  Copyright (c) 2015 ju.taobao.com. All rights reserved.
//

#import "LVAnimate.h"
#import "LVUtil.h"
#import "LView.h"
#import "lV.h"
#import "lVauxlib.h"
#import "lVlib.h"
#import "lVstate.h"
#import "lVgc.h"


@interface LVAnimate ()
@property(nonatomic,strong) id mySelf;
@property(nonatomic,assign) float time;
@end


@implementation LVAnimate

-(id) init:(lv_State*) l{
    self = [super init];
    if( self ){
        self.mySelf = self;
        self.lv_lview = (__bridge LView *)(l->lView);
    }
    return self;
}

-(void) dealloc{
    self.lv_lview = nil;
    self.lv_userData = nil;
}


static int lvNewAnimate (lv_State *L) {
    int argNum = lv_gettop(L);
    if( argNum>=1 ){
        LVAnimate* animate = [[LVAnimate alloc] init:L];
        
        int stackID = 1;
        
        float delay = 0;
        float duration = 0.3;
        UIViewAnimationOptions option = 0;
        CGFloat dampingRatio = 0;//0~1
        CGFloat velocity = 0;//0~1
        
        if( lv_type(L, stackID)==LV_TNUMBER ){
            duration = lv_tonumber(L,stackID++);
        }
        if( lv_type(L, stackID)==LV_TNUMBER ){
            delay = lv_tonumber(L,stackID++);
        }
        
        if( lv_type(L, stackID)==LV_TNUMBER ){
            dampingRatio = lv_tonumber(L,stackID++);
        }
        
        if( lv_type(L, stackID)==LV_TNUMBER ){
            velocity = lv_tonumber(L,stackID++);
        }
        
        if( lv_type(L, stackID)==LV_TNUMBER ){
            option = lv_tonumber(L,stackID++);
        }
        
        lv_createtable(L, 0, 8);// table
        if( argNum>=stackID && lv_type(L,stackID)==LV_TFUNCTION ){
            lv_pushstring(L, "animations");// key
            lv_pushvalue(L, stackID);//value
            lv_settable(L, -3);
            stackID++;
        }
        if( argNum>=stackID && lv_type(L,stackID)==LV_TFUNCTION ){
            lv_pushstring(L, "completion");// key
            lv_pushvalue(L, stackID );//value
            lv_settable(L, -3);
        }
        
        [LVUtil registryValue:L key:animate stack:-1];
        
        
        if( dampingRatio>0 ) {
            [UIView animateWithDuration:duration
                                  delay:delay
                 usingSpringWithDamping:dampingRatio
                  initialSpringVelocity:velocity
                                options:option animations:^{
                if( animate.lv_lview && animate.lv_lview.l ) {
                    lv_checkStack32( animate.lv_lview.l);
                    [LVUtil call:animate.lv_lview.l lightUserData:animate key1:"animations" key2:NULL nargs:0];
                }
            } completion:^(BOOL finished) {
                lv_State* l = animate.lv_lview.l;
                if( l ) {
                    lv_settop(l, 0);
                    lv_checkStack32(l);
                    [LVUtil call:l lightUserData:animate key1:"completion" key2:NULL nargs:0];
                    
                    [LVUtil unregistry:l key:animate];
                }
                animate.mySelf = nil;
            }];
        } else {
            [UIView animateWithDuration:duration
                                  delay:delay
                                options:option animations:^{
                                    if( animate.lv_lview && animate.lv_lview.l ) {
                                        lv_checkStack32( animate.lv_lview.l);
                                        [LVUtil call:animate.lv_lview.l lightUserData:animate key1:"animations" key2:NULL nargs:0];
                                    }
                                } completion:^(BOOL finished) {
                                    lv_State* l = animate.lv_lview.l;
                                    if( l ) {
                                        lv_settop(l, 0);
                                        lv_checkStack32(l);
                                        [LVUtil call:l lightUserData:animate key1:"completion" key2:NULL nargs:0];
                                        
                                        [LVUtil unregistry:l key:animate];
                                    }
                                    animate.mySelf = nil;
                                }];
        }
    }
    return 0; /* new userdatum is already on the stack */
}

+(int) classDefine:(lv_State *)L {
    {
        lv_pushcfunction(L, lvNewAnimate);
        lv_setglobal(L, "Animate");
    }
    return 1;
}

@end
