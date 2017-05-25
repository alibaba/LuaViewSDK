/**
  * Created by LuaView.
  * Copyright (c) 2017, Alibaba Group. All rights reserved.
  *
  * This source code is licensed under the MIT.
  * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
  */

#import "LVTapGesture.h"
#import "LVGesture.h"
#import "LView.h"
#import "LVHeads.h"

@implementation LVTapGesture


-(void) dealloc{
    LVLog(@"LVTapGesture.dealloc");
    [LVGesture releaseUD:_lv_userData];
}

-(id) init:(lua_State*) l{
    self = [super initWithTarget:self action:@selector(handleGesture:)];
    if( self ){
        self.lv_luaviewCore = LV_LUASTATE_VIEW(l);
    }
    return self;
}



-(void) handleGesture:(LVTapGesture*)sender {
    lua_State* l = self.lv_luaviewCore.l;
    if ( l ){
        lua_checkstack32(l);
        lv_pushUserdata(l, self.lv_userData);
        [LVUtil call:l lightUserData:self key1:"callback" key2:NULL nargs:1];
    }
}

static int lvNewTapGestureRecognizer (lua_State *L) {
    Class c = [LVUtil upvalueClass:L defaultClass:[LVTapGesture class]];
    {
        LVTapGesture* gesture = [[c alloc] init:L];
        
        if( lua_type(L, 1) == LUA_TFUNCTION ) {
            [LVUtil registryValue:L key:gesture stack:1];
        }
        
        {
            NEW_USERDATA(userData, Gesture);
            gesture.lv_userData = userData;
            userData->object = CFBridgingRetain(gesture);
            
            luaL_getmetatable(L, META_TABLE_TapGesture );
            lua_setmetatable(L, -2);
        }
    }
    return 1; /* new userdatum is already on the stack */
}

+(int) lvClassDefine:(lua_State *)L globalName:(NSString*) globalName{
    [LVUtil reg:L clas:self cfunc:lvNewTapGestureRecognizer globalName:globalName defaultName:@"TapGesture"];
    
    const struct luaL_Reg memberFunctions [] = {
        {NULL, NULL}
    };
    
    lv_createClassMetaTable(L ,META_TABLE_TapGesture);
    
    luaL_openlib(L, NULL, [LVGesture baseMemberFunctions], 0);
    luaL_openlib(L, NULL, memberFunctions, 0);
    return 1;
}


@end
