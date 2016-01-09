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


extern NSString *LVAnimatorGetAnimationKey(LVUserDataInfo *animator);

@interface LVAnimator : NSObject <LVProtocal, NSCopying, NSMutableCopying>

@property(nonatomic, copy) NSString *keyPath;
@property(nonatomic, copy) NSNumber *toValue;

@property(nonatomic, assign) float duration;
@property(nonatomic, assign) float delay;
@property(nonatomic, assign) int repeatCount;
@property(nonatomic, assign) BOOL autoreverses;

@property(nonatomic, weak) UIView *target;


@property(nonatomic, weak) LView* lv_lview;
@property(nonatomic, assign) LVUserDataInfo* lv_userData;

+(int) classDefine:(lv_State *)L ;

- (void)startWithKey:(NSString *)key;

@end
