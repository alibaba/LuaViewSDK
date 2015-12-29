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

- (id) initWith:(lv_State*)L statckID:(int) idx;
- (id) initWith:(lv_State*)L globalName:(NSString*) globalName;

- (NSString*) callWithArgs:(NSArray*) args;

- (void) pushFunctionToStack;

- (id) returnValue:(int)index;

@end
