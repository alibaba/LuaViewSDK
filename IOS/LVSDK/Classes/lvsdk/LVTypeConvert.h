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

+ (int) pushInvocationReturnValue:(NSInvocation*) invocation toLua:(lv_State*)L;
+ (id)  setInvocationReturnValue:(NSInvocation*) invocation withLua:(lv_State*)L stackID:(int)stackID;

+ (int) pushInvocation:(NSInvocation*) invocation argIndex:(int)index toLua:(lv_State*)L;
+ (int) setIvocation:(NSInvocation*) invocation argIndex:(int)index withLua:(lv_State*)L stackID:(int) stackID;


int lv_setValueWithType(void* p, int index,double value, int type );
double lv_getValueWithType(void* p, int index, int type );

@end
