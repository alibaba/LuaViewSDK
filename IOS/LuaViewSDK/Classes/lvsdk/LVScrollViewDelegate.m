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
}

- (void)scrollViewWillBeginDragging:(UIScrollView *)scrollView{
    [self.owner lv_callLuaByKey1:@"ScrollBegin"];
}
- (void)scrollViewWillBeginDecelerating:(UIScrollView *)scrollView{
}

- (void)scrollViewDidEndDecelerating:(UIScrollView *)scrollView{
    [self.owner lv_callLuaByKey1:@"ScrollEnd"];
}

- (void)scrollViewDidEndScrollingAnimation:(UIScrollView *)scrollView{
    [self.owner lv_callLuaByKey1:@"ScrollEnd"];
}

- (void)scrollViewDidEndDragging:(UIScrollView *)scrollView willDecelerate:(BOOL)decelerate{
    if( !decelerate ) {
        [self.owner lv_callLuaByKey1:@"ScrollEnd"];
    }
}

@end
