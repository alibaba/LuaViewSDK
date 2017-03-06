//
//  LVCustomErrorView.h
//  LVSDK
//
//  Created by dongxicheng on 7/20/15.
//  Copyright (c) 2015 dongxicheng. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "LVHeads.h"

@interface LVCustomPanel : UIView<LVProtocal, LVClassProtocal>

@property(nonatomic,weak) LView* lv_lview;
@property(nonatomic,assign) LVUserDataInfo* lv_userData;
@property(nonatomic,assign) NSUInteger lv_align;
@property(nonatomic,strong) CAShapeLayer* lv_shapeLayer;

+(int) lvClassDefine:(lua_State *)L globalName:(NSString*) globalName;

- (void) callLuaWithArgument:(NSString*) info;
- (void) callLuaWithArguments:(NSArray*) args;
// callLuaFunction()
// callLuaCallback

@end
