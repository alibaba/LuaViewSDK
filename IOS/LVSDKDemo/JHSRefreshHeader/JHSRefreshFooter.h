
//  MJRefreshFooter.h
//  MJRefreshExample
//
//  Created by MJ Lee on 15/3/5.
//  Copyright (c) 2015年 小码哥. All rights reserved.
//  上拉刷新控件

#import "JHSRefreshComponent.h"

@interface JHSRefreshFooter : JHSRefreshComponent
/** 创建footer */
+ (instancetype)footerWithRefreshingBlock:(MJRefreshComponentRefreshingBlock)refreshingBlock;
/** 创建footer */
+ (instancetype)footerWithRefreshingTarget:(id)target refreshingAction:(SEL)action;

/** 提示没有更多的数据 */
- (void)noticeNoMoreData;
/** 重置没有更多的数据（消除没有更多数据的状态） */
- (void)resetNoMoreData;
@end
