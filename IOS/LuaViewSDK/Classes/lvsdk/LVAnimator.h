//
//  LVAnimator.h
//  LuaViewSDK
//
//  Created by lamo on 15/12/28.
//  Copyright © 2015年 dongxicheng. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "LVHeads.h"
#import "lVtable.h"

typedef struct _LVUserDataAnimator {
    LVUserDataCommonHead;
    const void* animator;
} LVUserDataAnimator;

extern NSString *LVAnimatorGetAnimationKey(LVUserDataAnimator *animator);

@interface LVAnimator : NSObject <NSCopying, NSMutableCopying>

@property(nonatomic, copy) NSString *keyPath;
@property(nonatomic, copy) NSNumber *toValue;

@property(nonatomic, assign) float duration;
@property(nonatomic, assign) float delay;
@property(nonatomic, assign) int repeatCount;
@property(nonatomic, assign) BOOL autoreverses;

@property(nonatomic, weak) UIView *target;

@property(nonatomic, assign) LVUserDataAnimator *userData;
@property(nonatomic, assign) lv_State *lvState;

+(int) classDefine:(lv_State *)L ;

- (void)startWithKey:(NSString *)key;

@end
