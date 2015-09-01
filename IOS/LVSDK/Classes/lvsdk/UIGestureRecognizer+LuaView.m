//
//  UIGestureRecognizer+LuaView.m
//  LVSDK
//
//  Created by dongxicheng on 7/24/15.
//  Copyright (c) 2015 dongxicheng. All rights reserved.
//

#import "UIGestureRecognizer+LuaView.h"
#import "LView.h"

@implementation UIGestureRecognizer(UIGestureRecognizerLuaView)

- (LView*) lv_lview{
    return nil;
}

- (void) setLv_lview:(LView *)lview{
}

- (LVUserDataView*)lv_userData{
    return nil;
}

- (void) setLv_userData:(LVUserDataView*)userData{
}


@end
