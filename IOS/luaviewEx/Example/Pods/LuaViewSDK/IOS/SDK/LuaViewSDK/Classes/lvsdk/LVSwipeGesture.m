/**
  * Created by LuaView.
  * Copyright (c) 2017, Alibaba Group. All rights reserved.
  *
  * This source code is licensed under the MIT.
  * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
  */

#import "LVSwipeGesture.h"
#import "LVGesture.h"
#import "LView.h"
#import "LVHeads.h"

@implementation LVSwipeGesture


-(void) dealloc{
    LVLog(@"LVSwipeGesture.dealloc");
    [LVGesture releaseUD:_lv_userData];
}

-(id) init:(lua_State*) l{
    self = [super initWithTarget:self action:@selector(handleGesture:)];
    if( self ){
        self.lv_luaviewCore = LV_LUASTATE_VIEW(l);
    }
    return self;
}

-(void) handleGesture:(LVSwipeGesture*)sender {
    lua_State* l = self.lv_luaviewCore.l;
    if ( l ){
        lua_checkstack32(l);
        lv_pushUserdata(l,self.lv_userData);
        [LVUtil call:l lightUserData:self key1:"callback" key2:NULL nargs:1];
    }
}

static int lvSwipeGestureRecognizer (lua_State *L) {
    Class c = [LVUtil upvalueClass:L defaultClass:[LVSwipeGesture class]];
    {
        LVSwipeGesture* gesture = [[c alloc] init:L];
        
        if( lua_type(L, 1) == LUA_TFUNCTION ) {
            [LVUtil registryValue:L key:gesture stack:1];
        }
        
        {
            NEW_USERDATA(userData, Gesture);
            gesture.lv_userData = userData;
            userData->object = CFBridgingRetain(gesture);
            
            luaL_getmetatable(L, META_TABLE_SwipeGesture );
            lua_setmetatable(L, -2);
        }
    }
    return 1; /* new userdatum is already on the stack */
}

static int touchCount (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( LVIsType(user, Gesture) ){
        LVSwipeGesture* gesture =  (__bridge LVSwipeGesture *)(user->object);
        if( lua_gettop(L)>=2 ) {
            float num = lua_tonumber(L, 2);
            gesture.numberOfTouchesRequired = num;
            return 0;
        } else {
            float num = gesture.numberOfTouchesRequired;
            lua_pushnumber(L, num);
            return 1;
        }
    }
    return 0;
}

static int direction (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( LVIsType(user, Gesture) ){
        LVSwipeGesture* gesture =  (__bridge LVSwipeGesture *)(user->object);
        if ( lua_gettop(L)>=2 ) {
            float num = lua_tonumber(L, 2);
            gesture.direction = num;
            return 0;
        } else {
            float direction = gesture.direction;
            lua_pushnumber(L, direction);
            return 1;
        }
    }
    return 0;
}

+(int) lvClassDefine:(lua_State *)L globalName:(NSString*) globalName{
    {
        lua_settop(L, 0);
        NSDictionary* v = nil;
        v = @{
              @"LEFT":@(UISwipeGestureRecognizerDirectionLeft),
              @"RIGHT":@(UISwipeGestureRecognizerDirectionRight),
              @"UP":@(UISwipeGestureRecognizerDirectionUp),
              @"DOWN":@(UISwipeGestureRecognizerDirectionDown),
              };
        [LVUtil defineGlobal:@"GestureDirection" value:v L:L];
    }
    
    [LVUtil reg:L clas:self cfunc:lvSwipeGestureRecognizer globalName:globalName defaultName:@"SwipeGesture"];
    
    lv_createClassMetaTable(L ,META_TABLE_SwipeGesture);
    luaL_openlib(L, NULL, [LVGesture baseMemberFunctions], 0);
    {
        const struct luaL_Reg memberFunctions [] = {
            {"touchCount", touchCount},
            {"direction", direction},
            {NULL, NULL}
        };
        luaL_openlib(L, NULL, memberFunctions, 0);
    }
    return 1;
}



@end
