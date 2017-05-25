/**
  * Created by LuaView.
  * Copyright (c) 2017, Alibaba Group. All rights reserved.
  *
  * This source code is licensed under the MIT.
  * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
  */
#import <Foundation/Foundation.h>
#import "LVHeads.h"


@interface NSObject(NSObjectLuaView)<LVProtocal>

@property(nonatomic,weak) LuaViewCore* lv_luaviewCore;
@property(nonatomic,assign) LVUserDataInfo* lv_userData;
- (id) lv_nativeObject;

- (void) lv_callLuaCallback:(NSString*) key1;
- (void) lv_callLuaCallback:(NSString*) key1 key2:(NSString*) key2 argN:(int) argN;
- (NSString*) lv_callLuaFunc:(NSString*) funcName args:(NSArray*) args;

- (void) lv_buttonCallBack;

@end
