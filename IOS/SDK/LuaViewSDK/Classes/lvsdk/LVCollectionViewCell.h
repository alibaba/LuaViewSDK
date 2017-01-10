//
//  LVCollectionViewCell.h
//  LVSDK
//
//  Created by dongxicheng on 6/11/15.
//  Copyright (c) 2015 dongxicheng. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "LVHeads.h"

@interface LVCollectionViewCell : UICollectionViewCell

@property (nonatomic, assign) BOOL isInited;

-(void) pushTableToStack;

-(void) doInitWithLView:(LView*) lview;


@end
