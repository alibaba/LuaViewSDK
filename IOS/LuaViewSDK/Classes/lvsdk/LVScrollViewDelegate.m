//
//  LVScrollViewDelegate.m
//  LuaViewSDK
//
//  Created by dongxicheng on 10/21/15.
//  Copyright © 2015 dongxicheng. All rights reserved.
//

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
    [self.owner lv_callLuaByKey1:@"Scrolling"];
    if( [self.delegate respondsToSelector:@selector(scrollViewDidScroll:)] ) {
        [self.delegate performSelector:@selector(scrollViewDidScroll:) withObject:scrollView];
    }
}

- (void)scrollViewWillBeginDecelerating:(UIScrollView *)scrollView{
    if( [self.delegate respondsToSelector:@selector(scrollViewWillBeginDecelerating:)] ) {
        [self.delegate performSelector:@selector(scrollViewWillBeginDecelerating:) withObject:scrollView];
    }
}

- (void)scrollViewDidEndDecelerating:(UIScrollView *)scrollView{
    [self.owner lv_callLuaByKey1:@"ScrollEnd"];
    if( [self.delegate respondsToSelector:@selector(scrollViewDidEndDecelerating:)] ) {
        [self.delegate performSelector:@selector(scrollViewDidEndDecelerating:) withObject:scrollView];
    }
}

- (void)scrollViewDidEndScrollingAnimation:(UIScrollView *)scrollView{
    [self.owner lv_callLuaByKey1:@"ScrollEnd"];
    if( [self.delegate respondsToSelector:@selector(scrollViewDidEndScrollingAnimation:)] ) {
        [self.delegate performSelector:@selector(scrollViewDidEndScrollingAnimation:) withObject:scrollView];
    }
}

- (void)scrollViewWillBeginDragging:(UIScrollView *)scrollView{
    [self.owner lv_callLuaByKey1:@"ScrollBegin"];
    if( [self.delegate respondsToSelector:@selector(scrollViewWillBeginDragging:)] ) {
        [self.delegate performSelector:@selector(scrollViewWillBeginDragging:) withObject:scrollView];
    }
}

- (void)scrollViewDidEndDragging:(UIScrollView *)scrollView willDecelerate:(BOOL)decelerate{
    [self.owner lv_callLuaByKey1:@"DragEnd"];
    if( !decelerate ) {
        [self.owner lv_callLuaByKey1:@"ScrollEnd"];
    }
    if( [self.delegate respondsToSelector:@selector(scrollViewDidEndDragging:willDecelerate:)] ) {
        [self.delegate performSelector:@selector(scrollViewDidEndDragging:willDecelerate:) withObject:scrollView withObject:@(decelerate)];
    }
}


@end
