/**
  * Created by LuaView.
  * Copyright (c) 2017, Alibaba Group. All rights reserved.
  *
  * This source code is licensed under the MIT.
  * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
  */

#import <Foundation/Foundation.h>
#import "LVHeads.h"

@interface LVSystem : NSObject<LVClassProtocal>

+(int) lvClassDefine:(lua_State *)L globalName:(NSString*) globalName;

/*
 * 获取网络状态, 需要重载API获取网络链接信息: 2g 3g 4g wifi 未知类型返回unkown 断网返回"none"
 */
+(NSString*) netWorkType;

@end
