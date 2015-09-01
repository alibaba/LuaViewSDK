//
//  UIView+LuaView.h
//  LVSDK
//
//  Created by dongxicheng on 7/24/15.
//  Copyright (c) 2015 dongxicheng. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "LVHeads.h"

@interface UIView(UIViewLuaView)<LVProtocal>

@property(nonatomic,weak) LView* lv_lview;
@property(nonatomic,assign) LVUserDataView* lv_userData;

- (void) callLuaWithNoArgs:(NSString*) funcName;

@end
