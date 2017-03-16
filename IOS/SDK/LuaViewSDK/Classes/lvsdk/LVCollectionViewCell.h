/**
  * Created by LuaView.
  * Copyright (c) 2017, Alibaba Group. All rights reserved.
  *
  * This source code is licensed under the MIT.
  * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
  */

#import <UIKit/UIKit.h>
#import "LVHeads.h"

@interface LVCollectionViewCell : UICollectionViewCell

@property (nonatomic, assign) BOOL isInited;

-(void) pushTableToStack;

-(void) doInitWithLView:(LuaViewCore*) lview;


@end
