//
//  LVPanGestureRecognizer.h
//  LVSDK
//
//  Created by dongxicheng on 1/22/15.
//  Copyright (c) 2015 dongxicheng. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "LVHeads.h"
#import "NSObject+LuaView.h"

@interface LVPanGesture : UIPanGestureRecognizer<LVProtocal, LVClassProtocal>

@property(nonatomic,weak) LuaViewCore* lv_luaviewCore;
@property(nonatomic,assign) LVUserDataInfo* lv_userData;

-(id) init:(lua_State*) l;


+(int) lvClassDefine:(lua_State *)L globalName:(NSString*) globalName;

@end
