/**
  * Created by LuaView.
  * Copyright (c) 2017, Alibaba Group. All rights reserved.
  *
  * This source code is licensed under the MIT.
  * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
  */

#import <objc/message.h>
#import "LVMethod.h"
#import "LVHeads.h"
#import "LVTypeConvert.h"


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





