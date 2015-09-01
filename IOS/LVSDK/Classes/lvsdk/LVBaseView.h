//
//  LVBaseView.h
//  JU
//
//  Created by dongxicheng on 12/29/14.
//  Copyright (c) 2014 ju.taobao.com. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "LVHeads.h"

@interface LVBaseView : UIView<LVProtocal>

@property(nonatomic,weak) LView* lv_lview;
@property(nonatomic,assign) LVUserDataView* lv_userData;

-(id) init:(lv_State*) l;

+(const lvL_reg*) baseMemberFunctions;

+(int) classDefine: (lv_State *)L ;

@end
