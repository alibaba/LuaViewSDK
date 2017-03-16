/**
  * Created by LuaView.
  * Copyright (c) 2017, Alibaba Group. All rights reserved.
  *
  * This source code is licensed under the MIT.
  * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
  */

#import <Foundation/Foundation.h>
#import "LVHeads.h"

extern void lv_printToServer(lua_State* L, const char* cs, int withTabChar);

@interface LVDebuger : NSObject<LVClassProtocal>

+(int) lvClassDefine:(lua_State *)L globalName:(NSString*) globalName;


@end
