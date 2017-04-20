/**
  * Created by LuaView.
  * Copyright (c) 2017, Alibaba Group. All rights reserved.
  *
  * This source code is licensed under the MIT.
  * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
  */

#import "LVEmptyRefreshCollectionView.h"
#import "LVRefreshHeader.h"

@implementation LVEmptyRefreshCollectionView

// 下拉刷新
-(void) lv_initRefreshHeader{// 初始化下拉刷新功能
    LVRefreshHeader* refreshHeader = [[LVRefreshHeader alloc] init];
    self.lv_refresh_header = refreshHeader;
    
    __weak typeof(self) weakSelf = self;
    refreshHeader.refreshingBlock = ^(){
        [weakSelf lv_refreshHeaderToRefresh];
    };
}

- (void) lv_hiddenRefreshHeader:(BOOL) hidden{
    self.lv_refresh_header.hidden = hidden;
}

- (void) lv_beginRefreshing{// 进入刷新状态
    [self.lv_refresh_header beginRefreshing];
}

- (void) lv_endRefreshing{// 结束刷新状态
    [self.lv_refresh_header endRefreshing];
}

- (BOOL) lv_isRefreshing{// 是否正在刷新
    return self.lv_refresh_header.isRefreshing;
}

+(NSString*) globalName{
    return @"EmptyRefreshCollectionView";
}

@end
