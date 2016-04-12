//
//  LVAlertView.h
//  LVSDK
//
//  Created by dongxicheng on 1/14/15.
//  Copyright (c) 2015 dongxicheng. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "LVHeads.h"

@interface LVAlert : UIAlertView<LVProtocal,UIAlertViewDelegate>

@property(nonatomic,weak) LView* lv_lview;
@property(nonatomic,assign) LVUserDataInfo* lv_userData;

-(id) init:(lv_State*) l argNum:(int)num;

+(int) classDefine: (lv_State *)L ;


@end
