//
//  AppDelegate.h
//  Awesome
//
//  Created by lv-cli on 16/1/20.
//  Copyright © 2016年 juhuasuan. All rights reserved.
//

#import <UIKit/UIKit.h>

//#define ENABLE_NETWORK_DEBUG 1
#define ENABLE_LOCAL_DEBUG 1

#if !TARGET_IPHONE_SIMULATOR || ENABLE_NETWORK_DEBUG
#undef ENABLE_LOCAL_DEBUG
#endif

extern NSString * const LVReloadPackageNotification;

@interface AppDelegate : UIResponder <UIApplicationDelegate>

@property (strong, nonatomic) UIWindow *window;

#if TARGET_IPHONE_SIMULATOR

- (NSString *)lvSourcePath;

#endif

@end