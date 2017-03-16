/**
  * Created by LuaView.
  * Copyright (c) 2017, Alibaba Group. All rights reserved.
  *
  * This source code is licensed under the MIT.
  * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
  */


#import "LVAnimate.h"
#import "LVUtil.h"
#import "LView.h"
#import "LVHeads.h"


@interface LVAnimate ()
@property(nonatomic,strong) id mySelf;
@property(nonatomic,assign) float time;
@end


@implementation LVAnimate

-(id) init:(lua_State*) L{
    self = [super init];
    if( self ){
        self.mySelf = self;
        self.lv_luaviewCore = LV_LUASTATE_VIEW(L);
    }
    return self;
}

-(void) dealloc{
    self.lv_luaviewCore = nil;
    self.lv_userData = nil;
}


static int lvNewAnimate (lua_State *L) {
    int argNum = lua_gettop(L);
    if( argNum>=1 ){
        LVAnimate* animate = [[LVAnimate alloc] init:L];
        
        int stackID = 1;
        
        float delay = 0;
        float duration = 0.3;
        UIViewAnimationOptions option = 0;
        CGFloat dampingRatio = 0;//0~1
        CGFloat velocity = 0;//0~1
        
        if( lua_type(L, stackID)==LUA_TNUMBER ){
            duration = lua_tonumber(L,stackID++);
        }
        if( lua_type(L, stackID)==LUA_TNUMBER ){
            delay = lua_tonumber(L,stackID++);
        }
        
        if( lua_type(L, stackID)==LUA_TNUMBER ){
            dampingRatio = lua_tonumber(L,stackID++);
        }
        
        if( lua_type(L, stackID)==LUA_TNUMBER ){
            velocity = lua_tonumber(L,stackID++);
        }
        
        if( lua_type(L, stackID)==LUA_TNUMBER ){
            option = lua_tonumber(L,stackID++);
        }
        
        lua_createtable(L, 0, 8);// table
        if( argNum>=stackID && lua_type(L,stackID)==LUA_TFUNCTION ){
            lua_pushstring(L, "animations");// key
            lua_pushvalue(L, stackID);//value
            lua_settable(L, -3);
            stackID++;
        }
        if( argNum>=stackID && lua_type(L,stackID)==LUA_TFUNCTION ){
            lua_pushstring(L, "completion");// key
            lua_pushvalue(L, stackID );//value
            lua_settable(L, -3);
        }
        
        [LVUtil registryValue:L key:animate stack:-1];
        
        
        if( dampingRatio>0 ) {
            [UIView animateWithDuration:duration
                                  delay:delay
                 usingSpringWithDamping:dampingRatio
                  initialSpringVelocity:velocity
                                options:option animations:^{
                if( animate.lv_luaviewCore && animate.lv_luaviewCore.l ) {
                    lua_checkstack32( animate.lv_luaviewCore.l);
                    [LVUtil call:animate.lv_luaviewCore.l lightUserData:animate key1:"animations" key2:NULL nargs:0];
                }
            } completion:^(BOOL finished) {
                lua_State* l = animate.lv_luaviewCore.l;
                if( l ) {
                    lua_settop(l, 0);
                    lua_checkstack32(l);
                    [LVUtil call:l lightUserData:animate key1:"completion" key2:NULL nargs:0];
                    
                    [LVUtil unregistry:l key:animate];
                }
                animate.mySelf = nil;
            }];
        } else {
            [UIView animateWithDuration:duration
                                  delay:delay
                                options:option animations:^{
                                    if( animate.lv_luaviewCore && animate.lv_luaviewCore.l ) {
                                        lua_checkstack32( animate.lv_luaviewCore.l);
                                        [LVUtil call:animate.lv_luaviewCore.l lightUserData:animate key1:"animations" key2:NULL nargs:0];
                                    }
                                } completion:^(BOOL finished) {
                                    lua_State* l = animate.lv_luaviewCore.l;
                                    if( l ) {
                                        lua_settop(l, 0);
                                        lua_checkstack32(l);
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
