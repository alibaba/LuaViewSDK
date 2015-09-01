//
//  LVCollectionView.h
//  LVSDK
//
//  Created by dongxicheng on 6/11/15.
//  Copyright (c) 2015 dongxicheng. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "LVHeads.h"
#import "LVApiDelegate.h"

@interface LVCollectionView : UICollectionView<LVProtocal,UICollectionViewDataSource, UICollectionViewDelegate>

@property(nonatomic,weak) id<LVRefreshHeaderProtocol> refreshHeader;
@property(nonatomic,weak) id<LVRefreshFooterProtocol> refreshFooter;

@property(nonatomic,weak) LView* lv_lview;
@property(nonatomic,assign) LVUserDataView* lv_userData;

+ (int) classDefine: (lv_State *)L ;

- (id) init:(lv_State*) l identifierArray:(NSArray*) identifierArray;

+ (void) setDefaultStyle:(Class) c;

@end
