//
//  LVMethod.m
//  LVSDK
//
//  Created by dongxicheng on 4/24/15.
//  Copyright (c) 2015 dongxicheng. All rights reserved.
//

#import <objc/message.h>
#import "LVMethod.h"
#import "LVHeads.h"
#import "LVTypeConvert.h"
#import "lVapi.h"

@implementation LVMethod

-(id) initWithSel:(SEL)sel{
    self = [super init];
    if( self ){
        self.sel = sel;
        self.selName = NSStringFromSelector(sel);
        self.nargs = [self checkSelectorArgsNumber:NSStringFromSelector(sel)]; 
    }
    return self;
}

-(NSInteger) checkSelectorArgsNumber:(NSString*)s{
    NSInteger num = 0;
    for( int i=0; i<s.length; i++){
        if( [s characterAtIndex:i]==':'){
            num ++;
        }
    }
    return num;
}

-(int) callObj:(id) obj args:(lv_State*)L{
    NSMethodSignature * sig = [obj methodSignatureForSelector:self.sel];
    if ( sig ) {
        NSInvocation * invocation = [NSInvocation invocationWithMethodSignature:sig];
        [invocation setTarget: obj]; // 传递 参数0: self
        [invocation setSelector: self.sel];// 传递 参数1: SEL
        
        NSInteger numberOfArguments = sig.numberOfArguments;
        int luaArgsNum = lv_gettop(L);
        [invocation retainArguments];
        for( int i=2; (i<numberOfArguments) && (i<=luaArgsNum);  i++ ){// 传递 参数2, 参数3, ...
            [LVTypeConvert setIvocation:invocation argIndex:i withLua:L stackID:i];
        }
        [invocation invoke];
        
        return [LVTypeConvert pushInvocationReturnValue:invocation toLua:L];
    }
    LVError(@"Not found Method: %@.%@",[obj class], self.selName );
    return 0;
}

@end





