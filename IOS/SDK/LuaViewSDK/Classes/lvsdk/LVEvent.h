//
//  LVEvent.h
//  LuaViewSDK
//
//  Created by 董希成 on 2016/12/9.
//  Copyright © 2016年 dongxicheng. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <Foundation/Foundation.h>
#import "LVHeads.h"

#define LVTouchEventType_DOWN    1010
#define LVTouchEventType_MOVE    1020
#define LVTouchEventType_CANCEL  1030
#define LVTouchEventType_UP      1040

@interface LVEvent : NSObject<LVProtocal, LVClassProtocal>

@property(nonatomic,weak) LView* lv_lview;
@property(nonatomic,assign) LVUserDataInfo* lv_userData;

@property(nonatomic,weak) UIEvent* event;

@property(nonatomic,assign) NSInteger eventType;

@property(nonatomic,weak) UIGestureRecognizer* gesture;

-(id) init:(lua_State *)l gesture:(UIGestureRecognizer*) gesture;

+(int) lvClassDefine:(lua_State *)L globalName:(NSString*) globalName;

+(LVEvent*) createLuaEvent:(lua_State *)L  event:(UIEvent*) event gesture:(UIGestureRecognizer*) gesture;

@end
