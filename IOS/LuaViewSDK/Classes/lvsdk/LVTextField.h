//
//  LVTextField.h
//  JU
//
//  Created by dongxicheng on 1/7/15.
//  Copyright (c) 2015 ju.taobao.com. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "LVHeads.h"


@interface LVTextField : UITextField<LVProtocal>

@property(nonatomic,weak) LView* lv_lview;
@property(nonatomic,assign) LVUserDataInfo* lv_userData;
@property(nonatomic,assign) NSUInteger lv_align;
@property(nonatomic,strong) CAShapeLayer* lv_shapeLayer;

-(id) init:(lv_State*) l;

+(int) lvClassDefine:(lv_State *)L globalName:(NSString*) globalName;

@end
