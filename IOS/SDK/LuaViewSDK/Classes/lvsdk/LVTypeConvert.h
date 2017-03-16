/**
  * Created by LuaView.
  * Copyright (c) 2017, Alibaba Group. All rights reserved.
  *
  * This source code is licensed under the MIT.
  * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
  */

#import <Foundation/Foundation.h>
#import "LVHeads.h"
#import "LVUtil.h"

@interface LVTypeConvert : NSObject

int lv_pushInvocationReturnValueToLuaStack(NSInvocation* invocation, lua_State* L);
id  lv_setInvocationReturnValueByLuaStack(NSInvocation* invocation, lua_State* L, int stackID);

int lv_pushInvocationArgToLuaStack(NSInvocation* invocation, int index, lua_State* L);
int lv_setInvocationArgByLuaStack(NSInvocation* invocation, int index, lua_State* L, int stackID);

int lv_setValueWithType(void* p, int index, double value, int type );
double lv_getValueWithType(void* p, int index, int type );

@end
