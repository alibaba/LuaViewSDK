//
//  LVLoadingIndicator.m
//  LVSDK
//
//  Created by dongxicheng on 7/27/15.
//  Copyright (c) 2015 dongxicheng. All rights reserved.
//

#import "LVLoadingIndicator.h"
#import "LVBaseView.h"
#import "LView.h"
#import "LVHeads.h"


@implementation LVLoadingIndicator



-(id) init:(lua_State*) l{
    self = [super init];
    if( self ){
        self.lv_lview = LV_LUASTATE_VIEW(l);
        self.clipsToBounds = YES;
        self.userInteractionEnabled = NO;
    }
    return self;
}

-(void) dealloc{
}

#pragma -mark lvNewActivityIndicator
static int lvNewLoadingIndicator (lua_State *L) {
    {
        Class c = [LVUtil upvalueClass:L defaultClass:[LVLoadingIndicator class]];
        
        LVLoadingIndicator* pageControl = [[c alloc] init:L];
        
        {
            NEW_USERDATA(userData, View);
            userData->object = CFBridgingRetain(pageControl);
            
            luaL_getmetatable(L, META_TABLE_LoadingIndicator );
            lua_setmetatable(L, -2);
        }
        LView* view = LV_LUASTATE_VIEW(L);
        if( view ){
            [view containerAddSubview:pageControl];
        }
    }
    return 1; /* new userdatum is already on the stack */
}

static int startAnimating(lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( user ){
        LVLoadingIndicator* view = (__bridge LVLoadingIndicator *)(user->object);
        if( view ){
            [view startAnimating];
        }
    }
    return 0;
}

static int stopAnimating(lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( user ){
        LVLoadingIndicator* view = (__bridge LVLoadingIndicator *)(user->object);
        if( view ){
            [view stopAnimating];
        }
    }
    return 0;
}

static int isAnimating(lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( user ){
        LVLoadingIndicator* view = (__bridge LVLoadingIndicator *)(user->object);
        if( view ){
            lua_pushboolean(L, view.isAnimating);
            return 1;
        }
    }
    return 0;
}

static int color(lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( user ){
        LVLoadingIndicator* view = (__bridge LVLoadingIndicator *)(user->object);
        if( view ){
            if( lua_gettop(L)>=2 ) {
                UIColor* color = lv_getColorFromStack(L, 2);
                view.color = color;
                return 0;
            } else {
                UIColor* color = view.color;
                NSUInteger c = 0;
                CGFloat a = 1;
                if( lv_uicolor2int(color, &c, &a) ){
                    lua_pushnumber(L, c );
                    lua_pushnumber(L, a);
                    return 2;
                }
            }
        }
    }
    return 0;
}

+(int) lvClassDefine:(lua_State *)L globalName:(NSString*) globalName{
    [LVUtil reg:L clas:self cfunc:lvNewLoadingIndicator globalName:globalName defaultName:@"LoadingIndicator"];
    
    const struct luaL_Reg memberFunctions [] = {
        {"start",  startAnimating },
        {"stop",   stopAnimating },
        {"show",  startAnimating },
        {"hide",   stopAnimating },
        {"isAnimating",  isAnimating },
        {"color", color},
        {NULL, NULL}
    };
    
    lv_createClassMetaTable(L, META_TABLE_LoadingIndicator);
    
    luaL_openlib(L, NULL, [LVBaseView baseMemberFunctions], 0);
    luaL_openlib(L, NULL, memberFunctions, 0);
    
    const char* keys[] = { "addView", NULL};// 移除多余API
    lv_luaTableRemoveKeys(L, keys );
    return 1;
}



@end
