//
//  LVMethod.h
//  LVSDK
//
//  Created by dongxicheng on 4/24/15.
//  Copyright (c) 2015 dongxicheng. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "LVHeads.h"

@interface LVMethod : NSObject

@property (nonatomic,weak) id nativeObject;
@property (nonatomic,assign) SEL sel;
@property (nonatomic,copy) NSString* selectName; 
@property (nonatomic,assign) NSInteger nargs;

-(id) initWithNativeObject:(id) nativeObject sel:(SEL)sel;

-(int) performMethodWithArgs:(lv_State*)L;

@end
