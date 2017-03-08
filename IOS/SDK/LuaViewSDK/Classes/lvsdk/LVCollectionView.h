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

@property(nonatomic,weak) LuaViewCore* lv_luaviewCore;
@property(nonatomic,assign) LVUserDataInfo* lv_userData;
@property(nonatomic,assign) NSUInteger lv_align;
@property(nonatomic,strong) CAShapeLayer* lv_shapeLayer;

+(int) lvClassDefine:(lua_State *)L globalName:(NSString*) globalName;

- (id) init:(lua_State*) l;

@property(nonatomic,weak) id lvScrollViewDelegate;

@property(nonatomic,strong) LVFlowLayout* lvflowLayout;

@end
