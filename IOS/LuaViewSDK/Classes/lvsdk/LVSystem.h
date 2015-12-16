//
//  LVSystem.h
//  LVSDK
//
//  Created by dongxicheng on 1/15/15.
//  Copyright (c) 2015 dongxicheng. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "LVHeads.h"

@interface LVSystem : NSObject

+(int) classDefine:(lv_State *)L ;

/*
 * 获取网络状态, 需要重载API获取网络链接信息: 2g 3g 4g wifi 未知类型返回unkown 断网返回"none"
 */
+(NSString*) netWorkType;

@end
