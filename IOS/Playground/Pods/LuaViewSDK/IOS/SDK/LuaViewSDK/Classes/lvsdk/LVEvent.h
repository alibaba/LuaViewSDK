/**
  * Created by LuaView.
  * Copyright (c) 2017, Alibaba Group. All rights reserved.
  *
  * This source code is licensed under the MIT.
  * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
  */

#import <UIKit/UIKit.h>
#import <Foundation/Foundation.h>
#import "LVHeads.h"

#define LVTouchEventType_DOWN    1010
#define LVTouchEventType_MOVE    1020
#define LVTouchEventType_CANCEL  1030
#define LVTouchEventType_UP      1040

@interface LVEvent : NSObject<LVProtocal, LVClassProtocal>

@property(nonatomic,weak) LuaViewCore* lv_luaviewCore;
@property(nonatomic,assign) LVUserDataInfo* lv_userData;

@property(nonatomic,weak) UIEvent* event;

@property(nonatomic,assign) NSInteger eventType;

@property(nonatomic,weak) UIGestureRecognizer* gesture;

-(id) init:(lua_State *)l gesture:(UIGestureRecognizer*) gesture;

+(int) lvClassDefine:(lua_State *)L globalName:(NSString*) globalName;

+(LVEvent*) createLuaEvent:(lua_State *)L  event:(UIEvent*) event gesture:(UIGestureRecognizer*) gesture;

@end
