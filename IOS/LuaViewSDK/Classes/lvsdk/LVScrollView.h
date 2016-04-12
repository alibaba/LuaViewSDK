//
//  LVScrollView.h
//  lv5.1.4
//
//  Created by dongxicheng on 12/18/14.
//  Copyright (c) 2014 dongxicheng. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "LVHeads.h"
#import "UIScrollView+LuaView.h"

@interface LVScrollView : UIScrollView<UIScrollViewDelegate,LVProtocal>

@property(nonatomic,weak) LView* lv_lview;
@property(nonatomic,assign) LVUserDataInfo* lv_userData;
@property(nonatomic,assign) NSUInteger lv_align;

-(id) init:(lv_State*) l;

+(int) classDefine:(lv_State *)L;

+(const struct lvL_reg*) memberFunctions;

+ (void) setDefaultStyle:(Class) c;

@property(nonatomic,weak) id lvScrollViewDelegate;

@end
