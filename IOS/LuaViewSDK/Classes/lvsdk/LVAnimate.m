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
#import "LVHeads.h"


@interface LVAnimate ()
@property(nonatomic,strong) id mySelf;
@property(nonatomic,assign) float time;
@end


@implementation LVAnimate

-(id) init:(lua_State*) l{
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


static int lvNewAnimate (lua_State *L) {
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
            duration = lua_tonumber(L,stackID++);
        }
        if( lv_type(L, stackID)==LV_TNUMBER ){
            delay = lua_tonumber(L,stackID++);
        }
        
        if( lv_type(L, stackID)==LV_TNUMBER ){
            dampingRatio = lua_tonumber(L,stackID++);
        }
        
        if( lv_type(L, stackID)==LV_TNUMBER ){
            velocity = lua_tonumber(L,stackID++);
        }
        
        if( lv_type(L, stackID)==LV_TNUMBER ){
            option = lua_tonumber(L,stackID++);
        }
        
        lv_createtable(L, 0, 8);// table
        if( argNum>=stackID && lv_type(L,stackID)==LUA_TFUNCTION ){
            lua_pushstring(L, "animations");// key
            lv_pushvalue(L, stackID);//value
            lv_settable(L, -3);
            stackID++;
        }
        if( argNum>=stackID && lv_type(L,stackID)==LUA_TFUNCTION ){
            lua_pushstring(L, "completion");// key
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
                lua_State* l = animate.lv_lview.l;
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
                                    lua_State* l = animate.lv_lview.l;
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

+(int) lvClassDefine:(lua_State *)L globalName:(NSString*) globalName{
    [LVUtil reg:L clas:self cfunc:lvNewAnimate globalName:globalName defaultName:@"Animate"];
    return 1;
}

@end
