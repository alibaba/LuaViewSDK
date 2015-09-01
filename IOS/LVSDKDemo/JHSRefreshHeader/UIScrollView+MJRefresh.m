
//  UIScrollView+MJRefresh.m
//  MJRefreshExample
//
//  Created by MJ Lee on 15/3/4.
//  Copyright (c) 2015年 小码哥. All rights reserved.
//

#import "UIScrollView+MJRefresh.h"
#import "JHSRefreshHeader.h"
#import "JHSRefreshFooter.h"
#import <objc/runtime.h>

@implementation UIScrollView (MJRefresh)

#pragma mark - header
static const char MJRefreshHeaderKey = '\0';
- (void)setHeader:(JHSRefreshHeader *)header
{
    if (header != self.header) {
        // 删除旧的，添加新的
        [self.header removeFromSuperview];
        [self addSubview:header];
        
        // 存储新的
        [self willChangeValueForKey:@"header"]; // KVO
        objc_setAssociatedObject(self, &MJRefreshHeaderKey,
                                 header, OBJC_ASSOCIATION_ASSIGN);
        [self didChangeValueForKey:@"header"]; // KVO
    }
}

- (JHSRefreshHeader *)header
{
    return objc_getAssociatedObject(self, &MJRefreshHeaderKey);
}

#pragma mark - footer
static const char MJRefreshFooterKey = '\0';
- (void)setFooter:(JHSRefreshFooter *)footer
{
    if (footer != self.footer) {
        // 删除旧的，添加新的
        [self.footer removeFromSuperview];
        [self addSubview:footer];
        
        // 存储新的
        [self willChangeValueForKey:@"footer"]; // KVO
        objc_setAssociatedObject(self, &MJRefreshFooterKey,
                                 footer, OBJC_ASSOCIATION_ASSIGN);
        [self didChangeValueForKey:@"footer"]; // KVO
    }
}

- (JHSRefreshFooter *)footer
{
    return objc_getAssociatedObject(self, &MJRefreshFooterKey);
}
@end
