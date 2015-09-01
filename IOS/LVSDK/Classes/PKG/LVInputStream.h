//
//  LVInputStream.h
//  LVSDK
//
//  Created by dongxicheng on 5/4/15.
//  Copyright (c) 2015 dongxicheng. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface LVInputStream : NSObject

-(id) initWithData:(NSData*) data;

-(int) readInt;
-(NSString*) readUTF;
-(NSData*) readData:(NSInteger) length;

@end
