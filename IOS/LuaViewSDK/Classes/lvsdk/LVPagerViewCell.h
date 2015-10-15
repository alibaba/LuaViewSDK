//
//  LVPageViewCell.h
//  LuaViewSDK
//
//  Created by dongxicheng on 9/21/15.
//  Copyright © 2015 dongxicheng. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "LView.h"

@interface LVPagerViewCell : UIView


@property (nonatomic, assign) BOOL isInited;

-(void) pushTableToStack;

-(void) doInitWithLView:(LView*) lview;

@end
