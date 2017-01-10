//
//  LVAnimator.h
//  LuaViewSDK
//
//  Created by lamo on 15/12/28.
//  Copyright © 2015年 dongxicheng. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <QuartzCore/CAAnimation.h>
#import "LVHeads.h"
#import "ltable.h"

typedef NS_ENUM(int, LVAniamtorInterpolator) {
    LVLinearInterpolator = 0,
    LVAccelerateInterpolator,
    LVDecelerateInterpolator,
    LVAccelerateDecelerateInterpolator,
    LVAnticipateInterpolator,
    LVAnticipateOvershootInterpolator,
    LVOvershootInterpolator,
};

@interface LVAnimator : NSObject <LVProtocal, LVClassProtocal, NSCopying, NSMutableCopying>

@property(nonatomic, copy) NSString *keyPath;
@property(nonatomic, copy) NSValue *toValue;

@property(nonatomic, assign) float duration;
@property(nonatomic, assign) float delay;
@property(nonatomic, assign) int repeatCount;
@property(nonatomic, assign) BOOL autoreverses;
@property(nonatomic, assign) LVAniamtorInterpolator interpolator; // default is linear

@property(nonatomic, weak) UIView *target;

@property(nonatomic, weak) LView* lv_lview;
@property(nonatomic, assign) LVUserDataInfo* lv_userData;

@property(nonatomic, readonly, getter=isRunning) BOOL running;
@property(nonatomic, readonly, getter=isPaused) BOOL paused;

+(int) lvClassDefine:(lua_State *)L globalName:(NSString*) globalName;

- (void)start;
- (void)cancel;

- (void)pause;
- (void)resume;

@end
