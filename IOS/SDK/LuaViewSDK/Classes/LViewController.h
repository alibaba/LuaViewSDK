//
//  LViewController.h
//  LuaViewSDK
//
//  Created by lamo on 16/2/23.
//  Copyright © 2016年 dongxicheng. All rights reserved.
//

#import <UIKit/UIKit.h>


@class LuaView;

typedef void(^LuaViewRegister)(LuaView* lv);

@interface LViewController : UIViewController

@property(nonatomic, readonly) LuaView *lv;
@property(nonatomic, readonly) NSString *mainScriptName;
@property(nonatomic, readonly) NSString *packagePath;

+ (void)disableReloadKeyCommand:(BOOL)disable;

- (instancetype)initWithPackage:(NSString *)path mainScript:(NSString *)scriptName;

- (void)willCreateLuaView;
- (void)didCreateLuaView:(LuaView *) view;
- (void)willDestroyLuaView:(LuaView *) view;
- (void)didDestroyLuaView;

@property(nonatomic,copy) LuaViewRegister luaviewRegister;

@end
