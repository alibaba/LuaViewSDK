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

typedef NS_ENUM(int, LVAniamtorInterpolator) {
    LVAccelerateDecelerateInterpolator = 0,
    LVAccelerateInterpolator = 1,
    LVAnticipateInterpolator = 2,
    LVAnticipateOvershootInterpolator = 3,
    LVDecelerateInterpolator = 6,
    LVLinearInterpolator = 7,
    LVOvershootInterpolator = 8,
};

@interface LVAnimator : NSObject <LVProtocal, NSCopying, NSMutableCopying>

@property(nonatomic, copy) NSString *keyPath;
@property(nonatomic, copy) NSNumber *toValue;

@property(nonatomic, assign) float duration;
@property(nonatomic, assign) float delay;
@property(nonatomic, assign) int repeatCount;
@property(nonatomic, assign) BOOL autoreverses;
@property(nonatomic, assign) LVAniamtorInterpolator interpolator;

@property(nonatomic, weak) UIView *target;

@property(nonatomic, weak) LView* lv_lview;
@property(nonatomic, assign) LVUserDataInfo* lv_userData;

@property(nonatomic, readonly, getter=isRunning) BOOL running;
@property(nonatomic, readonly, getter=isPaused) BOOL paused;

+ (int)classDefine:(lv_State *)L;

- (void)start;
- (void)cancel;

- (void)pause;
- (void)resume;

@end
