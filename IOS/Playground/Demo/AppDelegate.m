//
//  AppDelegate.m
//  Demo
//
//  Created by lamo on 16/2/14.
//  Copyright © 2016年 juhuasuan. All rights reserved.
//

#import "AppDelegate.h"
#import "JHSLuaViewController.h"

@interface AppDelegate ()

@end

@implementation AppDelegate

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions {
    self.window = [[UIWindow alloc] initWithFrame:[[UIScreen mainScreen] bounds]];
    self.window.backgroundColor = [UIColor whiteColor];
    
    JHSLuaViewController* controller = [[JHSLuaViewController alloc] initWithPackage:[[NSBundle mainBundle] resourcePath] mainScript:@"kit/main.lua"];
    NSString* entry = @"App";
    controller.args = (NSDictionary*)entry;
    
    self.window.rootViewController = [[UINavigationController alloc]
                                      initWithRootViewController:controller];
    
    [self.window makeKeyAndVisible];
    
    return YES;
}

@end
