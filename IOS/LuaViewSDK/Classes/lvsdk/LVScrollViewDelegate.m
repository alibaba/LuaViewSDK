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

- (void)scrollViewDidScroll:(UIScrollView *)scrollView{
    [self.owner lv_callLuaByKey1:@"Scrolling" key2:nil];
}

- (void)scrollViewWillBeginDragging:(UIScrollView *)scrollView{
    [self.owner lv_callLuaByKey1:@"ScrollBegin" key2:nil];
}
- (void)scrollViewWillBeginDecelerating:(UIScrollView *)scrollView{
}

- (void)scrollViewDidEndDecelerating:(UIScrollView *)scrollView{
    [self.owner lv_callLuaByKey1:@"ScrollEnd" key2:nil];
}

- (void)scrollViewDidEndScrollingAnimation:(UIScrollView *)scrollView{
    [self.owner lv_callLuaByKey1:@"ScrollEnd" key2:nil];
}

- (void)scrollViewDidEndDragging:(UIScrollView *)scrollView willDecelerate:(BOOL)decelerate{
    if( !decelerate ) {
        [self.owner lv_callLuaByKey1:@"ScrollEnd" key2:nil];
    }
}

@end
