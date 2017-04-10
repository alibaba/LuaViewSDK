/**
  * Created by LuaView.
  * Copyright (c) 2017, Alibaba Group. All rights reserved.
  *
  * This source code is licensed under the MIT.
  * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
  */

#import "LVScrollView.h"
#import "LVBaseView.h"
#import "LVUtil.h"
#import "UIScrollView+LuaView.h"
#import "LVScrollViewDelegate.h"
#import "LVHeads.h"
#import "LView.h"

@interface LVScrollView ()
@property (nonatomic,strong) LVScrollViewDelegate* scrollViewDelegate;
@end

@implementation LVScrollView


-(id) init:(lua_State*) l{
    self = [super init];
    if( self ){
        self.lv_luaviewCore = LV_LUASTATE_VIEW(l);
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
    [self lv_callLuaCallback:@STR_ON_LAYOUT];
}

#pragma -mark ScrollView
static int lvNewScrollView (lua_State *L) {
    {
        Class c = [LVUtil upvalueClass:L defaultClass:[LVScrollView class]];
        
        LVScrollView* scrollView = [[c alloc] init:L];
        
        NEW_USERDATA(userData, View);
        userData->object = CFBridgingRetain(scrollView);
        scrollView.lv_userData = userData;
        
        //创建delegate用的事件存储器
        lua_createtable(L, 0, 0);
        lv_udataRef(L, USERDATA_KEY_DELEGATE );
        
        luaL_getmetatable(L, META_TABLE_UIScrollView );
        lua_setmetatable(L, -2);
        
        LuaViewCore* view = LV_LUASTATE_VIEW(L);
        if( view ){
            [view containerAddSubview:scrollView];
        }
    }
    return 1; /* new userdatum is already on the stack */
}

static int contentSize (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( user ){
        UIScrollView* view = (__bridge UIScrollView *)(user->object);
        if( [view isKindOfClass:[UIScrollView class]] ){
            if( lua_gettop(L)>=2 ) {
                double w = lua_tonumber(L, 2);// 2
                double h = lua_tonumber(L, 3);// 3
                CGSize s = CGSizeMake( w, h );
                if ( isNormalSize(s) ) {
                    view.contentSize = s;
                }
                return 0;
            } else {
                CGSize s = view.contentSize;
                lua_pushnumber(L, s.width   );
                lua_pushnumber(L, s.height    );
                return 2;
            }
        }
    }
    return 0;
}

static int contentOffset (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( user ){
        UIScrollView* view = (__bridge UIScrollView *)(user->object);
        if( lua_gettop(L)>=2 ) {
            double x = lua_tonumber(L, 2);// 2
            double y = lua_tonumber(L, 3);// 3
            BOOL yes = NO;
            if( lua_gettop(L)>=4 )
                yes = lua_toboolean(L, 4);// 3
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
            lua_pushnumber(L, p.x   );
            lua_pushnumber(L, p.y    );
            return 2;
        }
    }
    return 0;
}

static int contentInset (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( user ){
        UIScrollView* view = (__bridge UIScrollView *)(user->object);
        if( [view isKindOfClass:[UIScrollView class]] ){
            int num = lua_gettop(L);
            if( num>=2 ) {
                UIEdgeInsets edgeInsets = view.contentInset;
                if( num>=2 )
                    edgeInsets.top = lua_tonumber(L, 2);
                if( num>=3 )
                    edgeInsets.left = lua_tonumber(L, 3);
                if( num>=4 )
                    edgeInsets.bottom = lua_tonumber(L, 4);
                if( num>=5 )
                    edgeInsets.right = lua_tonumber(L, 5);
                if( isNormalEdgeInsets(edgeInsets) ) {
                    view.contentInset = edgeInsets;
                    view.scrollIndicatorInsets = edgeInsets;
                }
                return 0;
            } else {
                UIEdgeInsets edgeInsets = view.contentInset;
                lua_pushnumber(L, edgeInsets.top   );
                lua_pushnumber(L, edgeInsets.left   );
                lua_pushnumber(L, edgeInsets.bottom   );
                lua_pushnumber(L, edgeInsets.right   );
                return 4;
            }
        }
    }
    return 0;
}

//static int pageEnable (lua_State *L) {
//    LVUserDataView * user = (LVUserDataView *)lua_touserdata(L, 1);
//    if( user ){
//        UIScrollView* view = (__bridge UIScrollView *)(user->view);
//        if( [view isKindOfClass:[UIScrollView class]] ){
//            if( lua_gettop(L)>=2 ) {
//                BOOL yes = lua_toboolean(L, 2);// 2
//                view.pagingEnabled = yes;
//                return 0;
//            } else {
//                lua_pushnumber(L, view.pagingEnabled );
//                return 1;
//            }
//        }
//    }
//    return 0;
//}

static int showScrollIndicator (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( user ){
        UIScrollView* view = (__bridge UIScrollView *)(user->object);
        if( [view isKindOfClass:[UIScrollView class]] ){
            if( lua_gettop(L)>=2 ) {
                BOOL yes1 = lua_toboolean(L, 2);
                BOOL yes2 = lua_toboolean(L, 3);
                view.showsHorizontalScrollIndicator = yes1;
                view.showsVerticalScrollIndicator = yes2;
                return 0;
            } else {
                lua_pushboolean(L, view.showsHorizontalScrollIndicator );
                lua_pushboolean(L, view.showsVerticalScrollIndicator );
                return 2;
            }
        }
    }
    return 0;
}

//static int initRefreshHeader (lua_State *L) {
//    LVUserDataView * user = (LVUserDataView *)lua_touserdata(L, 1);
//    if( user ){
//        UIScrollView* scrollView = (__bridge UIScrollView *)(user->view);
//        [scrollView lv_initRefreshHeader];
//    }
//    return 0;
//}

static int startHeaderRefreshing (lua_State *L){
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( user ){
        LVScrollView* scrollView = (__bridge LVScrollView *)(user->object);
        [scrollView lv_beginRefreshing];
    }
    return 0;
}

static int stopHeaderRefreshing (lua_State *L){
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( user ){
        LVScrollView* scrollView = (__bridge LVScrollView *)(user->object);
        [scrollView lv_endRefreshing];
    }
    return 0;
}

static int isHeaderRefreshing (lua_State *L){
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( user ){
        LVScrollView* scrollView = (__bridge LVScrollView *)(user->object);
        BOOL yes = [scrollView lv_isRefreshing];
        lua_pushboolean(L, yes);
        return 1;
    }
    return 0;
}

//static int footerNoticeNoMoreData (lua_State *L){
//    LVUserDataView * user = (LVUserDataView *)lua_touserdata(L, 1);
//    if( user ){
//        LVScrollView* scrollView = (__bridge LVScrollView *)(user->view);
//        [scrollView lv_noticeNoMoreData];
//    }
//    return 0;
//}
//
//static int footerResetNoMoreData (lua_State *L){
//    LVUserDataView * user = (LVUserDataView *)lua_touserdata(L, 1);
//    if( user ){
//        LVScrollView* scrollView = (__bridge LVScrollView *)(user->view);
//        [scrollView lv_resetNoMoreData];
//    }
//    return 0;
//}
//
//static int hiddenRefreshFooter (lua_State *L){
//    LVUserDataView * user = (LVUserDataView *)lua_touserdata(L, 1);
//    if( user ){
//        LVScrollView* scrollView = (__bridge LVScrollView *)(user->view);
//        BOOL hidden = lua_toboolean(L, 2);
//        [scrollView lv_hiddenRefreshFooter:hidden];
//    }
//    return 0;
//}

//static int alwaysBounce(lua_State *L) {
//    LVUserDataView * user = (LVUserDataView *)lua_touserdata(L, 1);
//    if( user ){
//        UIScrollView* view = (__bridge UIScrollView *)(user->view);
//        if( [view isKindOfClass:[UIScrollView class]] ){
//            if( lua_gettop(L)>=2 ) {
//                BOOL yesVertical = lua_toboolean(L, 2);
//                BOOL yesHorizontal = lua_toboolean(L, 3);
//                view.alwaysBounceVertical = yesVertical;
//                view.alwaysBounceHorizontal = yesHorizontal;
//                return 0;
//            } else {
//                lua_pushboolean(L, view.alwaysBounceVertical);
//                lua_pushboolean(L, view.alwaysBounceHorizontal);
//                return 2;
//            }
//        }
//    }
//    return 0;
//}

static int callback (lua_State *L) {
    return lv_setCallbackByKey(L, nil, NO);
}

static void releaseUserDataView(LVUserDataInfo* userdata){
    if( userdata && userdata->object ){
        UIView<LVProtocal>* view = CFBridgingRelease(userdata->object);
        userdata->object = NULL;
        if( view ){
            view.lv_userData = nil;
            view.lv_luaviewCore = nil;
            [view removeFromSuperview];
            [view.layer removeFromSuperlayer];
            if( [view isKindOfClass:[UICollectionView class]] ) {
                UICollectionView* collectionView = (UICollectionView*)view;
                collectionView.delegate = nil;
                collectionView.dataSource = nil;
                collectionView.scrollEnabled = NO;
            } else if( [view isKindOfClass:[UIScrollView class]] ) {
                UIScrollView* scrollView = (UIScrollView*)view;
                scrollView.delegate = nil;
                scrollView.scrollEnabled = NO;
            }
        }
    }
}

#pragma -mark __gc
static int __gc (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    releaseUserDataView(user);
    return 0;
}

static const struct luaL_Reg memberFunctions [] = {
    {"callback",     callback },// 回调
    {"initParams",   callback },// 回调
    
    {"contentSize",     contentSize },// for IOS
    {"offset",     contentOffset },//
    // offsetBy
    
    {"contentInset",     contentInset },// for IOS
    
    {"showScrollIndicator",     showScrollIndicator },// for IOS
    
    // 下拉刷新
    //    {"initRefreshing", initRefreshHeader},
    {"startRefreshing", startHeaderRefreshing},// for IOS ScrollView
    {"stopRefreshing", stopHeaderRefreshing},// for IOS ScrollView
    {"isRefreshing", isHeaderRefreshing},// for IOS ScrollView
    
    // 上拉加载更多
    //    {"footerNoticeNoMoreData", footerNoticeNoMoreData},
    //    {"footerResetNoMoreData", footerResetNoMoreData},
    //    {"hiddenRefreshFooter", hiddenRefreshFooter},
    
    {"__gc",        __gc },
    {NULL, NULL}
};

+(const struct luaL_Reg*) memberFunctions{
    return memberFunctions;
}

+(int) lvClassDefine:(lua_State *)L globalName:(NSString*) globalName{
    [LVUtil reg:L clas:self cfunc:lvNewScrollView globalName:globalName defaultName:@"HScrollView"];
    [LVUtil reg:L clas:self cfunc:lvNewScrollView globalName:globalName defaultName:@"HorizontalScrollView"];
    
    lv_createClassMetaTable(L ,META_TABLE_UIScrollView);
    
    luaL_openlib(L, NULL, [LVBaseView baseMemberFunctions], 0);
    luaL_openlib(L, NULL, memberFunctions, 0);
    
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
