//
//  LVLongPressGestureRecognizer.m
//  LVSDK
//
//  Created by 城西 on 15/3/9.
//  Copyright (c) 2015年 dongxicheng. All rights reserved.
//

#import "LVLongPressGesture.h"
#import "LVGesture.h"
#import "LView.h"
#import "LVHeads.h"

@implementation LVLongPressGesture


-(void) dealloc{
    LVLog(@"LVLongPressGesture.dealloc");
    [LVGesture releaseUD:_lv_userData];
}

-(id) init:(lua_State*) l{
    self = [super initWithTarget:self action:@selector(handleGesture:)];
    if( self ){
        self.lv_lview = (__bridge LView *)(l->lView);
    }
    return self;
}

-(void) handleGesture:(LVLongPressGesture*)sender {
    lua_State* l = self.lv_lview.l;
    if ( l ){
        lv_checkStack32(l);
        lv_pushUserdata(l,self.lv_userData);
        [LVUtil call:l lightUserData:self key1:"callback" key2:NULL nargs:1];
    }
}


static int lvNewLongGesture (lua_State *L) {
    {
        Class c = [LVUtil upvalueClass:L defaultClass:[LVLongPressGesture class]];
        
        LVLongPressGesture* gesture = [[c alloc] init:L];
        
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

static int setTouchCount (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( LVIsType(user, Gesture) ){
        LVLongPressGesture* gesture =  (__bridge LVLongPressGesture *)(user->object);
        if( lv_gettop(L)>=2 ){
            float num = lua_tonumber(L, 2);
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

+(int) lvClassDefine:(lua_State *)L globalName:(NSString*) globalName{
    [LVUtil reg:L clas:self cfunc:lvNewLongGesture globalName:globalName defaultName:@"LongPressGesture"];
    
    lv_createClassMetaTable(L, META_TABLE_LongPressGesture);
    
    lvL_openlib(L, NULL, [LVGesture baseMemberFunctions], 0);
    
    {
        const struct luaL_Reg memberFunctions [] = {
            {"touchCount",     setTouchCount},
            {NULL, NULL}
        };
        lvL_openlib(L, NULL, memberFunctions, 0);
    }
    return 1;
}


@end
