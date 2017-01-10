//
//  LVCollectionViewDelegate.h
//  LuaViewSDK
//
//  Created by dongxicheng on 10/21/15.
//  Copyright © 2015 dongxicheng. All rights reserved.
//

#import "LVScrollViewDelegate.h"

#define IDENTIFIER "Id"
#define DEFAULT_CELL_IDENTIFIER  @"LVCollectionCell.default.identifier"

@class LVCollectionView;
@class LVFlowLayout;

@interface LVCollectionViewDelegate : LVScrollViewDelegate<UICollectionViewDataSource, UICollectionViewDelegate>

@property(nonatomic,weak) LVCollectionView* lvCollectionView;
@property(nonatomic,weak) LVFlowLayout* lvflowLayout;

@end
