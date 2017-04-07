/**
  * Created by LuaView.
  * Copyright (c) 2017, Alibaba Group. All rights reserved.
  *
  * This source code is licensed under the MIT.
  * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
  */

#import <Foundation/Foundation.h>
#import "LVHeads.h"

#define LV_STRUCT_MAX_LEN 16

@interface LVStruct : NSObject<LVProtocal, LVClassProtocal>
@property(nonatomic,weak) LuaViewCore* lv_luaviewCore;
@property(nonatomic,assign) LVUserDataInfo* lv_userData;
- (id) lv_nativeObject; // 返回native对象


+(int) lvClassDefine:(lua_State *)L globalName:(NSString*) globalName;

+(int) pushStructToLua:(lua_State*)L data:(void*)data;

-(CGFloat*) dataPointer;

@end
