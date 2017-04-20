/**
  * Created by LuaView.
  * Copyright (c) 2017, Alibaba Group. All rights reserved.
  *
  * This source code is licensed under the MIT.
  * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
  */

#import <Foundation/Foundation.h>
#import "LVHeads.h"

@interface LVNavigation : NSObject<LVClassProtocal>

+(int) lvClassDefine:(lua_State *)L globalName:(NSString*) globalName;

// 空实现去除编译警告
-(void) lv_setNavigationItemTitleView:(UIView*) view;
-(void) lv_setNavigationItemTitle:(NSString*) title;
-(void) lv_setNavigationItemLeftBarButtonItems:(NSArray*) items;
-(void) lv_setNavigationItemRightBarButtonItems:(NSArray*) items;
-(void) lv_setNavigationBarBackgroundImage:(UIImage*) image;

@end
