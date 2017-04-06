/**
  * Created by LuaView.
  * Copyright (c) 2017, Alibaba Group. All rights reserved.
  *
  * This source code is licensed under the MIT.
  * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
  */

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

@property(nonatomic, weak) LuaViewCore* lv_luaviewCore;
@property(nonatomic, assign) LVUserDataInfo* lv_userData;

@property(nonatomic, readonly, getter=isRunning) BOOL running;
@property(nonatomic, readonly, getter=isPaused) BOOL paused;

+(int) lvClassDefine:(lua_State *)L globalName:(NSString*) globalName;

- (void)start;
- (void)cancel;

- (void)pause;
- (void)resume;

@end
