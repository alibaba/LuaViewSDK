//
//  LVTypeTranslate.h
//  LVSDK
//
//  Created by dongxicheng on 7/2/15.
//  Copyright (c) 2015 dongxicheng. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "LVHeads.h"
#import "LVUtil.h"

@interface LVTypeConvert : NSObject

int lv_pushInvocationReturnValueToLuaStack(NSInvocation* invocation, lv_State* L);
id  lv_setInvocationReturnValueByLuaStack(NSInvocation* invocation, lv_State* L, int stackID);

int lv_pushInvocationArgToLuaStack(NSInvocation* invocation, int index, lv_State* L);
int lv_setInvocationArgByLuaStack(NSInvocation* invocation, int index, lv_State* L, int stackID);

int lv_setValueWithType(void* p, int index, double value, int type );
double lv_getValueWithType(void* p, int index, int type );

@end
