//
//  LVAppDelegate.m
//  luaviewEx
//
//  Created by yechunxiao19 on 04/06/2017.
//  Copyright (c) 2017 yechunxiao19. All rights reserved.
//

#import "LVAppDelegate.h"
#import "LVViewController.h"

@implementation LVAppDelegate

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions
{
    // Override point for customization after application launch.
    self.window = [[UIWindow alloc] initWithFrame:[[UIScreen mainScreen] bounds]];
    self.window.backgroundColor = [UIColor whiteColor];
    
    LVViewController* controller = [[LVViewController alloc] initWithPackage];
    
//    LVViewController* controller = [[LVViewController alloc] initWithPackage:[[NSBundle mainBundle] resourcePath] mainScript:@"kit/main.lua"];
    
    self.window.rootViewController = [[UINavigationController alloc]
                                      initWithRootViewController:controller];
    
    [self.window makeKeyAndVisible];
    
    return YES;
}

@end
