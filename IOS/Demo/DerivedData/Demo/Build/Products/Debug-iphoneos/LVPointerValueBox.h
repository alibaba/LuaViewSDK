//
//  LValueBox.h
//  LVSDK
//  用处不大
//
//  Created by dongxicheng on 7/1/15.
//  Copyright (c) 2015 dongxicheng. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface LVPointerValueBox : NSObject

-(id) initWithPointer:(void*) pointer;

@property (nonatomic, assign) void* pointer;

@end
