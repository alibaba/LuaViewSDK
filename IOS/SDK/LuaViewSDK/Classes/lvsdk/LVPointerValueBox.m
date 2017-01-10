//
//  LValueBox.m
//  LVSDK
//
//  Created by dongxicheng on 7/1/15.
//  Copyright (c) 2015 dongxicheng. All rights reserved.
//

#import "LVPointerValueBox.h"

@implementation LVPointerValueBox

-(id) initWithPointer:(void*) pointer{
    self = [super init];
    if ( self ){
        self.pointer = pointer;
    }
    return self;
}


@end
