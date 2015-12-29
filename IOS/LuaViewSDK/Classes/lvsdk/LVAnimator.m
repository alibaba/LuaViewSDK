//
//  LVAnimator.m
//  LuaViewSDK
//
//  Created by lamo on 15/12/28.
//  Copyright © 2015年 dongxicheng. All rights reserved.
//

#import "LVAnimator.h"
#import "lV.h"
#import "lVstate.h"
#import "lVauxlib.h"
#import "LVHeads.h"
#import "LVUtil.h"
#import <QuartzCore/CoreAnimation.h>

typedef NS_ENUM(int, LVAnimatorCallback) {
    kLVAnimatorCallbackOnStart = 1,
    kLVAnimatorCallbackOnEnd,
    kLVAnimatorCallbackOnCancel,
};

@implementation LVAnimator

NSString *LVAnimatorGetAnimationKey(LVUserDataAnimator *animator) {
    return animator ? [NSString stringWithFormat:@"<LVAnimator: %p>", animator] : @"";
}

static int lvNewAnimator(lv_State *L) {
    LVAnimator *animator = [LVAnimator new];

    NEW_USERDATA(userData, LVUserDataAnimator);
    userData->animator = CFBridgingRetain(animator);
    
    animator.userData = userData;
    animator.lvState = L;
    
    lvL_getmetatable(L, META_TABLE_Animator);
    lv_setmetatable(L, -2);
    
    return 1;
}

static int __gc(lv_State *L) {
    LVUserDataAnimator *data = (LVUserDataAnimator *)lv_touserdata(L, 1);
    if (LVIsType(data, LVUserDataAnimator) && data->animator) {
        LVAnimator *animator = (__bridge LVAnimator *)data->animator;

        CFBridgingRelease((__bridge CFTypeRef)(animator));
        data->animator = nil;
        
        animator.userData = NULL;
    }
    
    return 0;
}

static int __tostring(lv_State *L) {
    LVUserDataAnimator *data = (LVUserDataAnimator *)lv_touserdata(L, 1);
    if (LVIsType(data, LVUserDataAnimator)) {
        LVAnimator *animator = (__bridge LVAnimator *)data->animator;
        NSString *s = [NSString stringWithFormat:@"Animator<%@>: %@", animator.keyPath, animator.toValue];
        lv_pushstring(L, s.UTF8String);
        
        return 1;
    }
    
    return 0;
}

static int __eq(lv_State *L) {
    LVUserDataAnimator *data1 = (LVUserDataAnimator *)lv_touserdata(L, 1);
    LVUserDataAnimator *data2 = (LVUserDataAnimator *)lv_touserdata(L, 2);
    
    if (LVIsType(data1, LVUserDataAnimator) && LVIsType(data2, LVUserDataAnimator)) {
        LVAnimator *a1 = (__bridge LVAnimator *)data1->animator;
        LVAnimator *a2 = (__bridge LVAnimator *)data2->animator;
        
        BOOL eq = [a1 isEqual:a2];
        lv_pushboolean(L, eq);
        
        return 1;
    }
    
    return 0;
}

static int clone(lv_State *L) {
    LVUserDataAnimator *data = (LVUserDataAnimator *)lv_touserdata(L, 1);
    if (LVIsType(data, LVUserDataAnimator)) {
        LVAnimator *animator = [(__bridge LVAnimator *)data->animator copy];
        
        NEW_USERDATA(userData, LVUserDataAnimator);
        userData->animator = CFBridgingRetain(animator);
        
        animator.userData = userData;
        animator.lvState = L;
        
        lvL_getmetatable(L, META_TABLE_Animator);
        lv_setmetatable(L, -2);
        
        return 1;
    }
    
    return 0;
}

static int with(lv_State *L) {
    LVUserDataAnimator *adata = (LVUserDataAnimator *)lv_touserdata(L, 1);
    LVUserDataView *vdata = (LVUserDataView *)lv_touserdata(L, 2);

    if (LVIsType(adata, LVUserDataAnimator) && LVIsType(vdata, LVUserDataView)) {
        LVAnimator *animator = (__bridge LVAnimator *)adata->animator;
        animator.target = (__bridge UIView *)vdata->view;
    }
    
    lv_pushUserdata(L, adata);
    
    return 1;
}

static int start(lv_State *L) {
    LVUserDataAnimator *data = (LVUserDataAnimator *)lv_touserdata(L, 1);
    
    if (LVIsType(data, LVUserDataAnimator)) {
        LVAnimator *animator = (__bridge LVAnimator *)data->animator;
        [animator startWithKey:LVAnimatorGetAnimationKey(data)];
    }
    
    lv_pushUserdata(L, data);
    
    return 1;
}


static int duration(lv_State *L) {
    LVUserDataAnimator *data = (LVUserDataAnimator *)lv_touserdata(L, 1);
    float value = lv_tonumber(L, 2);
    
    if (LVIsType(data, LVUserDataAnimator)) {
        LVAnimator *animator = (__bridge LVAnimator *)data->animator;
        
        animator.duration = value;
    }
    
    lv_pushUserdata(L, data);
    
    return 1;
}

static int delay(lv_State *L) {
    LVUserDataAnimator *data = (LVUserDataAnimator *)lv_touserdata(L, 1);
    float value = lv_tonumber(L, 2);
    
    if (LVIsType(data, LVUserDataAnimator)) {
        LVAnimator *animator = (__bridge LVAnimator *)data->animator;
        
        animator.delay = value;
    }
    
    lv_pushUserdata(L, data);
    
    return 1;
}

static int repeatCount(lv_State *L) {
    LVUserDataAnimator *data = (LVUserDataAnimator *)lv_touserdata(L, 1);
    int value = (int)lv_tointeger(L, 2);
    
    if (LVIsType(data, LVUserDataAnimator)) {
        LVAnimator *animator = (__bridge LVAnimator *)data->animator;
        
        animator.repeatCount = value;
    }
    
    lv_pushUserdata(L, data);
    
    return 1;
}

static int autoreverses(lv_State *L) {
    LVUserDataAnimator *data = (LVUserDataAnimator *)lv_touserdata(L, 1);
    BOOL autoreverses = !!lv_toboolean(L, 2);
    
    if (LVIsType(data, LVUserDataAnimator)) {
        LVAnimator *animator = (__bridge LVAnimator *)data->animator;
        
        animator.autoreverses = autoreverses;
    }
    
    lv_pushUserdata(L, data);
    
    return 1;
}

static int setCallback(lv_State *L, int idx) {
    LVUserDataAnimator *data = (LVUserDataAnimator *)lv_touserdata(L, 1);
    
    if (LVIsType(data, LVUserDataAnimator)) {
        lv_pushvalue(L, 1);
        if (lv_type(L, 2) == LV_TFUNCTION) {
            lv_pushvalue(L, 2);
        } else {
            lv_pushnil(L);
        }
        
        lv_udataRef(L, idx);
    }
    
    lv_pushUserdata(L, data);
    
    return 1;
}

static int onStart(lv_State *L) {
    return setCallback(L, kLVAnimatorCallbackOnStart);
}

static int onEnd(lv_State *L) {
    return setCallback(L, kLVAnimatorCallbackOnEnd);
}

static int onCancel(lv_State *L) {
    return setCallback(L, kLVAnimatorCallbackOnCancel);
}

static int callback(lv_State *L) {
    LVUserDataAnimator *data = (LVUserDataAnimator *)lv_touserdata(L, 1);
    if (LVIsType(data, LVUserDataAnimator) && lv_type(L, 2) == LV_TTABLE) {
        lv_pushvalue(L, 2);
        lv_pushnil(L);
        
        while (lv_next(L, -2)) {
            if (lv_type(L, -2) != LV_TSTRING) {
                continue;
            }
            const char* key = lv_tostring(L, -2);
            int idx = 0;
            if (strcmp(key, "onStart") == 0) {
                idx = kLVAnimatorCallbackOnStart;
            } else if (strcmp(key, "onEnd") == 0) {
                idx = kLVAnimatorCallbackOnEnd;
            } else if (strcmp(key, "onCancel") == 0) {
                idx = kLVAnimatorCallbackOnCancel;
            }
            
            if (idx != 0) {
                lv_pushvalue(L, 1);
                if (lv_type(L, -2) == LV_TFUNCTION) {
                    lv_pushvalue(L, -2);
                } else {
                    lv_pushnil(L);
                }
                lv_udataRef(L, idx);
                lv_pop(L, 2);
            } else {
                lv_pop(L, 1);
            }
        }
        lv_pop(L, 1);
    }
    
    lv_pushUserdata(L, data);
    
    return 1;
}

static int updateAnimator(lv_State *L, NSString *keyPath) {
    LVUserDataAnimator *data = (LVUserDataAnimator *)lv_touserdata(L, 1);
    float value = lv_gettop(L) >= 2 ? lv_tonumber(L, 2) : 0;
    
    if (LVIsType(data, LVUserDataAnimator)) {
        LVAnimator *animator = (__bridge LVAnimator *)data->animator;
        if (keyPath) {
            animator.keyPath = keyPath;
        }
        
        animator.toValue = @(value);
    }
    
    lv_pushUserdata(L, data);
    
    return 1;
}

static int alpha(lv_State *L) {
    return updateAnimator(L, @"opacity");
}

static int rotation(lv_State *L) {
    return updateAnimator(L, @"transform.rotation");
}

static int scale(lv_State *L) {
    return updateAnimator(L, @"transform.scale");
}

static int scaleX(lv_State *L) {
    return updateAnimator(L, @"transform.scale.x");
}

static int scaleY(lv_State *L) {
    return updateAnimator(L, @"transform.scale.y");
}

static int translation(lv_State *L) {
    return updateAnimator(L, @"transform.translation");
}

static int translationX(lv_State *L) {
    return updateAnimator(L, @"transform.translation.x");
}

static int translationY(lv_State *L) {
    return updateAnimator(L, @"transform.translation.y");
}

static int value(lv_State *L) {
    return updateAnimator(L, nil);
}

+(int)classDefine:(lv_State *)L {
    lv_pushcfunction(L, lvNewAnimator);
    lv_setglobal(L, "Animation");
    
    const struct lvL_reg memberFunctions[] = {
        { "__gc", __gc },
        { "__tostring", __tostring },
        { "__eq", __eq },
        
        { "clone", clone },
        
        { "with", with },
        { "start", start },
        { "duration", duration },
        { "delay", delay },
        { "repeatCount", repeatCount },
        { "reverses", autoreverses },
        
        { "callback", callback },
        { "onStart", onStart },
        { "onEnd", onEnd },
        { "onCancel", onCancel },

        { "alpha", alpha },
        { "rotation", rotation },
        { "scale", scale },
        { "scaleX", scaleX },
        { "scaleY", scaleY },
        { "translation", translation },
        { "translationX", translationX },
        { "translationY", translationY },
        { "value", value },
        
        { NULL, NULL}
    };
    
    lv_createClassMetaTable(L, META_TABLE_Animator);
    lvL_openlib(L, NULL, memberFunctions, 0);
    
    return 1;
}

- (instancetype)init {
    if (self = [super init]) {
        _autoreverses = YES;
    }
    
    return self;
}

- (instancetype)copyWithZone:(NSZone *)zone {
    LVAnimator *animator = [[[self class] allocWithZone:zone] init];
    animator.keyPath = [self.keyPath copy];
    animator.toValue = [self.toValue copy];

    animator.duration = self.duration;
    animator.delay = self.delay;
    animator.repeatCount = self.repeatCount;
    animator.autoreverses = self.autoreverses;

    animator.target = self.target;
    
    return animator;
}

- (instancetype)mutableCopyWithZone:(NSZone *)zone {
    return [self copyWithZone:zone];
}

- (NSUInteger)hash {
    return self.keyPath.hash;
}

- (BOOL)isEqual:(id)object {
    if (object == nil) {
        return NO;
    }
    if (![object isKindOfClass:[self class]]) {
        return NO;
    }
    
    LVAnimator *a1 = (LVAnimator *)object;
    return lv_objcEqual(self.keyPath, a1.keyPath) &&
        lv_objcEqual(self.toValue, a1.toValue) &&
        self.duration == a1.duration &&
        self.delay == a1.delay &&
        self.repeatCount == a1.repeatCount &&
        self.autoreverses == a1.autoreverses;
}

- (CABasicAnimation *)buildAnimation {
    CABasicAnimation *animation = [CABasicAnimation animationWithKeyPath:self.keyPath];
    animation.delegate = self;
    
    animation.fromValue = [self.target.layer valueForKeyPath:self.keyPath];
    
    if ([self.keyPath isEqualToString:@"transform.rotation"]) {
        animation.toValue = @(self.toValue.floatValue * M_PI / 180.0);
    } else {
        animation.toValue = self.toValue;
    }
    animation.duration = self.duration;
    
    animation.fillMode = @"both";
    
    if (self.repeatCount > 0) {
        animation.repeatCount = self.repeatCount;
        if (animation.autoreverses) {
            animation.repeatCount += 0.5;
        }
        animation.autoreverses = self.autoreverses;
    } else if (self.repeatCount < 0) {
        animation.repeatCount = HUGE_VALF;
        animation.autoreverses = self.autoreverses;
    }
    
    animation.beginTime = CACurrentMediaTime() + self.delay;
    
    return animation;
}

- (void)startWithKey:(NSString *)key {
    CABasicAnimation *animation = nil;
    
    if (self.target != nil && (animation = [self buildAnimation])) {
        [self.target.layer setValue:animation.toValue forKeyPath:animation.keyPath];
        [self.target.layer addAnimation:animation forKey:key];
    }
}

- (void)animationDidStart:(CAAnimation *)anim {
    lv_State* l = self.lvState;
    if( l && self.userData ){
        lv_pushUserdata(l, self.userData);
        lv_pushUDataRef(l, kLVAnimatorCallbackOnStart);
        lv_runFunction(l);
    }
}

- (void)animationDidStop:(CAAnimation *)anim finished:(BOOL)flag {
    CALayer *layer = self.target.layer;
    if (layer == nil) {
        return;
    }
    
    lv_State* l = self.lvState;
    if( l && self.userData ){
        lv_pushUserdata(l, self.userData);
        lv_pushUDataRef(l, flag ? kLVAnimatorCallbackOnEnd : kLVAnimatorCallbackOnCancel);
        lv_runFunction(l);
    }
}

@end
