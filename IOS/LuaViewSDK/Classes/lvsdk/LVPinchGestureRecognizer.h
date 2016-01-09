//
//  LVPinchGestureRecognizer.h
//  LVSDK
//
//  Created by 董希成 on 15/3/9.
//  Copyright (c) 2015年 dongxicheng. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "UIGestureRecognizer+LuaView.h"
#import "LVHeads.h"

@interface LVPinchGestureRecognizer : UIPinchGestureRecognizer<LVProtocal>

@property(nonatomic,weak) LView* lv_lview;
@property(nonatomic,assign) LVUserDataInfo* lv_userData;

-(id) init:(lv_State*) l;


+(int) classDefine:(lv_State *)L ;


@end
