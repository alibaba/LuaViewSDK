//
//  LVResponse.m
//  LVSDK
//
//  Created by dongxicheng on 2/2/15.
//  Copyright (c) 2015 dongxicheng. All rights reserved.
//

#import "LVHttpResponse.h"

@implementation LVHttpResponse

-(id) initWithResponse:(NSURLResponse*)response data:(NSData*)data error:(NSError*)error{
    self = [super init];
    if( self ){
        self.response = response;
        if( [response isKindOfClass:[NSHTTPURLResponse class]] ){
            self.httpResponse = (NSHTTPURLResponse*)response;
        }
        self.data = data;
        self.error = error;
    }
    return self;
}
@end
