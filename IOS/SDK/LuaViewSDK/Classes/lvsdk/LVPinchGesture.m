/**
  * Created by LuaView.
  * Copyright (c) 2017, Alibaba Group. All rights reserved.
  *
  * This source code is licensed under the MIT.
  * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
  */

#import "LVPinchGesture.h"
#import "LVGesture.h"
#import "LView.h"
#import "LVHeads.h"

@implementation LVPinchGesture

-(void) dealloc{
    LVLog(@"LVPinchGesture.dealloc");
    [LVGesture releaseUD:_lv_userData];
}

-(id) init:(lua_State*) l{
    self = [super initWithTarget:self action:@selector(handleGesture:)];
    if( self ){
        self.lv_luaviewCore = LV_LUASTATE_VIEW(l);
    }
    return self;
}

-(void) handleGesture:(LVPinchGesture*)sender {
    lua_State* l = self.lv_luaviewCore.l;
    if ( l ){
        lua_checkstack32(l);
        lv_pushUserdata(l,self.lv_userData);
        [LVUtil call:l lightUserData:self key1:"callback" key2:NULL nargs:1];
    }
}

static int lvNewPinchGestureRecognizer (lua_State *L) {
    Class c = [LVUtil upvalueClass:L defaultClass:[LVPinchGesture class]];
    {
        LVPinchGesture* gesture = [[c alloc] init:L];
        
        if( lua_type(L, 1) != LUA_TFUNCTION ) {
            [LVUtil registryValue:L key:gesture stack:1];
        }
        
        {
            NEW_USERDATA(userData, Gesture);
            gesture.lv_userData = userData;
            userData->object = CFBridgingRetain(gesture);
            
            luaL_getmetatable(L, META_TABLE_PinchGesture );
            lua_setmetatable(L, -2);
        }
    }
    return 1; /* new userdatum is already on the stack */
}

static int scale (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( LVIsType(user, Gesture) ){
        LVPinchGesture* gesture =  (__bridge LVPinchGesture *)(user->object);
        float s = gesture.scale;
        lua_pushnumber(L, s);
        return 1;
    }
    return 0;
}

+(int) lvClassDefine:(lua_State *)L globalName:(NSString*) globalName{
    [LVUtil reg:L clas:self cfunc:lvNewPinchGestureRecognizer globalName:globalName defaultName:@"PinchGesture"];
    
    lv_createClassMetaTable(L ,META_TABLE_PinchGesture);
    
    luaL_openlib(L, NULL, [LVGesture baseMemberFunctions], 0);
    
    {
        const struct luaL_Reg memberFunctions [] = {
            {"scale", scale},
            {NULL, NULL}
        };
        luaL_openlib(L, NULL, memberFunctions, 0);
    }
    return 1;
}

@end
