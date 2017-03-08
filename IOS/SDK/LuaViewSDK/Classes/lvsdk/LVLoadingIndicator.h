//
//  LVLoadingIndicator.h
//  LVSDK
//
//  Created by dongxicheng on 7/27/15.
//  Copyright (c) 2015 dongxicheng. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "LVHeads.h"

@interface LVLoadingIndicator : UIActivityIndicatorView<LVProtocal, LVClassProtocal>

@property(nonatomic,weak) LuaViewCore* lv_lview;
@property(nonatomic,assign) LVUserDataInfo* lv_userData;
@property(nonatomic,assign) NSUInteger lv_align;
@property(nonatomic,strong) CAShapeLayer* lv_shapeLayer;

-(id) init:(lua_State*) l;

+(int) lvClassDefine:(lua_State *)L globalName:(NSString*) globalName;


@end
