/**
  * Created by LuaView.
  * Copyright (c) 2017, Alibaba Group. All rights reserved.
  *
  * This source code is licensed under the MIT.
  * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
  */

#import <UIKit/UIKit.h>
#import "LVHeads.h"

@interface LVBaseView : UIView<LVProtocal, LVClassProtocal>

@property(nonatomic,weak) LuaViewCore* lv_luaviewCore;
@property(nonatomic,assign) LVUserDataInfo* lv_userData;
@property(nonatomic,assign) NSUInteger lv_align;

@property(nonatomic,strong) CAShapeLayer* lv_shapeLayer;
@property(nonatomic,assign) BOOL lv_canvas;
@property(nonatomic,strong) NSString * lv_identifier;

-(id) init:(lua_State*) l;

+(const luaL_Reg*) baseMemberFunctions;

+(int) lvClassDefine:(lua_State *)L globalName:(NSString*) globalName;

@end


/**
 * callback回调统一处理API
 */
extern int lv_setCallbackByKey(lua_State *L, const char* key, BOOL addGesture);
