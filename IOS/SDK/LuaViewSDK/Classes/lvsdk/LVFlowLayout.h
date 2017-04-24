/**
  * Created by LuaView.
  * Copyright (c) 2017, Alibaba Group. All rights reserved.
  *
  * This source code is licensed under the MIT.
  * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
  */

#import <UIKit/UIKit.h>

@interface LVFlowLayout : UICollectionViewFlowLayout

-(void) resetPinnedDic;
-(void) addPinnedIndexPath:(NSIndexPath*)indexPath;
-(void) delPinnedIndexPath:(NSIndexPath*)indexPath;
-(BOOL) isPinned:(NSIndexPath*)indexPath;
-(BOOL) pinnedDicIsNil;

@end
