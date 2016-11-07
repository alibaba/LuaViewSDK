//
//  LVFlowLayout.h
//  LVFlowlayout
//
//  Created by 城西 on 16/11/7.
//  Copyright © 2016年 alibaba. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface LVFlowLayout : UICollectionViewFlowLayout

-(void) resetPinnedDic;
-(void) addPinnedIndexPath:(NSIndexPath*)indexPath;
-(BOOL) isPinned:(NSIndexPath*)indexPath;
-(BOOL) pinnedDicIsNil;

@end
