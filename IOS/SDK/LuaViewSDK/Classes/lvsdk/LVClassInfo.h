/**
  * Created by LuaView.
  * Copyright (c) 2017, Alibaba Group. All rights reserved.
  *
  * This source code is licensed under the MIT.
  * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
  */

#import <Foundation/Foundation.h>

@class LVMethod;

@interface LVClassInfo : NSObject


- (void) addMethod:(LVMethod*) method key:(NSString*) key;
- (LVMethod*) getMethod:(NSString*) methodName;

- (BOOL) existMethod:(NSString*) methodName;
- (void) setMethod:(NSString*) methodName exist:(BOOL) exist;

+ (LVClassInfo*) classInfo:(NSString*) className;

@end
