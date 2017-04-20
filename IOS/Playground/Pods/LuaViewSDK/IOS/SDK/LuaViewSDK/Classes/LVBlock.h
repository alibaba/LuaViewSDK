/**
  * Created by LuaView.
  * Copyright (c) 2017, Alibaba Group. All rights reserved.
  *
  * This source code is licensed under the MIT.
  * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
  */

#import <Foundation/Foundation.h>
#import "LVHeads.h"

@interface LVBlock : NSObject

@property (nonatomic,assign) int returnValueNum;// 返回值的数量

- (id) initWith:(lua_State*)L statckID:(int) idx;
- (id) initWith:(lua_State*)L globalName:(NSString*) globalName;

/*
 * 调用Lua function
 */
- (NSString*) callWithArgs:(NSArray*) args;
- (NSString*) callWithArgs:(NSArray*) args returnValueNum:(int) returnValueNum;

/*
 * 获取返回值
 */
- (id) returnValue:(int)index;
- (id) returnValue;


- (void) pushFunctionToStack;


@end
