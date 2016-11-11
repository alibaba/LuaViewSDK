//
//  LVLabel.h
//  lv5.1.4
//
//  Created by dongxicheng on 12/19/14.
//  Copyright (c) 2014 dongxicheng. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "LVHeads.h"

@interface LVLabel : UILabel<LVProtocal>

@property(nonatomic,weak) LView* lv_lview;
@property(nonatomic,assign) LVUserDataInfo* lv_userData;
@property(nonatomic,assign) NSUInteger lv_align;
@property(nonatomic,strong) CAShapeLayer* lv_shapeLayer;

-(id) init:(NSString*)imageName l:(lv_State*) l;

+(int) lvClassDefine:(lv_State *)L globalName:(NSString*) globalName;

@end
