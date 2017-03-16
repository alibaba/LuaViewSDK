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
#import "LView.h"

@interface LVExGlobalFunc : NSObject<LVClassProtocal>

+(int) lvClassDefine:(lua_State *)L globalName:(NSString*) globalName;

+(void) registry:(lua_State*)L  window:(UIView*)window;

@end
