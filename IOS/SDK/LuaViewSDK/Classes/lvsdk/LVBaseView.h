//
//  LVBaseView.h
//  JU
//
//  Created by dongxicheng on 12/29/14.
//  Copyright (c) 2014 ju.taobao.com. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "LVHeads.h"

@interface LVBaseView : UIView<LVProtocal, LVClassProtocal>

@property(nonatomic,weak) LuaViewCore* lv_luaviewCore;
@property(nonatomic,assign) LVUserDataInfo* lv_userData;
@property(nonatomic,assign) NSUInteger lv_align;

@property(nonatomic,strong) CAShapeLayer* lv_shapeLayer;
@property(nonatomic,assign) BOOL lv_canvas;

-(id) init:(lua_State*) l;

+(const luaL_Reg*) baseMemberFunctions;

+(int) lvClassDefine:(lua_State *)L globalName:(NSString*) globalName;

@end


/**
 * callback回调统一处理API
 */
extern int lv_setCallbackByKey(lua_State *L, const char* key, BOOL addGesture);
