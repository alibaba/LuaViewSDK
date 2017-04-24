/**
  * Created by LuaView.
  * Copyright (c) 2017, Alibaba Group. All rights reserved.
  *
  * This source code is licensed under the MIT.
  * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
  */

#import <Foundation/Foundation.h>

#define NW_NETWOEK_STATUS_NOTIFY @"TBNetworkStatusChangeNotify"

typedef enum {
    NotReachable = 0,
    ReachableViaWiFi,
    ReachableVia2G,
    ReachableVia3G,
    ReachableVia4G
} LVNetworkStatusEnum;

@interface LVNetworkStatus : NSObject

+ (LVNetworkStatus *)shareInstance;

- (LVNetworkStatusEnum)currentNetworkStatus;

- (LVNetworkStatusEnum)preNetworkStatus;

- (NSString *)currentNetworkStatusString;




@end
