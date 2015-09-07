//
//  UIView+LuaView.m
//  LVSDK
//
//  Created by dongxicheng on 7/24/15.
//  Copyright (c) 2015 dongxicheng. All rights reserved.
//

#import "UIView+LuaView.h"
#import "LView.h"

@implementation UIView (UIViewLuaView)

- (LView*) lv_lview{
    return nil;
}

- (void) setLv_lview:(LView *)lview{
}

- (LVUserDataView*)lv_userData{
    return nil;
}

- (void) setLv_userData:(LVUserDataView *)userData{
}

- (void) callLuaWithNoArgs:(NSString*) funcName{
    lv_State* l = self.lv_lview.l;
    if( l && self.lv_userData && funcName){
        lv_checkStack32(l);
        lv_pushUserdata(l, self.lv_userData);
        lv_pushUDataRef(l, USERDATA_KEY_DELEGATE);
        [LVUtil call:l key1:funcName.UTF8String key2:NULL nargs:0 nrets:0];
    }
}


@end
