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
#import "lV.h"
#import "lVauxlib.h"
#import "lVlib.h"
#import "lVstate.h"
#import "lVgc.h"


@implementation LVLoadingIndicator



-(id) init:(lv_State*) l{
    self = [super init];
    if( self ){
        self.lv_lview = (__bridge LView *)(l->lView);
        self.clipsToBounds = YES;
        self.userInteractionEnabled = NO;
    }
    return self;
}

-(void) dealloc{
}

#pragma -mark lvNewActivityIndicator
static int lvNewActivityIndicator (lv_State *L) {
    {
        LVLoadingIndicator* pageControl = [[LVLoadingIndicator alloc] init:L];
        
        {
            NEW_USERDATA(userData, View);
            userData->object = CFBridgingRetain(pageControl);
            
            lvL_getmetatable(L, META_TABLE_UIActivityIndicatorView );
            lv_setmetatable(L, -2);
        }
        LView* view = (__bridge LView *)(L->lView);
        if( view ){
            [view containerAddSubview:pageControl];
        }
    }
    return 1; /* new userdatum is already on the stack */
}

static int startAnimating(lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user ){
        LVLoadingIndicator* view = (__bridge LVLoadingIndicator *)(user->object);
        if( view ){
            [view startAnimating];
        }
    }
    return 0;
}

static int stopAnimating(lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user ){
        LVLoadingIndicator* view = (__bridge LVLoadingIndicator *)(user->object);
        if( view ){
            [view stopAnimating];
        }
    }
    return 0;
}

static int isAnimating(lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user ){
        LVLoadingIndicator* view = (__bridge LVLoadingIndicator *)(user->object);
        if( view ){
            lv_pushboolean(L, view.isAnimating);
            return 1;
        }
    }
    return 0;
}

static int color(lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user ){
        LVLoadingIndicator* view = (__bridge LVLoadingIndicator *)(user->object);
        if( view ){
            if( lv_gettop(L)>=2 ) {
                UIColor* color = lv_getColorFromStack(L, 2);
                view.color = color;
                return 0;
            } else {
                UIColor* color = view.color;
                NSUInteger c = 0;
                CGFloat a = 1;
                if( lv_uicolor2int(color, &c, &a) ){
                    lv_pushnumber(L, c );
                    lv_pushnumber(L, a);
                    return 2;
                }
            }
        }
    }
    return 0;
}

+(int) classDefine:(lv_State *)L {
    {
        lv_pushcfunction(L, lvNewActivityIndicator);
        lv_setglobal(L, "LoadingIndicator");
    }
    const struct lvL_reg memberFunctions [] = {
        {"start",  startAnimating },
        {"stop",   stopAnimating },
        {"show",  startAnimating },
        {"hide",   stopAnimating },
        {"isAnimating",  isAnimating },
        {"color", color},
        {NULL, NULL}
    };
    
    lv_createClassMetaTable(L, META_TABLE_UIActivityIndicatorView);
    
    lvL_openlib(L, NULL, [LVBaseView baseMemberFunctions], 0);
    lvL_openlib(L, NULL, memberFunctions, 0);
    
    const char* keys[] = { "addView", NULL};// 移除多余API
    lv_luaTableRemoveKeys(L, keys );
    return 1;
}



@end
