//
//  UIScrollView+luaview.h
//  LVSDK
//
//  Created by dongxicheng on 7/7/15.
//  Copyright (c) 2015 dongxicheng. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "LVHeads.h"
#import "LVApiDelegate.h"
#import "UIView+LuaView.h"


@interface UIScrollView (UIScrollViewLuaView)<LVProtocal>

@property(nonatomic,weak) id<LVRefreshHeaderProtocol> refreshHeader;
@property(nonatomic,weak) id<LVRefreshFooterProtocol> refreshFooter;

- (void) luaViewPullDownToRefresh;
- (void) luaViewPullUpToLoadMore;

- (void) luaViewInitRefreshHeader;
- (void) luaViewInitRefreshFooter;

@end
