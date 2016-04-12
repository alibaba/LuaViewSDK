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

@interface LVCollectionView : UICollectionView<LVProtocal>

@property(nonatomic,weak) LView* lv_lview;
@property(nonatomic,assign) LVUserDataInfo* lv_userData;
@property(nonatomic,assign) NSUInteger lv_align;

+ (int) classDefine: (lv_State *)L ;

- (id) init:(lv_State*) l identifierArray:(NSArray*) identifierArray;

+ (void) setDefaultStyle:(Class) c;

@property(nonatomic,weak) id lvScrollViewDelegate;

@end
