//
//  AppDelegate.m
//
//  Created by lv-cli on 16/1/20.
//  Copyright © 2016年 juhuasuan. All rights reserved.
//

#import "AppDelegate.h"
#import "LView.h"
#import "LVPkgManager.h"

NSString * const LVReloadPackageNotification = @"LVReloadPackageNotification";

@interface AppDelegate ()

#if ENABLE_NETWORK_DEBUG
@property(nonatomic, copy) NSString *packageURL;
#endif
@property(nonatomic, strong) LView *lv;

@end

@implementation AppDelegate

- (BOOL)application:(UIApplication *)application
    didFinishLaunchingWithOptions:(NSDictionary *)launchOptions {
    
    self.window = [[UIWindow alloc] initWithFrame:[[UIScreen mainScreen] bounds]];
    self.window.backgroundColor = [UIColor whiteColor];
    [self.window makeKeyAndVisible];
    
    [self rebuildLView];
    UIViewController *vc = [UIViewController new];
    vc.view = self.lv;
    self.window.rootViewController = vc;
    
#if ENABLE_NETWORK_DEBUG
    self.packageURL = @"";
    [self reloadPackage];
#else
    [self.lv runFile:@"main.lua"];
#endif
    
    return YES;
}

- (void)rebuildLView {
    self.lv = [[LView alloc] initWithFrame:self.window.bounds];

#if ENABLE_LOCAL_DEBUG
    NSString *localSrcPath = [self lvSourcePath];
    NSMutableArray *paths = [self.lv.bundleSearchPath mutableCopy] ?: [NSMutableArray array];
    if (paths.count == 0) {
        [paths addObject:localSrcPath];
    } else {
        [paths insertObject:localSrcPath atIndex:0];
    }
    
    [self.lv setBundleSearchPath:paths];
#endif // ENABLE_LOCAL_DEBUG
}

#if ENABLE_NETWORK_DEBUG || ENABLE_LOCAL_DEBUG

- (NSArray<UIKeyCommand *> *)keyCommands {
    UIKeyCommand *reloadKeyCommand = [UIKeyCommand
        keyCommandWithInput:@"r"
        modifierFlags:UIKeyModifierCommand
        action:@selector(reloadPackage)];
    
    return @[reloadKeyCommand];
}

- (void)reloadPackage {
    [[NSNotificationCenter defaultCenter]
     postNotificationName:LVReloadPackageNotification object:nil];
    
#if ENABLE_NETWORK_DEBUG

    [LVUtil download:self.packageURL callback:^(NSData *data) {
        if (!data) {
            return;
        }
        
        NSString *pkgName = @"Main";
        [LVPkgManager unpackageData:data packageName:pkgName localMode:NO];
        
        [self rebuildLView];
        self.window.rootViewController.view = self.lv;
        
        [self.lv runPackage:@"Main"];
    }];
    
#else

    [self rebuildLView];
    self.window.rootViewController.view = self.lv;
    
    [self.lv runFile:@"main.lua"];
    
#endif // ENABLE_NETWORK_DEBUG
}

#endif // ENABLE_NETWORK_DEBUG || ENABLE_LOCAL_DEBUG

#if TARGET_IPHONE_SIMULATOR

- (NSString *)lvSourcePath {
    NSString *filePath = [NSString stringWithUTF8String:__FILE__];
    NSMutableArray *paths = [[filePath componentsSeparatedByString:@"/"] mutableCopy];
    NSRange range = NSMakeRange([paths count] - 3, 3);
    [paths removeObjectsInRange:range];
    return [[paths componentsJoinedByString:@"/"] stringByAppendingPathComponent:@"lv-src"];
}

#endif

@end
