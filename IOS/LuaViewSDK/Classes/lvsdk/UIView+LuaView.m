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
    [self callLuaWithNoArgs:funcName key2:nil];
}

- (void) callLuaWithNoArgs:(NSString*) funcName key2:(NSString*) key2{
    lv_State* l = self.lv_lview.l;
    if( l && self.lv_userData && funcName){
        lv_checkStack32(l);
        lv_pushUserdata(l, self.lv_userData);
        lv_pushUDataRef(l, USERDATA_KEY_DELEGATE);
        [LVUtil call:l key1:funcName.UTF8String key2:key2.UTF8String nargs:0 nrets:0];
    }
}

// rotation
- (CGFloat) lv_rotation{
    return 0;
}
- (void) setLv_rotation:(CGFloat)f{
}

// rotationX
- (CGFloat) lv_rotationX{
    return 0;
}
- (void) setLv_rotationX:(CGFloat)f{
}

// rotationY
- (CGFloat) lv_rotationY{
    return 0;
}
- (void) setLv_rotationY:(CGFloat)f{
}

// scaleX
- (CGFloat) lv_scaleX{
    return 1.0;
}
-(void) setLv_scaleX:(CGFloat)f{
}

// scaleY
- (CGFloat) lv_scaleY{
    return 1.0;
}
-(void) setLv_scaleY:(CGFloat)f{
}


@end
