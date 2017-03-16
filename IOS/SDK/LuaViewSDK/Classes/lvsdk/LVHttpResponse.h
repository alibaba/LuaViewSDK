/**
  * Created by LuaView.
  * Copyright (c) 2017, Alibaba Group. All rights reserved.
  *
  * This source code is licensed under the MIT.
  * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
  */

#import <Foundation/Foundation.h>

@interface LVHttpResponse : NSObject

@property(nonatomic,strong) NSURLResponse *response;
@property(nonatomic,strong) NSHTTPURLResponse *httpResponse;
@property(nonatomic,strong) NSMutableData *data;
@property(nonatomic,strong) NSError *error;

@end
