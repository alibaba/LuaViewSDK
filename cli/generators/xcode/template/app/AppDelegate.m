//
//  AppDelegate.m
//
//  Created by lv-cli on 16/1/20.
//  Copyright © 2016年 juhuasuan. All rights reserved.
//

#import "AppDelegate.h"
#import "LView.h"
#import "ViewController.h"

@interface AppDelegate ()

@end

@implementation AppDelegate

- (BOOL)application:(UIApplication *)application
    didFinishLaunchingWithOptions:(NSDictionary *)launchOptions {
    
    self.window = [[UIWindow alloc] initWithFrame:[[UIScreen mainScreen] bounds]];
    self.window.backgroundColor = [UIColor whiteColor];
    [self.window makeKeyAndVisible];
    
    ViewController *vc = [ViewController new];
    self.window.rootViewController = vc;
    
    return YES;
}

+ (NSString *)lvSourcePath {
#if TARGET_IPHONE_SIMULATOR
    NSString *filePath = [NSString stringWithUTF8String:__FILE__];
    NSMutableArray *paths = [[filePath componentsSeparatedByString:@"/"] mutableCopy];
    NSRange range = NSMakeRange([paths count] - 3, 3);
    [paths removeObjectsInRange:range];
    return [[paths componentsJoinedByString:@"/"] stringByAppendingPathComponent:@"lv-src"];
#else
	return [[NSBundle mainBundle] resourcePath];
#endif
}

@end
