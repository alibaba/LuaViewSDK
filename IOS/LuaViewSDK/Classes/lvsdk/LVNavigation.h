//
//  LVNavigationBar.h
//  LVSDK
//
//  Created by dongxicheng on 7/15/15.
//  Copyright (c) 2015 dongxicheng. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "LVHeads.h"

@interface LVNavigation : NSObject

+(int) classDefine:(lv_State *)L ;

// 空实现去除编译警告
-(void) lv_setNavigationItemTitleView:(UIView*) view;
-(void) lv_setNavigationItemTitle:(NSString*) title;
-(void) lv_setNavigationItemLeftBarButtonItems:(NSArray*) items;
-(void) lv_setNavigationItemRightBarButtonItems:(NSArray*) items;
-(void) lv_setNavigationBarBackgroundImage:(UIImage*) image;

@end
