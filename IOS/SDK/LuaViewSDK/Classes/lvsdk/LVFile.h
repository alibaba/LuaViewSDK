/**
  * Created by LuaView.
  * Copyright (c) 2017, Alibaba Group. All rights reserved.
  *
  * This source code is licensed under the MIT.
  * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
  */
#import <Foundation/Foundation.h>
#import "LVHeads.h"

@interface LVFile : NSObject<LVClassProtocal>

+(int) lvClassDefine:(lua_State *)L globalName:(NSString*) globalName;

@end
