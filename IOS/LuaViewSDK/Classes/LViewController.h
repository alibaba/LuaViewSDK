//
//  LViewController.h
//  LuaViewSDK
//
//  Created by lamo on 16/2/23.
//  Copyright © 2016年 dongxicheng. All rights reserved.
//

#import <UIKit/UIKit.h>

@class LView;

@interface LViewController : UIViewController

@property(nonatomic, readonly) LView *lv;
@property(nonatomic, readonly) NSString *mainScriptName;
@property(nonatomic, readonly) NSString *packagePath;

+ (void)disableReloadKeyCommand:(BOOL)disable;

- (instancetype)initWithPackage:(NSString *)path mainScript:(NSString *)scriptName;

- (void)willCreateLuaView;
- (void)didCreateLuaView:(LView *)view;
- (void)willDestroyLuaView:(LView *)view;
- (void)didDestroyLuaView;

@end
