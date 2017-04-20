/**
  * Created by LuaView.
  * Copyright (c) 2017, Alibaba Group. All rights reserved.
  *
  * This source code is licensed under the MIT.
  * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
  */

#import "LVScrollViewDelegate.h"

#define IDENTIFIER "Id"
#define DEFAULT_CELL_IDENTIFIER  @"LVCollectionCell.default.identifier"

@class LVCollectionView;
@class LVFlowLayout;

@interface LVCollectionViewDelegate : LVScrollViewDelegate<UICollectionViewDataSource, UICollectionViewDelegate>

@property(nonatomic,weak) LVCollectionView* lvCollectionView;
@property(nonatomic,weak) LVFlowLayout* lvflowLayout;

@end
