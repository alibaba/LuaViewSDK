//
//  LVCollectionView.h
//  LVSDK
//
//  Created by dongxicheng on 6/11/15.
//  Copyright (c) 2015 dongxicheng. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "LVHeads.h"
#import "UIScrollView+LuaView.h"
#import "LVFlowLayout.h"

@interface LVCollectionView : UICollectionView<LVProtocal, LVClassProtocal>

@property(nonatomic,weak) LView* lv_lview;
@property(nonatomic,assign) LVUserDataInfo* lv_userData;
@property(nonatomic,assign) NSUInteger lv_align;
@property(nonatomic,strong) CAShapeLayer* lv_shapeLayer;

+(int) lvClassDefine:(lv_State *)L globalName:(NSString*) globalName;

- (id) init:(lv_State*) l;

@property(nonatomic,weak) id lvScrollViewDelegate;

@property(nonatomic,strong) LVFlowLayout* lvflowLayout;

@end
