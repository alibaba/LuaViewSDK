//
//  DemoPort.m
//  LVSDK
//
//  Created by dongxicheng on 2/11/15.
//  Copyright (c) 2015 dongxicheng. All rights reserved.
//

#import "DemoLuaViewDelegate.h"
#import "SubViewController.h"
#import "JHSRefreshHeader.h"
#import "UIScrollView+MJRefresh.h"
#import "LVScrollView.h"
#import "JHSRefreshAutoFooter.h"
#import "JHSRefreshStateHeader.h"
#import "JHSRefreshAutoStateFooter.h"

@implementation DemoLuaViewDelegate

-(void) setHeaderRefresh:(LVScrollView*) scrollView{
    JHSRefreshStateHeader* refreshHeader = [[JHSRefreshStateHeader alloc] init];
    scrollView.header = refreshHeader;
    scrollView.refreshHeader = refreshHeader;
}

-(void) setFooterRefresh:(LVScrollView*) scrollView{
    JHSRefreshAutoStateFooter* refreshFooter = [[JHSRefreshAutoStateFooter alloc] init];
    scrollView.footer = refreshFooter;
    scrollView.refreshFooter = refreshFooter;
}

@end
