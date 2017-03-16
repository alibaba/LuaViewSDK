/**
  * Created by LuaView.
  * Copyright (c) 2017, Alibaba Group. All rights reserved.
  *
  * This source code is licensed under the MIT.
  * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
  */

#import <UIKit/UIKit.h>
#import <Foundation/Foundation.h>

#define DEFAULT_DISPLAY_DURATION 2.0f

@interface LVToast : NSObject

+ (void)showWithText:(NSString *) text;
+ (void)showWithText:(NSString *) text duration:(CGFloat)duration;

+ (void)showWithText:(NSString *) text topOffset:(CGFloat) topOffset;
+ (void)showWithText:(NSString *) text topOffset:(CGFloat) topOffset duration:(CGFloat) duration;

+ (void)showWithText:(NSString *) text bottomOffset:(CGFloat) bottomOffset;
+ (void)showWithText:(NSString *) text bottomOffset:(CGFloat) bottomOffset duration:(CGFloat) duration;

@end
