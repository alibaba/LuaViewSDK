//
//  LVPageView.h
//  LuaViewSDK
//
//  Created by dongxicheng on 9/21/15.
//  Copyright © 2015 dongxicheng. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "LVHeads.h"

@class LVPagerIndicator;
@interface LVPagerView : UIView<LVProtocal, LVClassProtocal, UIScrollViewDelegate>


@property(nonatomic,weak) LView* lv_lview;
@property(nonatomic,assign) LVUserDataInfo* lv_userData;
@property(nonatomic,assign) NSUInteger lv_align;
@property(nonatomic,strong) CAShapeLayer* lv_shapeLayer;


-(id) init:(lua_State*) l;
+(int) lvClassDefine:(lua_State *)L globalName:(NSString*) globalName;

-(void) setIndicator:(LVPagerIndicator*) indicator;
-(void) setCurrentPageIdx:(NSInteger) pageIdx animation:(BOOL) animation;

@end
