//
//  UIScrollView+luaview.m
//  LVSDK
//
//  Created by dongxicheng on 7/7/15.
//  Copyright (c) 2015 dongxicheng. All rights reserved.
//

#import "UIScrollView+LuaView.h"
#import "LView.h"

@implementation UIScrollView (UIScrollViewLuaView)

- (id<LVRefreshHeaderProtocol>) refreshHeader{
    return nil;
}

- (void) setRefreshHeader:(id<LVRefreshHeaderProtocol>)head{
    __weak typeof(self) wself = self;
    head.refreshingBlock = ^(){
        [wself luaViewPullDownToRefresh];
    };
}

- (void) luaViewPullDownToRefresh{
    // 开始下拉刷新调用
    [self callLuaWithNoArgs:HEADER_BEGIN_REFRESHING];
}

- (void) luaViewPullUpToLoadMore{
    // 开始上拉刷新调用
    [self callLuaWithNoArgs:FOOTER_BEGIN_REFRESHING];
}

- (id<LVRefreshFooterProtocol>) refreshFooter{
    return nil;
}

- (void) setRefreshFooter:(id<LVRefreshFooterProtocol>)foot{
    __weak typeof(self) wself = self;
    foot.refreshingBlock = ^(){
        [wself luaViewPullUpToLoadMore];
    };
}


- (void)scrollViewDidScroll:(UIScrollView *)scrollView{
    [self callLuaWithNoArgs:@"Scrolling"];
}

- (void)scrollViewWillBeginDragging:(UIScrollView *)scrollView{
    [self callLuaWithNoArgs:@"BeginDragging"];
}
- (void)scrollViewWillBeginDecelerating:(UIScrollView *)scrollView{
    [self callLuaWithNoArgs:@"BeginDecelerating"];
}

- (void)scrollViewDidEndDecelerating:(UIScrollView *)scrollView{
    [self callLuaWithNoArgs:@"EndScrolling"];
}

- (void)scrollViewDidEndScrollingAnimation:(UIScrollView *)scrollView{
    [self callLuaWithNoArgs:@"EndScrolling"];
}


- (void) luaViewInitRefreshHeader{
}

- (void) luaViewInitRefreshFooter{
}

@end
