/**
  * Created by LuaView.
  * Copyright (c) 2017, Alibaba Group. All rights reserved.
  *
  * This source code is licensed under the MIT.
  * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
  */

#import "UIScrollView+LuaView.h"
#import "LuaViewCore.h"
#import "NSObject+LuaView.h"


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
    [self lv_callLuaCallback:@"PullDown"];
}

- (void) lv_refreshFooterToLoadMore{
    // 开始上拉刷新调用
    [self lv_callLuaCallback:@"PullUpRefresh"];
}


@end
