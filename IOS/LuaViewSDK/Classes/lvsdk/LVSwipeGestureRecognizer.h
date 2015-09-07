//
//  LVSwipeGestureRecognizer.h
//  LVSDK
//
//  Created by 城西 on 15/3/9.
//  Copyright (c) 2015年 dongxicheng. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "LVHeads.h"

@interface LVSwipeGestureRecognizer : UISwipeGestureRecognizer

@property(nonatomic,weak) LView* lv_lview;
@property(nonatomic,assign) LVUserDataGesture* lv_userData;

-(id) init:(lv_State*) l;

+(int) classDefine:(lv_State *)L ;

@end
