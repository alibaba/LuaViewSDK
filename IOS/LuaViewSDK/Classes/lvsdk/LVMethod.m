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


@interface LVMethod ()
@property(nonatomic,strong) NSMethodSignature * methodSig;
@end

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

-(int) callObj:(id) obj args:(lua_State*)L{
    NSMethodSignature * sig = self.methodSig;
    if( sig==nil ){
        sig = [obj methodSignatureForSelector:self.sel];
        self.methodSig = sig;
    }
    if ( sig ) {
        NSInvocation * invocation = [NSInvocation invocationWithMethodSignature:sig];
        [invocation setTarget: obj]; // 传递 参数0: self
        [invocation setSelector: self.sel];// 传递 参数1: SEL
        
        NSInteger numberOfArguments = sig.numberOfArguments;
        int luaArgsNum = lua_gettop(L);
        [invocation retainArguments];
        for( int i=2; (i<numberOfArguments) && (i<=luaArgsNum);  i++ ){// 传递 参数2, 参数3, ...
            lv_setInvocationArgByLuaStack(invocation, i, L, i);
        }
        [invocation invoke];
        
        return lv_pushInvocationReturnValueToLuaStack(invocation, L);
    }
    LVError(@"Not found Method: %@.%@",[obj class], self.selName );
    return 0;
}

@end





