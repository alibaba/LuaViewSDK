//
//  LVLuaFunction.h
//  LVSDK
//
//  Created by dongxicheng on 4/27/15.
//  Copyright (c) 2015 dongxicheng. All rights reserved.
//

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
