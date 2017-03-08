//
//  NSObject+LuaView.h
//  LuaViewSDK
//
//  Created by 董希成 on 2017/3/7.
//  Copyright © 2017年 dongxicheng. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "LVHeads.h"


@interface NSObject(NSObjectLuaView)<LVProtocal>

@property(nonatomic,weak) LuaViewCore* lv_luaviewCore;
@property(nonatomic,assign) LVUserDataInfo* lv_userData;
- (id) lv_nativeObject;

- (void) lv_callLuaByKey1:(NSString*) key1;
- (void) lv_callLuaByKey1:(NSString*) key1 key2:(NSString*) key2 argN:(int) argN;
-(NSString*) lv_callLua:(NSString*) functionName args:(NSArray*) args;

- (void) lv_buttonCallBack;

@end
