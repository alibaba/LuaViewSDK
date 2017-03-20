//
//  LVToast.h
//  LuaViewSDK
//
//  Created by dongxicheng on 10/23/15.
//  Copyright © 2015 dongxicheng. All rights reserved.
//

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