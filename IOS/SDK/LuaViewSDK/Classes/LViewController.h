/**
  * Created by LuaView.
  * Copyright (c) 2017, Alibaba Group. All rights reserved.
  *
  * This source code is licensed under the MIT.
  * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
  */

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
