//
//  LVRotationGestureRecognizer.m
//  LVSDK
//
//  Created by 董希成 on 15/3/9.
//  Copyright (c) 2015年 dongxicheng. All rights reserved.
//

#import "LVRotationGesture.h"
#import "LVGesture.h"
#import "LView.h"
#import "LVHeads.h"

@implementation LVRotationGesture

-(void) dealloc{
    LVLog(@"LVRotationGesture.dealloc");
    [LVGesture releaseUD:_lv_userData];
}

-(id) init:(lua_State*) l{
    self = [super initWithTarget:self action:@selector(handleGesture:)];
    if( self ){
        self.lv_lview = (__bridge LView *)(l->lView);
    }
    return self;
}

-(void) handleGesture:(LVRotationGesture*)sender {
    lua_State* l = self.lv_lview.l;
    if ( l ){
        lv_checkStack32(l);
        lv_pushUserdata(l,self.lv_userData);
        [LVUtil call:l lightUserData:self key1:"callback" key2:NULL nargs:1];
    }
}

static int lvNewRotationGestureRecognizer (lua_State *L) {
    Class c = [LVUtil upvalueClass:L defaultClass:[LVRotationGesture class]];
    {
        LVRotationGesture* gesture = [[c alloc] init:L];
        
        if( lv_type(L, 1) == LUA_TFUNCTION ) {
            [LVUtil registryValue:L key:gesture stack:1];
        }
        
        {
            NEW_USERDATA(userData, Gesture);
            gesture.lv_userData = userData;
            userData->object = CFBridgingRetain(gesture);
            
            lvL_getmetatable(L, META_TABLE_RotaionGesture );
            lv_setmetatable(L, -2);
        }
    }
    return 1; /* new userdatum is already on the stack */
}

static int rotation (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( LVIsType(user, Gesture) ){
        LVRotationGesture* gesture =  (__bridge LVRotationGesture *)(user->object);
        float s = gesture.rotation;
        lv_pushnumber(L, s);
        return 1;
    }
    return 0;
}

+(int) lvClassDefine:(lua_State *)L globalName:(NSString*) globalName{
    [LVUtil reg:L clas:self cfunc:lvNewRotationGestureRecognizer globalName:globalName defaultName:@"RotationGesture"];
    
    lv_createClassMetaTable(L ,META_TABLE_RotaionGesture);
    
    lvL_openlib(L, NULL, [LVGesture baseMemberFunctions], 0);
    
    {
        const struct luaL_Reg memberFunctions [] = {
            {"rotation", rotation},
            {NULL, NULL}
        };
        lvL_openlib(L, NULL, memberFunctions, 0);
    }
    return 1;
}


@end
