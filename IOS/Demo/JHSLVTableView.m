//
//  JHSLVTableView.m
//  LVSDK
//
//  Created by dongxicheng on 9/2/15.
//  Copyright (c) 2015 dongxicheng. All rights reserved.
//

#import "JHSLVTableView.h"
#import "UIScrollView+MJRefresh.h"
#import <MJRefreshNormalHeader.h>


@implementation JHSLVTableView

// 下拉刷新
-(void) lv_initRefreshHeader{// 初始化下拉刷新功能
    MJRefreshNormalHeader* refreshHeader = [[MJRefreshNormalHeader alloc] init];
    self.header = refreshHeader;
    refreshHeader.refreshingBlock = ^(){
        [self lv_refreshHeaderToRefresh];
    };
}

- (void) lv_hiddenRefreshHeader:(BOOL) hidden{
    self.header.hidden = hidden;
}

- (void) lv_beginRefreshing{// 进入刷新状态
    [self.header beginRefreshing];
}

- (void) lv_endRefreshing{// 结束刷新状态
    [self.header endRefreshing];
}

- (BOOL) lv_isRefreshing{// 是否正在刷新
    return self.header.isRefreshing;
}
@end
