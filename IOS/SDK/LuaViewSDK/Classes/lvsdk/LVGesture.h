/**
  * Created by LuaView.
  * Copyright (c) 2017, Alibaba Group. All rights reserved.
  *
  * This source code is licensed under the MIT.
  * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
  */

#import <Foundation/Foundation.h>
#import "LVHeads.h"
#import "NSObject+LuaView.h"

@class LVGesture;

typedef void(^LVGestureOnTouchEventCallback)(LVGesture* gesture, int argN);

@interface LVGesture : UIGestureRecognizer<LVClassProtocal,LVClassProtocal,UIGestureRecognizerDelegate>

@property(nonatomic,weak) LuaViewCore* lv_luaviewCore;
@property(nonatomic,assign) LVUserDataInfo* lv_userData;

@property(nonatomic,copy) LVGestureOnTouchEventCallback onTouchEventCallback;

-(id) init:(lua_State*) l;

+(const luaL_Reg*) baseMemberFunctions;

+(void) releaseUD:(LVUserDataInfo *) user;

+(int) lvClassDefine:(lua_State *)L globalName:(NSString*) globalName;


@end
