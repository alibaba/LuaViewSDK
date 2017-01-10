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


// 下拉刷新
-(void) lv_initRefreshHeader{// 初始化下拉刷新功能
}

- (void) lv_hiddenRefreshHeader:(BOOL) hidden{
}

- (void) lv_beginRefreshing{// 进入刷新状态
}

- (void) lv_endRefreshing{// 结束刷新状态
}

- (BOOL) lv_isRefreshing{// 是否正在刷新
    return NO;
}

// 加载更多
- (void) lv_initRefreshFooter{// 初始化上拉加载更多功能
}
- (void) lv_hiddenRefreshFooter:(BOOL) hidden{
}

- (void) lv_noticeNoMoreData{// 提示没有更多的数据
}

- (void) lv_resetNoMoreData{// 重置没有更多的数据（消除没有更多数据的状态）
}

- (void) lv_refreshHeaderToRefresh{
    // 开始下拉刷新调用
    [self lv_callLuaByKey1:@"PullDown"];
}

- (void) lv_refreshFooterToLoadMore{
    // 开始上拉刷新调用
    [self lv_callLuaByKey1:@"PullUpRefresh"];
}


@end
