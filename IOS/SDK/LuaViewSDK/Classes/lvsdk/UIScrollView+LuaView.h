/**
  * Created by LuaView.
  * Copyright (c) 2017, Alibaba Group. All rights reserved.
  *
  * This source code is licensed under the MIT.
  * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
  */

#import <UIKit/UIKit.h>
#import "LVHeads.h"
#import "UIView+LuaView.h"


@interface UIScrollView (UIScrollViewLuaView)<LVProtocal>


// 下拉刷新组件 需要重载的API
- (void) lv_initRefreshHeader;// 开启下拉刷新功能
- (void) lv_hiddenRefreshHeader:(BOOL) hidden;
- (void) lv_beginRefreshing;// 进入刷新状态
- (void) lv_endRefreshing;// 结束刷新状态
- (BOOL) lv_isRefreshing;// 是否正在刷新

// 加载更多组件 需要重载的API
- (void) lv_initRefreshFooter;// 开启上拉加载更多功能
- (void) lv_hiddenRefreshFooter:(BOOL) hidden;
- (void) lv_noticeNoMoreData;// 提示没有更多的数据
- (void) lv_resetNoMoreData;// 重置没有更多的数据（消除没有更多数据的状态）

/*
 * 回调脚本开始下拉刷新
 */
- (void) lv_refreshHeaderToRefresh;

/*
 * 回调脚本开始加载更多
 */
- (void) lv_refreshFooterToLoadMore;


@end
