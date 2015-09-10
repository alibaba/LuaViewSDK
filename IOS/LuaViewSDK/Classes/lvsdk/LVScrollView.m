//
//  LVScrollView.m
//  lv5.1.4
//
//  Created by dongxicheng on 12/18/14.
//  Copyright (c) 2014 dongxicheng. All rights reserved.
//

#import "LVScrollView.h"
#import "LVFunctionRegister.h"
#import "LVBaseView.h"
#import "LVUtil.h"
#import "UIScrollView+LuaView.h"

#define KEY_LUA_INFO 1

@implementation LVScrollView


-(id) init:(lv_State*) l{
    self = [super init];
    if( self ){
        self.lv_lview = (__bridge LView *)(l->lView);
        self.delegate = self;
    }
    return self;
}

-(void) dealloc{
}

- (BOOL)touchesShouldCancelInContentView:(UIView *)view {
    return YES;
}

static Class g_class = nil;

+ (void) setDefaultStyle:(Class) c{
    if( [c isSubclassOfClass:[LVScrollView class]] ) {
        g_class = c;
    }
}

#pragma -mark ScrollView
static int lvNewScrollView (lv_State *L) {
    {
        if( g_class == nil )
            g_class = [LVScrollView class];
        LVScrollView* scrollView = [[g_class alloc] init:L];
        
        
        NEW_USERDATA(userData, LVUserDataView);
        userData->view = CFBridgingRetain(scrollView);
        scrollView.lv_userData = userData;
        
        if ( lv_gettop(L)>=2 ) {
            lv_pushvalue(L, 1);
            lv_udataRef(L, KEY_LUA_INFO );
        }
        
        lvL_getmetatable(L, META_TABLE_UIScrollView );
        lv_setmetatable(L, -2);
        
        UIView* view = (__bridge UIView *)(L->lView);
        if( view ){
            [view addSubview:scrollView];
        }
    }
    return 1; /* new userdatum is already on the stack */
}

static int contentSize (lv_State *L) {
    LVUserDataView * user = (LVUserDataView *)lv_touserdata(L, 1);
    if( user ){
        UIScrollView* view = (__bridge UIScrollView *)(user->view);
        if( [view isKindOfClass:[UIScrollView class]] ){
            if( lv_gettop(L)>=2 ) {
                double w = lv_tonumber(L, 2);// 2
                double h = lv_tonumber(L, 3);// 3
                CGSize s = CGSizeMake( w, h );
                if ( isNormalSize(s) ) {
                    view.contentSize = s;
                }
                return 0;
            } else {
                CGSize s = view.contentSize;
                lv_pushnumber(L, s.width   );
                lv_pushnumber(L, s.height    );
                return 2;
            }
        }
    }
    return 0;
}

static int contentOffset (lv_State *L) {
    LVUserDataView * user = (LVUserDataView *)lv_touserdata(L, 1);
    if( user ){
        UIScrollView* view = (__bridge UIScrollView *)(user->view);
        if( lv_gettop(L)>=2 ) {
            double x = lv_tonumber(L, 2);// 2
            double y = lv_tonumber(L, 3);// 3
            BOOL yes = NO;
            if( lv_gettop(L)>=4 )
                yes = lvL_checkbool(L, 4);// 3
            if( [view isKindOfClass:[UIScrollView class]] ){
                CGPoint p = CGPointMake(x, y);
                if( isNormalPoint(p) ) {
                    [view setContentOffset:p animated:yes];
                }
                return 0;
            }
        } else {
            CGPoint p = view.contentOffset;
            lv_pushnumber(L, p.x   );
            lv_pushnumber(L, p.y    );
            return 2;
        }
    }
    return 0;
}

static int contentInset (lv_State *L) {
    LVUserDataView * user = (LVUserDataView *)lv_touserdata(L, 1);
    if( user ){
        UIScrollView* view = (__bridge UIScrollView *)(user->view);
        if( [view isKindOfClass:[UIScrollView class]] ){
            int num = lv_gettop(L);
            if( num>=2 ) {
                UIEdgeInsets edgeInsets = view.contentInset;
                if( num>=2 )
                    edgeInsets.top = lv_tonumber(L, 2);
                if( num>=3 )
                    edgeInsets.left = lv_tonumber(L, 3);
                if( num>=4 )
                    edgeInsets.bottom = lv_tonumber(L, 4);
                if( num>=5 )
                    edgeInsets.right = lv_tonumber(L, 5);
                if( isNormalEdgeInsets(edgeInsets) ) {
                    view.contentInset = edgeInsets;
                }
                return 0;
            } else {
                UIEdgeInsets edgeInsets = view.contentInset;
                lv_pushnumber(L, edgeInsets.top   );
                lv_pushnumber(L, edgeInsets.left   );
                lv_pushnumber(L, edgeInsets.bottom   );
                lv_pushnumber(L, edgeInsets.right   );
                return 4;
            }
        }
    }
    return 0;
}

static int scrollIndicatorInsets (lv_State *L) {
    LVUserDataView * user = (LVUserDataView *)lv_touserdata(L, 1);
    if( user ){
        UIScrollView* view = (__bridge UIScrollView *)(user->view);
        if( [view isKindOfClass:[UIScrollView class]] ){
            int num = lv_gettop(L);
            if( num>=2 ) {
                UIEdgeInsets edgeInsets = view.scrollIndicatorInsets;
                if( num>=2 )
                    edgeInsets.top = lv_tonumber(L, 2);
                if( num>=3 )
                    edgeInsets.left = lv_tonumber(L, 3);
                if( num>=4 )
                    edgeInsets.bottom = lv_tonumber(L, 4);
                if( num>=5 )
                    edgeInsets.right = lv_tonumber(L, 5);
                if( isNormalEdgeInsets(edgeInsets )  ) {
                    view.scrollIndicatorInsets = edgeInsets;
                }
                return 0;
            } else {
                UIEdgeInsets edgeInsets = view.scrollIndicatorInsets;
                lv_pushnumber(L, edgeInsets.top   );
                lv_pushnumber(L, edgeInsets.left   );
                lv_pushnumber(L, edgeInsets.bottom   );
                lv_pushnumber(L, edgeInsets.right   );
                return 4;
            }
        }
    }
    return 0;
}

static int pageEnable (lv_State *L) {
    LVUserDataView * user = (LVUserDataView *)lv_touserdata(L, 1);
    if( user ){
        UIScrollView* view = (__bridge UIScrollView *)(user->view);
        if( [view isKindOfClass:[UIScrollView class]] ){
            if( lv_gettop(L)>=2 ) {
                BOOL yes = lvL_checkbool(L, 2);// 2
                view.pagingEnabled = yes;
                return 0;
            } else {
                lv_pushnumber(L, view.pagingEnabled );
                return 1;
            }
        }
    }
    return 0;
}

static int showScrollIndicator (lv_State *L) {
    LVUserDataView * user = (LVUserDataView *)lv_touserdata(L, 1);
    if( user ){
        UIScrollView* view = (__bridge UIScrollView *)(user->view);
        if( [view isKindOfClass:[UIScrollView class]] ){
            if( lv_gettop(L)>=2 ) {
                BOOL yes1 = lv_toboolean(L, 2);
                BOOL yes2 = lv_toboolean(L, 3);
                view.showsHorizontalScrollIndicator = yes1;
                view.showsVerticalScrollIndicator = yes2;
                return 0;
            } else {
                lv_pushboolean(L, view.showsHorizontalScrollIndicator );
                lv_pushboolean(L, view.showsVerticalScrollIndicator );
                return 2;
            }
        }
    }
    return 0;
}

static int initRefreshHeader (lv_State *L) {
    LVUserDataView * user = (LVUserDataView *)lv_touserdata(L, 1);
    if( user ){
        UIScrollView* scrollView = (__bridge UIScrollView *)(user->view);
        [scrollView lv_initRefreshHeader];
    }
    return 0;
}

static int initRefreshFooter (lv_State *L){
    LVUserDataView * user = (LVUserDataView *)lv_touserdata(L, 1);
    if( user ){
        UIScrollView* scrollView = (__bridge UIScrollView *)(user->view);
        [scrollView lv_initRefreshFooter];
    }
    return 0;
}



static int headerBeginRefreshing (lv_State *L){
    LVUserDataView * user = (LVUserDataView *)lv_touserdata(L, 1);
    if( user ){
        LVScrollView* scrollView = (__bridge LVScrollView *)(user->view);
        [scrollView lv_beginRefreshing];
    }
    return 0;
}

static int headerEndRefreshing (lv_State *L){
    LVUserDataView * user = (LVUserDataView *)lv_touserdata(L, 1);
    if( user ){
        LVScrollView* scrollView = (__bridge LVScrollView *)(user->view);
        [scrollView lv_endRefreshing];
    }
    return 0;
}

static int footerNoticeNoMoreData (lv_State *L){
    LVUserDataView * user = (LVUserDataView *)lv_touserdata(L, 1);
    if( user ){
        LVScrollView* scrollView = (__bridge LVScrollView *)(user->view);
        [scrollView lv_noticeNoMoreData];
    }
    return 0;
}

static int footerResetNoMoreData (lv_State *L){
    LVUserDataView * user = (LVUserDataView *)lv_touserdata(L, 1);
    if( user ){
        LVScrollView* scrollView = (__bridge LVScrollView *)(user->view);
        [scrollView lv_resetNoMoreData];
    }
    return 0;
}


static int hiddenRefreshHeader (lv_State *L){
    LVUserDataView * user = (LVUserDataView *)lv_touserdata(L, 1);
    if( user ){
        LVScrollView* scrollView = (__bridge LVScrollView *)(user->view);
        BOOL hidden = lv_toboolean(L, 2);
        [scrollView lv_hiddenRefreshHeader:hidden];
    }
    return 0;
}


static int hiddenRefreshFooter (lv_State *L){
    LVUserDataView * user = (LVUserDataView *)lv_touserdata(L, 1);
    if( user ){
        LVScrollView* scrollView = (__bridge LVScrollView *)(user->view);
        BOOL hidden = lv_toboolean(L, 2);
        [scrollView lv_hiddenRefreshFooter:hidden];
    }
    return 0;
}

static int alwaysBounce(lv_State *L) {
    LVUserDataView * user = (LVUserDataView *)lv_touserdata(L, 1);
    if( user ){
        UIScrollView* view = (__bridge UIScrollView *)(user->view);
        if( [view isKindOfClass:[UIScrollView class]] ){
            if( lv_gettop(L)>=2 ) {
                BOOL yesVertical = lv_toboolean(L, 2);
                BOOL yesHorizontal = lv_toboolean(L, 3);
                view.alwaysBounceVertical = yesVertical;
                view.alwaysBounceHorizontal = yesHorizontal;
                return 0;
            } else {
                lv_pushboolean(L, view.alwaysBounceVertical);
                lv_pushboolean(L, view.alwaysBounceHorizontal);
                return 2;
            }
        }
    }
    return 0;
}

static int scrollRectToVisible (lv_State *L) {
    if( lv_gettop(L)>=5 ) {
        LVUserDataView * user = (LVUserDataView *)lv_touserdata(L, 1);
        double x = lv_tonumber(L, 2);// 2
        double y = lv_tonumber(L, 3);// 3
        double w = lv_tonumber(L, 4);// 3
        double h = lv_tonumber(L, 5);// 3
        BOOL yes = NO;
        if( lv_gettop(L)>=6 ) {
            yes = lv_toboolean(L, 6);
        }
        if( user ){
            UIScrollView* view = nil;
            view = (__bridge UIScrollView *)(user->view);
            if( [view isKindOfClass:[UIScrollView class]] ){
                CGRect r = {0};
                r.origin.x = x;
                r.origin.y = y;
                r.size.width = w;
                r.size.height = h;
                if( isNormalRect(r) ) {
                    [view scrollRectToVisible:r animated:yes];
                }
                return 0;
            }
        }
    }
    return 0;
}

static const struct lvL_reg memberFunctions [] = {
    {"setContentSize",  contentSize },
    {"contentSize",     contentSize },
    
    {"setContentOffset",  contentOffset },
    {"contentOffset",     contentOffset },
    
    {"setContentInset",  contentInset },
    {"contentInset",     contentInset },
    
    {"setScrollIndicatorInsets",  scrollIndicatorInsets },
    {"scrollIndicatorInsets",     scrollIndicatorInsets },
    
    {"setPageEnable",  pageEnable },
    {"pageEnable",     pageEnable },
    
    {"setShowScrollIndicator",  showScrollIndicator },
    {"showScrollIndicator",     showScrollIndicator },
    
    {"setAlwaysBounce", alwaysBounce },
    {"alwaysBounce",    alwaysBounce },
    
    {"initRefreshHeader", initRefreshHeader},
    {"initRefreshFooter", initRefreshFooter},
    
    {"headerBeginRefreshing", headerBeginRefreshing},
    {"headerEndRefreshing", headerEndRefreshing},
    {"hiddenRefreshHeader", hiddenRefreshHeader},
    
    {"footerNoticeNoMoreData", footerNoticeNoMoreData},
    {"footerResetNoMoreData", footerResetNoMoreData},
    {"hiddenRefreshFooter", hiddenRefreshFooter},
    
    {"scrollRectToVisible",     scrollRectToVisible },
    
    
    {"initPullDownRefresh", initRefreshHeader},
    {"initPullUpRefresh", initRefreshFooter},
    
    {"beginPullDownRefreshing", headerBeginRefreshing},
    {"endPullDownRefreshing", headerEndRefreshing},
    {"hiddenPullDownRefreshing", hiddenRefreshHeader},
    
    {"footerNoticeNoMoreData", footerNoticeNoMoreData},
    {"footerResetNoMoreData", footerResetNoMoreData},
    {"hiddenRefreshFooter", hiddenRefreshFooter},
    
    {NULL, NULL}
};

+(const struct lvL_reg*) memberFunctions{
    return memberFunctions;
}

+(int) classDefine:(lv_State *)L {
    {
        lv_pushcfunction(L, lvNewScrollView);
        lv_setglobal(L, "UIScrollView");
    }
    
    lv_createClassMetaTable(L ,META_TABLE_UIScrollView);
    
    lvL_openlib(L, NULL, [LVBaseView baseMemberFunctions], 0);
    lvL_openlib(L, NULL, memberFunctions, 0);
    return 1;
}


//----------------------------------------------------------------------------------------

@end
