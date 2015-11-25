//
//  LVResponse.h
//  LVSDK
//
//  Created by dongxicheng on 2/2/15.
//  Copyright (c) 2015 dongxicheng. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface LVHttpResponse : NSObject

@property(nonatomic,strong) NSURLResponse *response;
@property(nonatomic,strong) NSHTTPURLResponse *httpResponse;
@property(nonatomic,strong) NSMutableData *data;
@property(nonatomic,strong) NSError *error;

@end
