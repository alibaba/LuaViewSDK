
//  UIScrollView+MJRefresh.h
//  MJRefreshExample
//
//  Created by MJ Lee on 15/3/4.
//  Copyright (c) 2015年 小码哥. All rights reserved.
//  给ScrollView增加下拉刷新、上拉刷新的功能

#import <UIKit/UIKit.h>

@class JHSRefreshHeader, JHSRefreshFooter;

@interface UIScrollView (MJRefresh)
/** 下拉刷新控件 */
@property (strong, nonatomic) JHSRefreshHeader *header;
/** 上拉刷新控件 */
@property (strong, nonatomic) JHSRefreshFooter *footer;
@end
