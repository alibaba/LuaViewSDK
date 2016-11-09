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

@property (nonatomic,assign) SEL sel;
@property (nonatomic,copy)   NSString* selName;
@property (nonatomic,assign) NSInteger nargs;

-(id) initWithSel:(SEL)sel;

-(int) performMethodWithArgs:(lv_State*)L nativeObject:(id) nativeObject;

@end
