//
//  AppDelegate.m
//  Demo
//
//  Created by lamo on 16/2/14.
//  Copyright © 2016年 juhuasuan. All rights reserved.
//

#import "AppDelegate.h"
#import "ViewController.h"

@interface AppDelegate ()

@end

@implementation AppDelegate

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions {
    self.window = [[UIWindow alloc] initWithFrame:[[UIScreen mainScreen] bounds]];
    self.window.backgroundColor = [UIColor whiteColor];
    
    self.window.rootViewController = [[UINavigationController alloc]
                                      initWithRootViewController:[ViewController new]];
    [self.window makeKeyAndVisible];
    
    return YES;
}

@end
