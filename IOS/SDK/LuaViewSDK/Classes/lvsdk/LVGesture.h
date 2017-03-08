//
//  LVGesture.h
//  LVSDK
//
//  Created by dongxicheng on 1/22/15.
//  Copyright (c) 2015 dongxicheng. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "LVHeads.h"
#import "UIGestureRecognizer+LuaView.h"

@class LVGesture;

typedef void(^LVGestureOnTouchEventCallback)(LVGesture* gesture, int argN);

@interface LVGesture : UIGestureRecognizer<LVClassProtocal,LVClassProtocal,UIGestureRecognizerDelegate>

@property(nonatomic,weak) LuaViewCore* lv_lview;
@property(nonatomic,assign) LVUserDataInfo* lv_userData;

@property(nonatomic,copy) LVGestureOnTouchEventCallback onTouchEventCallback;

-(id) init:(lua_State*) l;

+(const luaL_Reg*) baseMemberFunctions;

+(void) releaseUD:(LVUserDataInfo *) user;

+(int) lvClassDefine:(lua_State *)L globalName:(NSString*) globalName;


@end
