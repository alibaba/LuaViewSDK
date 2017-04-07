/**
  * Created by LuaView.
  * Copyright (c) 2017, Alibaba Group. All rights reserved.
  *
  * This source code is licensed under the MIT.
  * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
  */

#import <Foundation/Foundation.h>
#import "LVHeads.h"


//LVData
@interface LVData : NSObject<LVProtocal, LVClassProtocal>

@property(nonatomic,weak) LuaViewCore* lv_luaviewCore;
@property(nonatomic,assign) LVUserDataInfo* lv_userData;

@property(nonatomic,strong) NSMutableData* data;//真实的数据

-(id) init:(lua_State*) l;
+(int) lvClassDefine:(lua_State *)L globalName:(NSString*) globalName;

+(int) createDataObject:(lua_State *)L  data:(NSData*) data;

@end
