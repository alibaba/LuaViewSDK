//
//  LVLongPressGestureRecognizer.h
//  LVSDK
//
//  Created by 城西 on 15/3/9.
//  Copyright (c) 2015年 dongxicheng. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "LVHeads.h"
#import "UIGestureRecognizer+LuaView.h"

@interface LVLongPressGestureRecognizer : UILongPressGestureRecognizer<LVProtocal>


@property(nonatomic,weak) LView* lv_lview;
@property(nonatomic,assign) LVUserDataInfo* lv_userData;

-(id) init:(lv_State*) l;

+(int) classDefine:(lv_State *)L ;

@end
