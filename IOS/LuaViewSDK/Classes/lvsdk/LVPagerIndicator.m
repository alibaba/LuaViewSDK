//
//  LVPagerIndicator.m
//  lv5.1.4
//
//  Created by dongxicheng on 12/18/14.
//  Copyright (c) 2014 dongxicheng. All rights reserved.
//

#import "LVPagerIndicator.h"
#import "LVBaseView.h"
#import "LView.h"

@implementation LVPagerIndicator


-(id) init:(lv_State*) l{
    self = [super init];
    if( self ){
        self.lv_lview = (__bridge LView *)(l->lView);
    }
    return self;
}

-(void) dealloc{
}

#pragma -mark PageControl
static int lvNewPageControl (lv_State *L) {
    {
        LVPagerIndicator* pageControl = [[LVPagerIndicator alloc] init:L];
        
        {
            NEW_USERDATA(userData, LVUserDataView);
            userData->view = CFBridgingRetain(pageControl);
            
            lvL_getmetatable(L, META_TABLE_UIPageControl );
            lv_setmetatable(L, -2);
        }
        LView* view = (__bridge LView *)(L->lView);
        if( view ){
            [view containerAddSubview:pageControl];
        }
    }
    return 1; /* new userdatum is already on the stack */
}

static int setPageCount(lv_State *L) {
    LVUserDataView * user = (LVUserDataView *)lv_touserdata(L, 1);
    if( user ){
        LVPagerIndicator* view = (__bridge LVPagerIndicator *)(user->view);
        if( view ){
            if( lv_gettop(L)>=2 ) {
                int number = lv_tonumber(L, 2);
                view.numberOfPages = number;
                return 0;
            } else {
                lv_pushnumber(L, view.numberOfPages );
                return 1;
            }
        }
    }
    return 0;
}

static int setCurrentPage(lv_State *L) {
    LVUserDataView * user = (LVUserDataView *)lv_touserdata(L, 1);
    if( user ){
        LVPagerIndicator* view = (__bridge LVPagerIndicator *)(user->view);
        if( view ){
            if( lv_gettop(L)>=2 ) {
                int currentPage = lv_tonumber(L, 2);
                view.currentPage = currentPage-1;
                return 0;
            } else {
                lv_pushnumber(L, view.currentPage+1 );
                return 1;
            }
        }
    }
    return 0;
}

static int pageIndicatorTintColor(lv_State *L) {
    LVUserDataView * user = (LVUserDataView *)lv_touserdata(L, 1);
    if( user ){
        LVPagerIndicator* view = (__bridge LVPagerIndicator *)(user->view);
        if( view ){
            if( lv_gettop(L)>=2 ) {
                NSUInteger color = lv_tonumber(L, 2);
                float a = ( (color>>24)&0xff )/255.0;
                float r = ( (color>>16)&0xff )/255.0;
                float g = ( (color>>8)&0xff )/255.0;
                float b = ( (color>>0)&0xff )/255.0;
                if( a==0 ){
                    a = 1;
                }
                if( lv_gettop(L)>=3 ){
                    a = lv_tonumber(L, 3);
                }
                view.pageIndicatorTintColor = [UIColor colorWithRed:r green:g blue:b alpha:a];;
                return 0;
            } else {
                UIColor* color = view.pageIndicatorTintColor;
                NSUInteger c = 0;
                float a = 0;
                if( lv_uicolor2int(color, &c, &a) ){
                    lv_pushnumber(L, c );
                    lv_pushnumber(L, a );
                    return 2;
                }
            }
        }
    }
    return 0;
}

static int currentPageIndicatorTintColor(lv_State *L) {
    LVUserDataView * user = (LVUserDataView *)lv_touserdata(L, 1);
    if( user ){
        LVPagerIndicator* view = (__bridge LVPagerIndicator *)(user->view);
        if( view ){
            if( lv_gettop(L)>=2 ) {
                NSUInteger color = lv_tonumber(L, 2);
                float a = ( (color>>24)&0xff )/255.0;
                float r = ( (color>>16)&0xff )/255.0;
                float g = ( (color>>8)&0xff )/255.0;
                float b = ( (color>>0)&0xff )/255.0;
                if( a==0 ){
                    a = 1;
                }
                if( lv_gettop(L)>=3 ){
                    a = lv_tonumber(L, 3);
                }
                view.currentPageIndicatorTintColor = [UIColor colorWithRed:r green:g blue:b alpha:a];;
                return 0;
            } else {
                UIColor* color = view.currentPageIndicatorTintColor;
                NSUInteger c = 0;
                float a = 0;
                if( lv_uicolor2int(color, &c, &a) ){
                    lv_pushnumber(L, c );
                    lv_pushnumber(L, a );
                    return 2;
                }
            }
        }
    }
    return 0;
}

+(int) classDefine:(lv_State *)L {
    {
        lv_pushcfunction(L, lvNewPageControl);
        lv_setglobal(L, "UIPagerIndicator");
    }
    const struct lvL_reg memberFunctions [] = {
        {"setPageCount",  setPageCount },
        {"pageCount",     setPageCount },
        
        {"setCurrentPage",  setCurrentPage },
        {"currentPage",     setCurrentPage },
        
        {"setPageColor",     pageIndicatorTintColor },
        {"pageColor",     pageIndicatorTintColor },
        
        {"setCurrentPageColor",     currentPageIndicatorTintColor },
        {"currentPageColor",     currentPageIndicatorTintColor },
        {NULL, NULL}
    };
    
    lv_createClassMetaTable(L, META_TABLE_UIPageControl);
    
    lvL_openlib(L, NULL, [LVBaseView baseMemberFunctions], 0);
    lvL_openlib(L, NULL, memberFunctions, 0);
    return 1;
}


//----------------------------------------------------------------------------------------

@end
