//
//  LVScrollView.m
//  lv5.1.4
//
//  Created by dongxicheng on 12/18/14.
//  Copyright (c) 2014 dongxicheng. All rights reserved.
//

#import "LVScrollView.h"
#import "LVRegisterManager.h"
#import "LVBaseView.h"
#import "LVUtil.h"
#import "UIScrollView+LuaView.h"
#import "LVScrollViewDelegate.h"
#import "lV.h"
#import "lVauxlib.h"
#import "lVlib.h"
#import "lVstate.h"
#import "lVgc.h"

@interface LVScrollView ()
@property (nonatomic,strong) LVScrollViewDelegate* scrollViewDelegate;
@end

@implementation LVScrollView


-(id) init:(lv_State*) l{
    self = [super init];
    if( self ){
        self.lv_lview = (__bridge LView *)(l->lView);
        self.scrollViewDelegate = [[LVScrollViewDelegate alloc] init:self];
        self.delegate = self.scrollViewDelegate;
        self.alwaysBounceHorizontal = YES;
        self.alwaysBounceVertical = NO;
        self.showsHorizontalScrollIndicator = NO;
        self.showsVerticalScrollIndicator = NO;
        self.scrollsToTop = NO;
    }
    return self;
}

-(void) setLvScrollViewDelegate:(id)lvScrollViewDelegate{
    _lvScrollViewDelegate = lvScrollViewDelegate;
    self.scrollViewDelegate.delegate = lvScrollViewDelegate;
}

-(void) dealloc{
}

- (BOOL)touchesShouldCancelInContentView:(UIView *)view {
    return YES;
}

- (void) layoutSubviews{
    [super layoutSubviews];
    [self lv_alignSubviews];
    [self lv_runCallBack:STR_ON_LAYOUT];
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
        
        
        NEW_USERDATA(userData, View);
        userData->object = CFBridgingRetain(scrollView);
        scrollView.lv_userData = userData;
        
        //创建delegate用的事件存储器
        lv_createtable(L, 0, 0);
        lv_udataRef(L, USERDATA_KEY_DELEGATE );
        
        lvL_getmetatable(L, META_TABLE_UIScrollView );
        lv_setmetatable(L, -2);
        
        LView* view = (__bridge LView *)(L->lView);
        if( view ){
            [view containerAddSubview:scrollView];
        }
    }
    return 1; /* new userdatum is already on the stack */
}

static int contentSize (lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user ){
        UIScrollView* view = (__bridge UIScrollView *)(user->object);
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
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user ){
        UIScrollView* view = (__bridge UIScrollView *)(user->object);
        if( lv_gettop(L)>=2 ) {
            double x = lv_tonumber(L, 2);// 2
            double y = lv_tonumber(L, 3);// 3
            BOOL yes = NO;
            if( lv_gettop(L)>=4 )
                yes = lvL_checkbool(L, 4);// 3
            if( [view isKindOfClass:[UIScrollView class]] ){
                CGPoint p = CGPointMake(x, y);
                if( isNormalPoint(p) ) {
                    CGRect r = view.frame;
                    r.origin.x = x;
                    r.origin.y = y;
                    if( x > view.contentSize.width-view.frame.size.width ) {
                        x = view.contentSize.width-view.frame.size.width;
                    }
                    if( x < 0 ) {
                        x = 0;
                    }
                    if( y > view.contentSize.height-view.frame.size.height ) {
                        y = view.contentSize.height-view.frame.size.height;
                    }
                    if( y < 0 ) {
                        y = 0;
                    }
                    [view setContentOffset:CGPointMake(x, y) animated:yes];
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
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user ){
        UIScrollView* view = (__bridge UIScrollView *)(user->object);
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
                    view.scrollIndicatorInsets = edgeInsets;
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

//static int pageEnable (lv_State *L) {
//    LVUserDataView * user = (LVUserDataView *)lv_touserdata(L, 1);
//    if( user ){
//        UIScrollView* view = (__bridge UIScrollView *)(user->view);
//        if( [view isKindOfClass:[UIScrollView class]] ){
//            if( lv_gettop(L)>=2 ) {
//                BOOL yes = lvL_checkbool(L, 2);// 2
//                view.pagingEnabled = yes;
//                return 0;
//            } else {
//                lv_pushnumber(L, view.pagingEnabled );
//                return 1;
//            }
//        }
//    }
//    return 0;
//}

static int showScrollIndicator (lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user ){
        UIScrollView* view = (__bridge UIScrollView *)(user->object);
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

//static int initRefreshHeader (lv_State *L) {
//    LVUserDataView * user = (LVUserDataView *)lv_touserdata(L, 1);
//    if( user ){
//        UIScrollView* scrollView = (__bridge UIScrollView *)(user->view);
//        [scrollView lv_initRefreshHeader];
//    }
//    return 0;
//}

static int startHeaderRefreshing (lv_State *L){
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user ){
        LVScrollView* scrollView = (__bridge LVScrollView *)(user->object);
        [scrollView lv_beginRefreshing];
    }
    return 0;
}

static int stopHeaderRefreshing (lv_State *L){
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user ){
        LVScrollView* scrollView = (__bridge LVScrollView *)(user->object);
        [scrollView lv_endRefreshing];
    }
    return 0;
}

static int isHeaderRefreshing (lv_State *L){
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user ){
        LVScrollView* scrollView = (__bridge LVScrollView *)(user->object);
        BOOL yes = [scrollView lv_isRefreshing];
        lv_pushboolean(L, yes);
        return 1;
    }
    return 0;
}

//static int footerNoticeNoMoreData (lv_State *L){
//    LVUserDataView * user = (LVUserDataView *)lv_touserdata(L, 1);
//    if( user ){
//        LVScrollView* scrollView = (__bridge LVScrollView *)(user->view);
//        [scrollView lv_noticeNoMoreData];
//    }
//    return 0;
//}
//
//static int footerResetNoMoreData (lv_State *L){
//    LVUserDataView * user = (LVUserDataView *)lv_touserdata(L, 1);
//    if( user ){
//        LVScrollView* scrollView = (__bridge LVScrollView *)(user->view);
//        [scrollView lv_resetNoMoreData];
//    }
//    return 0;
//}
//
//static int hiddenRefreshFooter (lv_State *L){
//    LVUserDataView * user = (LVUserDataView *)lv_touserdata(L, 1);
//    if( user ){
//        LVScrollView* scrollView = (__bridge LVScrollView *)(user->view);
//        BOOL hidden = lv_toboolean(L, 2);
//        [scrollView lv_hiddenRefreshFooter:hidden];
//    }
//    return 0;
//}

//static int alwaysBounce(lv_State *L) {
//    LVUserDataView * user = (LVUserDataView *)lv_touserdata(L, 1);
//    if( user ){
//        UIScrollView* view = (__bridge UIScrollView *)(user->view);
//        if( [view isKindOfClass:[UIScrollView class]] ){
//            if( lv_gettop(L)>=2 ) {
//                BOOL yesVertical = lv_toboolean(L, 2);
//                BOOL yesHorizontal = lv_toboolean(L, 3);
//                view.alwaysBounceVertical = yesVertical;
//                view.alwaysBounceHorizontal = yesHorizontal;
//                return 0;
//            } else {
//                lv_pushboolean(L, view.alwaysBounceVertical);
//                lv_pushboolean(L, view.alwaysBounceHorizontal);
//                return 2;
//            }
//        }
//    }
//    return 0;
//}

static int callback (lv_State *L) {
    return lv_setCallbackByKey(L, STR_CALLBACK, NO);
}


static const struct lvL_reg memberFunctions [] = {
    {"callback",     callback },// 回调
    
    {"contentSize",     contentSize },//TODO
    {"offset",     contentOffset },//TODO
    
    {"contentInset",     contentInset },
    
    {"showScrollIndicator",     showScrollIndicator },
    
    // 下拉刷新
    //    {"initRefreshing", initRefreshHeader},
    {"startRefreshing", startHeaderRefreshing},
    {"stopRefreshing", stopHeaderRefreshing},
    {"isRefreshing", isHeaderRefreshing},
    
    // 上拉加载更多
    //    {"footerNoticeNoMoreData", footerNoticeNoMoreData},
    //    {"footerResetNoMoreData", footerResetNoMoreData},
    //    {"hiddenRefreshFooter", hiddenRefreshFooter},
    
    {NULL, NULL}
};

+(const struct lvL_reg*) memberFunctions{
    return memberFunctions;
}

+(int) classDefine:(lv_State *)L {
//    {
//        lv_pushcfunction(L, lvNewScrollView);
//        lv_setglobal(L, "ScrollView");
//    }
    {
        lv_pushcfunction(L, lvNewScrollView);
        lv_setglobal(L, "HScrollView");
    }
    {
        lv_pushcfunction(L, lvNewScrollView);
        lv_setglobal(L, "HorizontalScrollView");
    }
    
    lv_createClassMetaTable(L ,META_TABLE_UIScrollView);
    
    lvL_openlib(L, NULL, [LVBaseView baseMemberFunctions], 0);
    lvL_openlib(L, NULL, memberFunctions, 0);
    
    const char* keys[] = { "startRefreshing", "stopRefreshing", "isRefreshing", NULL};// 移除多余API
    lv_luaTableRemoveKeys(L, keys );
    return 1;
}


//----------------------------------------------------------------------------------------

-(NSString*) description{
    return [NSString stringWithFormat:@"<ScrollView(0x%x) frame = %@; contentSize = %@>", (int)[self hash],
            NSStringFromCGRect(self.frame),NSStringFromCGSize(self.contentSize) ];
}
@end
