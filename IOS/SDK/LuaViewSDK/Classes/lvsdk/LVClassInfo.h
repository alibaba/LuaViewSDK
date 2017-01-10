//
//  LVClassInfo.h
//  LuaViewSDK
//
//  Created by 董希成 on 2016/11/9.
//  Copyright © 2016年 dongxicheng. All rights reserved.
//

#import <Foundation/Foundation.h>

@class LVMethod;

@interface LVClassInfo : NSObject


- (void) addMethod:(LVMethod*) method key:(NSString*) key;
- (LVMethod*) getMethod:(NSString*) methodName;

- (BOOL) existMethod:(NSString*) methodName;
- (void) setMethod:(NSString*) methodName exist:(BOOL) exist;

+ (LVClassInfo*) classInfo:(NSString*) className;

@end
