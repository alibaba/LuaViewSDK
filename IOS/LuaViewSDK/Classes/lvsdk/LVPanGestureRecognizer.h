//
//  LVPanGestureRecognizer.h
//  LVSDK
//
//  Created by dongxicheng on 1/22/15.
//  Copyright (c) 2015 dongxicheng. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "LVHeads.h"

@interface LVPanGestureRecognizer : UIPanGestureRecognizer

@property(nonatomic,weak) LView* lv_lview;
@property(nonatomic,assign) LVUserDataGesture* lv_userData;

-(id) init:(lv_State*) l;


+(int) classDefine:(lv_State *)L ;

@end
