/**
  * Created by LuaView.
  * Copyright (c) 2017, Alibaba Group. All rights reserved.
  *
  * This source code is licensed under the MIT.
  * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
  */

#import "LVScrollViewDelegate.h"
#import "UIView+LuaView.h"


@implementation LVScrollViewDelegate

-(id) init:(UIView*) tableView{
    self = [super init];
    if( self ){
        self.owner = tableView;
    }
    return self;
}

- (void)scrollViewDidScroll:(UIScrollView *)scrollView{
    [self.owner lv_callLuaCallback:@"Scrolling"];
    if( [self.delegate respondsToSelector:@selector(scrollViewDidScroll:)] ) {
        [self.delegate scrollViewDidScroll:scrollView];
    }
}

- (void)scrollViewWillBeginDecelerating:(UIScrollView *)scrollView{
    if( [self.delegate respondsToSelector:@selector(scrollViewWillBeginDecelerating:)] ) {
        [self.delegate scrollViewWillBeginDecelerating:scrollView];
    }
}

- (void)scrollViewDidEndDecelerating:(UIScrollView *)scrollView{
    [self.owner lv_callLuaCallback:@"ScrollEnd"];
    if( [self.delegate respondsToSelector:@selector(scrollViewDidEndDecelerating:)] ) {
        [self.delegate scrollViewDidEndDecelerating:scrollView];
    }
}

- (void)scrollViewDidEndScrollingAnimation:(UIScrollView *)scrollView{
    [self.owner lv_callLuaCallback:@"ScrollEnd"];
    if( [self.delegate respondsToSelector:@selector(scrollViewDidEndScrollingAnimation:)] ) {
        [self.delegate scrollViewDidEndScrollingAnimation:scrollView];
    }
}

- (void)scrollViewWillBeginDragging:(UIScrollView *)scrollView{
    [self.owner lv_callLuaCallback:@"ScrollBegin"];
    if( [self.delegate respondsToSelector:@selector(scrollViewWillBeginDragging:)] ) {
        [self.delegate scrollViewWillBeginDragging:scrollView];
    }
}

- (void)scrollViewWillEndDragging:(UIScrollView *)scrollView withVelocity:(CGPoint)velocity targetContentOffset:(inout CGPoint *)targetContentOffset {
    [self.owner lv_callLuaCallback:@"DragEnd"];
}

- (void)scrollViewDidEndDragging:(UIScrollView *)scrollView willDecelerate:(BOOL)decelerate{
    [self.owner lv_callLuaCallback:@"DragEnd"];
    if( !decelerate ) {
        [self.owner lv_callLuaCallback:@"ScrollEnd"];
    }
    if( [self.delegate respondsToSelector:@selector(scrollViewDidEndDragging:willDecelerate:)] ) {
        [self.delegate scrollViewDidEndDragging:scrollView willDecelerate:decelerate];
    }
}


@end
